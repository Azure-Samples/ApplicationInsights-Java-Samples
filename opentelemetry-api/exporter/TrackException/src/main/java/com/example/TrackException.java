package com.example;

import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackException {

    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final Logger log4jLogger = LogManager.getLogger("log4j-logger");
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();
    private static final Tracer tracer = openTelemetry.getTracer("my tracer");

    public static void main(String[] args) throws InterruptedException {
        track();
        Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export

        trackWithLog4j2();
        Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
    }

    private static void track() {
        Span span = tracer.spanBuilder("TrackException").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            throw new RuntimeException("intentionally throws an exception");
        } catch (Throwable throwable) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(throwable);
        } finally {
            span.end(); // Cannot set a span after this call
        }
    }

    static void trackWithLog4j2() {
        log4jLogger.error("This is an exception from log4j2", new Exception("my exception"));
    }

    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        AzureMonitorAutoConfigure.customize(sdkBuilder, CONNECTION_STRING);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}