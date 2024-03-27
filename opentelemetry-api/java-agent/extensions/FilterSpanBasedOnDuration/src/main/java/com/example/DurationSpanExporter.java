package com.example;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DurationSpanExporter implements SpanExporter {

    public final SpanExporter delegate;

    public DurationSpanExporter(SpanExporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        List<SpanData> copiedList = new ArrayList<>();
        for(SpanData spanData : collection) {
            double duration = getRequestDurationInSeconds(spanData);
            if (duration > 5) { // ignore spans with duration less than 5 seconds
                copiedList.add(spanData);
            }
        }
        return delegate.export(copiedList);
    }

    private static double getRequestDurationInSeconds(SpanData spanData) {
        return TimeUnit.MILLISECONDS.convert(spanData.getEndEpochNanos() - spanData.getStartEpochNanos(), TimeUnit.NANOSECONDS) / 1000.0;
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }
}
