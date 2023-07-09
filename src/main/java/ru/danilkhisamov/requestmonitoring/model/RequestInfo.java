package ru.danilkhisamov.requestmonitoring.model;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import lombok.Data;

@Data
public class RequestInfo {
    private Instant timeframeStart;
    private Instant timeframeEnd;
    private int requestCount;

    public RequestInfo(long timeframeDuration, TemporalUnit unit) {
        initialize(timeframeDuration, unit);
    }

    public synchronized void initialize(long timeframeDuration, TemporalUnit unit) {
        this.timeframeStart = Instant.now();
        this.timeframeEnd = timeframeStart.plus(timeframeDuration, unit);
        this.requestCount = 0;
    }

    public synchronized void incrementRequestCount() {
        requestCount++;
    }
}
