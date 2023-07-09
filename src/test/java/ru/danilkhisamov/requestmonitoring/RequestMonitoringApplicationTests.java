package ru.danilkhisamov.requestmonitoring;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.danilkhisamov.requestmonitoring.config.RequestLimitConfigurationProperties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestMonitoringApplicationTests {

    private final MockMvc mockMvc;
    private final RequestLimitConfigurationProperties limitConfigurationProperties;

    @Test
    @SneakyThrows
    public void randomLoadTest() {
        var random = new Random();
        var threadPoolSize = 10;
        var requestsMin = 10;
        var requestsMax = 30;
        var timeoutMillisMin = 300;
        var timeoutMillisMax = 5000;

        var executionService = Executors.newScheduledThreadPool(threadPoolSize);

        var requestsList = IntStream.range(0, threadPoolSize)
                .mapToObj(i -> random.nextInt(requestsMin, requestsMax))
                .toList();

        var totalRequests = requestsList.stream()
                .mapToInt(i -> i)
                .sum();

        var countDownLatch = new CountDownLatch(totalRequests);


        requestsList
                .stream()
                .map(requests -> new ClientEmulator(
                        requests,
                        random.nextLong(timeoutMillisMin, timeoutMillisMax),
                        countDownLatch,
                        executionService)
                )
                .forEach(ce -> executionService.schedule(ce::sendOperationRequest, ce.timeoutMillis, TimeUnit.MILLISECONDS));

        countDownLatch.await();
    }

    private class ClientEmulator {
        private final String ip;
        private final long timeoutMillis;
        private final CountDownLatch countDownLatch;
        private final ScheduledExecutorService executorService;
        private int totalRequests;
        private int currentRequest;
        private Instant timeframeStart;
        private Instant timeframeEnd;

        public ClientEmulator(int totalRequests, long timeoutMillis, CountDownLatch countDownLatch, ScheduledExecutorService executorService) {
            this.ip = generateIP();
            this.timeoutMillis = timeoutMillis;
            this.countDownLatch = countDownLatch;
            this.executorService = executorService;
            this.totalRequests = totalRequests;
            this.currentRequest = 0;
        }

        @SneakyThrows
        public void sendOperationRequest() {
            if (timeframeStart == null || timeframeEnd != null && timeframeEnd.isBefore(Instant.now())) {
                timeframeStart = Instant.now();
                timeframeEnd = timeframeStart.plus(limitConfigurationProperties.getMinutes(), ChronoUnit.MINUTES);
                currentRequest = 0;
            }

            mockMvc
                    .perform(get("/operation").with(remoteHost(ip)))
                    .andExpect(currentRequest++ < limitConfigurationProperties.getRequests() ? status().isOk() : status().isBadGateway());
            totalRequests--;
            countDownLatch.countDown();

            if (totalRequests > 0) {
                executorService.schedule(this::sendOperationRequest, timeoutMillis, TimeUnit.MILLISECONDS);
            }
        }

        private static RequestPostProcessor remoteHost(final String remoteAddress) {
            return request -> {
                request.setRemoteAddr(remoteAddress);
                return request;
            };
        }

        private static String generateIP() {
            return new Random()
                    .ints(4, 0, 255)
                    .boxed()
                    .map(Object::toString)
                    .collect(Collectors.joining("."));
        }
    }

}
