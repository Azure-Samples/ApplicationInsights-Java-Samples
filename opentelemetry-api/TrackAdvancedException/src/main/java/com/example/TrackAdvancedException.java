package com.example;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.implementation.ResourceAttributes;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
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
    // traceIdHex must be a 32-hex-character lowercase string
    // spanIdHex must be a 16-hex-character lowercase string
    // if traceIdHex or spanIdHex is invalid, it will result to SpanContext.INVALID
    // only when traceIdHex and spanIdHex are valid, operation_Id and operation_ParentId will get stamped onto the advanced exception
    // in this example, "ff01020304050600ff0a0b0c0d0e0f00" is your operation_id and "090a0b0c0d0e0f00" is your operation_SpanId
    SpanContext spanContext = SpanContext.create("ff01020304050600ff0a0b0c0d0e0f00", "090a0b0c0d0e0f00", TraceFlags.getSampled(), TraceState.getDefault());
    try (Scope ignored = Span.wrap(spanContext).makeCurrent()) {
      log4jLogger.error("This is an exception with custom stack trace from log4j2", new AdvancedException("my exception"));
    }
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
                            .put(ResourceAttributes.SERVICE_INSTANCE_ID, "my cloud instance id")
                            .build())
                    .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(logRecordExporter).build())
                    .build())
            .build();
    GlobalOpenTelemetry.set(sdk);
    GlobalLoggerProvider.set(sdk.getSdkLoggerProvider());
  }
}
