package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporter;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackDependency {

    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();
    private static final Tracer tracer = openTelemetry.getTracer("your tracer name");

    public static void main(String[] args) throws InterruptedException {
        track();
        Thread.sleep(8000); // wait at least 5 seconds to give batch span processor time to export
    }

    private static void track() {
        Span span = tracer.spanBuilder("your dependency name")
            .setSpanKind(SpanKind.CLIENT)
            .setAttribute("http.method", "GET")
            .setAttribute("http.url", "https://www.google.com/")
            .startSpan();
        try (Scope ignored = span.makeCurrent()) {
            // make the dependency call
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }

    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        AzureMonitorExporter.customize(sdkBuilder, CONNECTION_STRING);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
