package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporter;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;

import java.util.HashMap;
import java.util.Map;

import static io.opentelemetry.semconv.ServiceAttributes.SERVICE_NAME;
import static io.opentelemetry.semconv.incubating.ServiceIncubatingAttributes.SERVICE_INSTANCE_ID;

public class TrackAdvancedException {

    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final Logger log4jLogger = LogManager.getLogger(TrackAdvancedException.class);

    public static void main(String[] args) throws InterruptedException {
        OpenTelemetry openTelemetry = initOpenTelemetry();

        OpenTelemetryAppender.install(openTelemetry);

        trackAdvancedException();
        Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
    }

    private static void trackAdvancedException() {
        // traceIdHex must be a 32-hex-character lowercase string
        // spanIdHex must be a 16-hex-character lowercase string
        // if traceIdHex or spanIdHex is invalid, it will result to SpanContext.INVALID
        // only when traceIdHex and spanIdHex are valid, operation_Id and operation_ParentId will get stamped onto the advanced exception
        // in this example, "ff01020304050600ff0a0b0c0d0e0f00" is your operation_id and "090a0b0c0d0e0f00" is your operation_SpanId
        SpanContext spanContext = SpanContext.create("ff01020304050600ff0a0b0c0d0e0f00", "090a0b0c0d0e0f00", TraceFlags.getSampled(), TraceState.getDefault());
        try (Scope ignored = Span.wrap(spanContext).makeCurrent()) {
            Map<String, Object> mapMessage = new HashMap<>();
            mapMessage.put("key", "trackAdvancedException"); // add a custom dimension
            mapMessage.put("message", "This is an exception with custom stack trace from log4j2");
            log4jLogger.error(new MapMessage<>(mapMessage), new AdvancedException("my exception"));
        }
    }

    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder()
            .addResourceCustomizer((resource, configProperties) ->
                resource.merge(Resource.getDefault().toBuilder()
                    .put(SERVICE_NAME, "my cloud role name")
                    .put(SERVICE_INSTANCE_ID, "my cloud instance id")
                    .build()));
        AzureMonitorExporter.customize(sdkBuilder, CONNECTION_STRING);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
