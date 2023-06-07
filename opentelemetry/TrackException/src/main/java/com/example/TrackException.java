package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackException {

  private static final Logger log4jLogger = LogManager.getLogger("log4j-logger");
  protected static final Tracer spanTracer = initTracer();

  public static void main(String[] args) throws InterruptedException {
    track();
    Thread.sleep(2000);

    trackWithLog4j2();
    Thread.sleep(2000);
  }

  private static void track() {
    Span span = spanTracer.spanBuilder("track").startSpan();
    // put the span into the current Context
    try (Scope scope = span.makeCurrent()) {
      throw new RuntimeException("intentionally throws an exception");
    } catch (Throwable throwable) {
      span.setStatus(StatusCode.ERROR, "Something bad happened!");
      span.recordException(throwable);
    } finally {
      span.end(); // Cannot set a span after this call
    }
  }

  static void trackWithLog4j2() {
    Span span = spanTracer.spanBuilder("trackWithLog4j2").startSpan();
    // put the span into the current Context
    try (Scope scope = span.makeCurrent()) {
      log4jLogger.error("This is an exception from log4j2");
    } catch (Throwable throwable) {
      span.setStatus(StatusCode.ERROR, "Something bad happened!");
      span.recordException(throwable);
    } finally {
      span.end(); // Cannot set a span after this call
    }
  }

  private static Tracer initTracer() {
    // Create Azure Monitor exporter and configure OpenTelemetry tracer to use this exporter
    // This should be done just once when application starts up
    SpanExporter exporter = new AzureMonitorExporterBuilder()
        .connectionString("<APPLICATION_INSIGHTS_CONNECTION_STRING>")
        .buildTraceExporter();

    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(SimpleSpanProcessor.create(exporter))
        .build();

    OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .buildAndRegisterGlobal();

    return sdk.getTracer("TrackException");
  }
}