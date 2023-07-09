package ru.danilkhisamov.requestmonitoring.exception;

public class RequestLimitExceedException extends RuntimeException {
    public RequestLimitExceedException(String message) {
        super(message);
    }
}
