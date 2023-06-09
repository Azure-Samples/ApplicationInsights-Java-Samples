package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.implementation.ResourceAttributes;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackAdvancedException {

  private static final String CONNECTION_STRING = "<Your Connection String>";
  private static final Logger log4jLogger = LogManager.getLogger(TrackAdvancedException.class);

  public static void main(String[] args) throws InterruptedException {
    initOpenTelemetry();

    trackAdvancedException();
    Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
  }

  private static void trackAdvancedException() {
    Exception exception = new Exception("my exception");
    StackTraceElement[] stackTraceElements = new StackTraceElement[] {
        new StackTraceElement("declaringClass1", "methodName1", "filename1", 123),
        new StackTraceElement("declaringClass2", "methodName2", "filename2", 456),
        new StackTraceElement("declaringClass3", "methodName3", "filename3", 789)

    };
    exception.setStackTrace(stackTraceElements);
    log4jLogger.error("This is an advanced exception from log4j2", exception);
  }

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
                            .build())
                    .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(logRecordExporter).build())
                    .build())
            .build();
    GlobalOpenTelemetry.set(sdk);
    GlobalLoggerProvider.set(sdk.getSdkLoggerProvider());
  }
}
