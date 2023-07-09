package ru.danilkhisamov.requestmonitoring.service;

import jakarta.servlet.http.HttpServletRequest;

public interface DemoLimitService {
    void doSomething(HttpServletRequest request);
}
