package ru.danilkhisamov.requestmonitoring.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.danilkhisamov.requestmonitoring.exception.RequestLimitExceedException;
import ru.danilkhisamov.requestmonitoring.service.impl.RequestLimitServiceImpl;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLimitAspect {
    private final RequestLimitServiceImpl requestMonitoringService;

    @Before("@annotation(ru.danilkhisamov.requestmonitoring.aop.RequestLimited)")
    public void beforeAdvice(JoinPoint joinPoint) {
        var method = joinPoint.toString();
        var ip = Arrays.stream(joinPoint.getArgs())
                .filter(obj -> obj instanceof HttpServletRequest)
                .map(obj -> (HttpServletRequest) obj)
                .findFirst()
                .orElseThrow()
                .getRemoteAddr();
        if (!requestMonitoringService.isAllowedToSendRequests(ip + ":" + method)) {
            throw new RequestLimitExceedException(
                    String.format(
                            "Method[%s] invocations for IP[%s] is exceeded",
                            method,
                            ip
                    )
            );
        }
    }
}
