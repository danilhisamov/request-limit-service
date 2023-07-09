package ru.danilkhisamov.requestmonitoring.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.danilkhisamov.requestmonitoring.aop.RequestLimited;
import ru.danilkhisamov.requestmonitoring.service.DemoLimitService;

@Service
@Slf4j
public class DemoLimitServiceImpl implements DemoLimitService {

    @Override
    @RequestLimited
    public void doSomething(HttpServletRequest request) {
        log.info("do something...");
    }
}
