package com.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws InterruptedException {
        Tracer tracer = GlobalOpenTelemetry.getTracer("TelemetryFilteredBaseOnSpanEvents", "1.0-SNAPSHOT");
        Span span = tracer.spanBuilder("mySpan").startSpan(); // create a span
        span.addEvent("Status code 404"); // add an event to the span
        try (Scope scope = span.makeCurrent()) {
            logger.debug("Hello world!");
            Thread.sleep(10000);
        } finally {
            span.end(); // end the span
        }
    }
}
