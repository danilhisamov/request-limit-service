package ru.danilkhisamov.requestmonitoring.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.danilkhisamov.requestmonitoring.config.RequestLimitConfigurationProperties;
import ru.danilkhisamov.requestmonitoring.model.RequestInfo;
import ru.danilkhisamov.requestmonitoring.service.RequestLimitService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestLimitServiceImpl implements RequestLimitService {
    private final ConcurrentHashMap<String, RequestInfo> requestData = new ConcurrentHashMap<>();
    private final RequestLimitConfigurationProperties limitConfigurationProperties;

    public boolean isAllowedToSendRequests(String ipMethodId) {
        requestData.putIfAbsent(ipMethodId, new RequestInfo(limitConfigurationProperties.getMinutes(), ChronoUnit.MINUTES));
        final var info = requestData.get(ipMethodId);

        synchronized (info) {
            if (Instant.now().isAfter(info.getTimeframeEnd())) {
                info.initialize(limitConfigurationProperties.getMinutes(), ChronoUnit.MINUTES);
            }

            if (info.getRequestCount() >= limitConfigurationProperties.getRequests()) {
                log.warn("IP-METHOD: {} is exceeded request limit", ipMethodId);
                return false;
            }

            info.incrementRequestCount();

            log.info("IP-METHOD: {} request count: {}", ipMethodId, info.getRequestCount());
            return true;
        }
    }

    @Scheduled(fixedDelayString = "#{${application.limit.minutes} * 2}", timeUnit = TimeUnit.MINUTES)
    public void cleanExpiredRequestInfo() {
        log.info("Cleaning requestData...");
        var now = Instant.now();
        requestData.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getTimeframeEnd()));
        log.info("Cleaning requestData is FINISHED");
    }
}
