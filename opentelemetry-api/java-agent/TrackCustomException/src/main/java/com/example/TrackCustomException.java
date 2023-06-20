package com.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TrackCustomException {

  public static void main(String[] args) throws InterruptedException {
    trackCustomException();
    Thread.sleep(50000); // wait at least 5 seconds to give batch span processor time to export
  }

  private static void trackCustomException() {
    // traceIdHex must be a 32-hex-character lowercase string
    // spanIdHex must be a 16-hex-character lowercase string
    // if traceIdHex or spanIdHex is invalid, it will result to SpanContext.INVALID
    // only when traceIdHex and spanIdHex are valid, operation_Id and operation_ParentId will get stamped onto the advanced exception
    // in this example, "ff01020304050600ff0a0b0c0d0e0f00" is your operation_id and "090a0b0c0d0e0f00" is your operation_SpanId
    SpanContext spanContext = SpanContext.create("ff01020304050600ff0a0b0c0d0e0f00", "090a0b0c0d0e0f00", TraceFlags.getSampled(), TraceState.getDefault());
    try (Scope ignored = Span.wrap(spanContext).makeCurrent()) {
      StringWriter sw = new StringWriter();
      new Exception().printStackTrace(new PrintWriter(sw, true));

      GlobalOpenTelemetry.get().getLogsBridge().get("my logger").logRecordBuilder()
          .setAttribute(SemanticAttributes.EXCEPTION_TYPE, "my exception type")
          .setAttribute(SemanticAttributes.EXCEPTION_MESSAGE, "This is an custom exception with custom exception type")
          .setAttribute(SemanticAttributes.EXCEPTION_STACKTRACE, sw.toString())
          .emit();
    }
  }
}
