package com.example;

import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.MapMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

import static io.opentelemetry.semconv.ServiceAttributes.SERVICE_NAME;

public class TrackLog4j2 {

    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final AttributeKey<String> SERVICE_INSTANCE_ID = AttributeKey.stringKey("service.instance.id");

    private static final org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger("log4j-logger");
    private static final org.slf4j.Logger slf4j_2_Logger = LoggerFactory.getLogger(TrackLog4j2.class);

    public static void main(String[] args) throws InterruptedException {
        OpenTelemetry openTelemetry = initOpenTelemetry();

        OpenTelemetryAppender.install(openTelemetry);

        // track a trace using log4j2
        track();
        Thread.sleep(6000); // wait at least 5 seconds to give batch LogRecord processor time to export

        // track a trace using log4j2 and slf4j-2 logger
        trackWithSlf4j_2();
        Thread.sleep(8000); // wait at least 5 seconds to give batch LogRecord processor time to export
    }

    /**
     * track with Log4j2
     */
    private static void track() {
        // Log using log4j2 API
        Map<String, Object> mapMessage = new HashMap<>();
        mapMessage.put("key", "track");
        mapMessage.put("message", "track - it's a log4j2 message with custom attributes");
        runWithASpan(() -> log4jLogger.warn(new MapMessage<>(mapMessage)), true);
        ThreadContext.clearAll();
        runWithASpan(() -> log4jLogger.error("track - a log4j2 log message without custom attributes"), false);
    }

    /**
     * track with log4j2 using slf4j-2 logger
     */
    private static void trackWithSlf4j_2() {
        // Log using slf4j logger with log4j2
        runWithASpan(
            () ->
                slf4j_2_Logger
                    .atWarn()
                    .setMessage("trackWithSlf4j_2 - a slf4j log message with custom attributes")
                    .addKeyValue("key", "trackWithSlf4j_2")
                    .log(), true);

        runWithASpan(() -> slf4j_2_Logger.error("trackWithSlf4j_2 - a slf4j_2 log message without custom attributes"), false);
    }

    /**
     * initialize OpenTelemetry using Azure Monitor OpenTelemetry Exporter
     */
    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder()
            .addResourceCustomizer((resource, configProperties) ->
                resource.merge(Resource.getDefault().toBuilder()
                    .put(SERVICE_NAME, "my cloud role name")
                    .put(SERVICE_INSTANCE_ID, "my cloud instance id")
                    .build()));
        AzureMonitorAutoConfigure.customize(sdkBuilder, CONNECTION_STRING);
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
            MDC.remove("MDC key");
        } finally {
            span.end();
        }
    }
}
