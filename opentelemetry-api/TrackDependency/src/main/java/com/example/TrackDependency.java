package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackDependency {

  private static final String CONNECTION_STRING = "<Your Connection String>";
  private static final Logger logger = LoggerFactory.getLogger(TrackDependency.class);
  private static final Tracer spanTracer = initTracer();

  public static void main(String[] args) throws InterruptedException {
    track();
    Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
  }

  private static void track() {
    Span span = spanTracer.spanBuilder("trackDependency").setSpanKind(SpanKind.CLIENT).startSpan();
    // Make the span the current span
    try (Scope scope = span.makeCurrent()) {
      span.setAttribute("http.method", "GET");
      span.setAttribute("http.url", "https://www.google.com/");
      logger.info("track a custom dependency");
    } finally {
      span.end();
    }
  }

  private static Tracer initTracer() {
    // Create Azure Monitor exporter and configure OpenTelemetry tracer to use this exporter
    // This should be done just once when application starts up
    SpanExporter exporter = new AzureMonitorExporterBuilder()
        .connectionString(CONNECTION_STRING)
        .buildTraceExporter();

    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
        .build();

    OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .buildAndRegisterGlobal();

    return sdk.getTracer("TrackDependency");
  }
}