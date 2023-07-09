package ru.danilkhisamov.requestmonitoring.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.danilkhisamov.requestmonitoring.aop.RequestLimited;
import ru.danilkhisamov.requestmonitoring.service.DemoLimitService;

@Slf4j
@RestController
@RequestMapping("/operation")
@RequiredArgsConstructor
public class OperationController {
    private final DemoLimitService demoLimitService;

    @GetMapping
    @RequestLimited
    public ResponseEntity<Void> getOperation(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/demo-service")
    public void getOperationService(HttpServletRequest request) {
        demoLimitService.doSomething(request);
    }
}
