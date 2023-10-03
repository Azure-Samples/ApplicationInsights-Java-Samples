package com.example;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;

import java.util.Collection;

public class DemoLogRecordExporter implements LogRecordExporter {

    private static final String TARGET_ATTRIBUTE_KEY = "PIDVALUE";
    @Override
    public CompletableResultCode export(Collection<LogRecordData> collection) {
        for(LogRecordData data : collection) {
            String pidValue = data.getAttributes().get(AttributeKey.stringKey(TARGET_ATTRIBUTE_KEY));
            String body = data.getBody().asString();
            String newBody;
            if (pidValue != null && !pidValue.isEmpty()) {
                newBody = body.replace(TARGET_ATTRIBUTE_KEY, pidValue);
            }
            // LogRecordBuilder package protected
            // construct a new LogRecordData?
        }
        return CompletableResultCode.ofSuccess();
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
