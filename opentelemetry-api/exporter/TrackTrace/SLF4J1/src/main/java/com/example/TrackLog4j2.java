package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.implementation.ResourceAttributes;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.resources.Resource;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TrackLog4j2 {

    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final org.slf4j.Logger slf4j_1_Logger = LoggerFactory.getLogger(TrackLog4j2.class);

    public static void main(String[] args) throws InterruptedException {
        OpenTelemetry openTelemetry = initOpenTelemetry();

        OpenTelemetryAppender.install(openTelemetry);

        // track a trace using log4j2 and slf4j_1 logger
        trackWithSlf4j_1();
        Thread.sleep(8000); // wait at least 5 seconds to give batch LogRecord processor time to export
    }

    /**
     * track with log4j2 using slf4j logger
     */
    private static void trackWithSlf4j_1() {
        // Log using slf4j_1 logger with log4j2
        runWithASpan(
            () -> slf4j_1_Logger.warn("trackWithSlf4j_1 - a slf4j_1 log message with MDC"), true);

        runWithASpan(() -> slf4j_1_Logger.error("trackWithSlf4j_1 - a slf4j_1 log message without MDC"), false);
    }

    /**
     * initialize OpenTelemetry using Azure Monitor OpenTelemetry Exporter
     */
    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder()
            .addResourceCustomizer((resource, configProperties) ->
                resource.merge(Resource.getDefault().toBuilder()
                    .put(ResourceAttributes.SERVICE_NAME, "my cloud role name")
                    .put(ResourceAttributes.SERVICE_INSTANCE_ID, "my cloud instance id")
                    .build()));
        new AzureMonitorExporterBuilder()
            .connectionString(CONNECTION_STRING)
            .install(sdkBuilder);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }

    static void runWithASpan(Runnable runnable, boolean withSpan) {
        if (!withSpan) {
            runnable.run();
            return;
        }
        Span span = GlobalOpenTelemetry.getTracer("my tracer name").spanBuilder("my span name").startSpan();
        try (Scope ignore = span.makeCurrent()) {
            MDC.put("MDC key", "MDC value");
            runnable.run();
        } finally {
            span.end();
            MDC.remove("MDC key");
        }
    }
}
