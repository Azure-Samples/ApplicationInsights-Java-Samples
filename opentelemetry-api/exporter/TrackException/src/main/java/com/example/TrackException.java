package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackException {

  private static final String CONNECTION_STRING = "<Your Connection String>";
  private static final Logger log4jLogger = LogManager.getLogger("log4j-logger");
  protected static final Tracer tracer = initTracer();

  public static void main(String[] args) throws InterruptedException {
    track();
    Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export

    trackWithLog4j2();
    Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
  }

  private static void track() {
    Span span = tracer.spanBuilder("TrackException").startSpan();
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
    log4jLogger.error("This is an exception from log4j2", new Exception("my exception"));
  }

  private static Tracer initTracer() {
    // Create Azure Monitor spanExporter and configure OpenTelemetry tracer to use this spanExporter
    // This should be done just once when application starts up
    SpanExporter spanExporter = new AzureMonitorExporterBuilder()
        .connectionString(CONNECTION_STRING)
        .buildTraceExporter();

    LogRecordExporter logRecordExporter = new AzureMonitorExporterBuilder()
        .connectionString(CONNECTION_STRING)
        .buildLogRecordExporter();

    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
        .build();

      // In this example Log4j2 log events will be sent to both the console appender and
      // the `OpenTelemetryAppender`, which will drop the logs until `GlobalLoggerProvider.set(..)` is
      // called. Once initialized, logs will be emitted to a `Logger` obtained from the `SdkLoggerProvider`.
      SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
          .addLogRecordProcessor(BatchLogRecordProcessor.builder(logRecordExporter).build())
          .build();

    OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setLoggerProvider(sdkLoggerProvider)
        .build();

    return sdk.getTracer("my tracer name");
  }
}