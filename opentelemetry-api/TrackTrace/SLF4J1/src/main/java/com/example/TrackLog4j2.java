package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.implementation.ResourceAttributes;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TrackLog4j2 {

  private static final String CONNECTION_STRING = "<Your Connection String>";
  private static final org.slf4j.Logger slf4j_1_Logger = LoggerFactory.getLogger(TrackLog4j2.class);

  public static void main(String[] args) throws InterruptedException {
    initOpenTelemetry();

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
  private static void initOpenTelemetry() {
    LogRecordExporter logRecordExporter = new AzureMonitorExporterBuilder()
        .connectionString(CONNECTION_STRING)
        .buildLogRecordExporter();
    OpenTelemetrySdk sdk =
        OpenTelemetrySdk.builder()
            .setTracerProvider(SdkTracerProvider.builder().setSampler(Sampler.alwaysOn()).build())
            .setLoggerProvider(
                SdkLoggerProvider.builder()
                    .setResource(
                        Resource.getDefault().toBuilder()
                            .put(ResourceAttributes.SERVICE_NAME, "my cloud role name")
                            .put(ResourceAttributes.SERVICE_INSTANCE_ID, "my cloud role instance")
                            .build())
                    .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(logRecordExporter).build())
                    .build())
            .build();
    GlobalOpenTelemetry.set(sdk);
    GlobalLoggerProvider.set(sdk.getSdkLoggerProvider());
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
