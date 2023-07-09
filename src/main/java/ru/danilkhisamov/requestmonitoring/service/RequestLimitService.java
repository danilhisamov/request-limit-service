package ru.danilkhisamov.requestmonitoring.service;

public interface RequestLimitService {
    boolean isAllowedToSendRequests(String ip);
}
