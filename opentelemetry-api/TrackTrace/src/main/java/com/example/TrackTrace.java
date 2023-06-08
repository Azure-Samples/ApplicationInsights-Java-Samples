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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.MapMessage;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TrackTrace {

  private static final String CONNECTION_STRING = "<Your Connection String>";
  private static final org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger("log4j-logger");
  private static final org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger("slf4j-logger");

  public static void main(String[] args) throws InterruptedException {
    initOpenTelemetry();

    // track a trace
    trackWithLogback();
    Thread.sleep(2000); // wait 2 sec

    // track a trace using log4j2
    trackWithLog4j2();
    Thread.sleep(2000); // wait 2 sec
  }

  /**
   * track with Logback using sl4j
   */
  private static void trackWithLogback() {
    // Log using slf4j API w/ logback backend
    runWithASpan(
        () ->
            slf4jLogger
                .atInfo()
                .setMessage("trackWithLogback - a slf4j log message with custom attributes")
                .addKeyValue("key", "trackWithLogback")
                .log(), true);
    runWithASpan(() -> slf4jLogger.info("trackWithLogback - a slf4j log message 2 without custom attributes"), false);
  }

  /**
   * track with Log4j2
   */
  private static void trackWithLog4j2() {
    // Log using log4j2 API
    Map<String, Object> mapMessage = new HashMap<>();
    mapMessage.put("key", "trackWithLog4j2");
    mapMessage.put("message", "trackWithLog4j2 - it's a log4j2 message with custom attributes");
    runWithASpan(() -> log4jLogger.info(new MapMessage<>(mapMessage)), true);
    ThreadContext.clearAll();
    runWithASpan(() -> log4jLogger.info("trackWithLog4j2 - a log4j log message without custom attributes"), false);
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
                            .put(ResourceAttributes.SERVICE_NAME, "track-trace-example")
                            .build())
                    .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(logRecordExporter).build())
                    .build())
            .build();
    GlobalOpenTelemetry.set(sdk);
    GlobalLoggerProvider.set(sdk.getSdkLoggerProvider());

    // Add hook to close SDK, which flushes logs
    // TODO (heya) use opentelemetry-java autoconfigure when it becomes stable
    Runtime.getRuntime().addShutdownHook(new Thread(sdk::close));
  }

  static void runWithASpan(Runnable runnable, boolean withSpan) {
    if (!withSpan) {
      runnable.run();
      return;
    }
    Span span = GlobalOpenTelemetry.getTracer("my-tracer").spanBuilder("my-span").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      runnable.run();
    } finally {
      span.end();
    }
  }
}
