package com.example;

import io.opentelemetry.api.common.Value;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogFilter implements LogRecordExporter {

    private final LogRecordExporter delegate;

    public LogFilter(LogRecordExporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletableResultCode export(Collection<LogRecordData> collection) {
        List<LogRecordData> filteredLogs = new ArrayList<>();
        for (LogRecordData logRecordData : collection) {
            if (shouldInclude(logRecordData)) {
                filteredLogs.add(logRecordData);
            }
        }
        return delegate.export(filteredLogs);
    }

    private boolean shouldInclude(LogRecordData logRecordData) {
        Value<?> bodyValue = logRecordData.getBodyValue();
        String logMessageToExclude = "Initializing Spring embedded WebApplicationContext";
        return bodyValue != null && !bodyValue.asString().equals(logMessageToExclude);
    }

    @Override
    public CompletableResultCode flush() {
        return delegate.flush();
    }

    @Override
    public CompletableResultCode shutdown() {
        return delegate.shutdown();
    }
}
