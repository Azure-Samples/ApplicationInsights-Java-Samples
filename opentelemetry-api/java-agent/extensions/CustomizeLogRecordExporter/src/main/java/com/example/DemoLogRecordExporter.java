package com.example;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.Body;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DemoLogRecordExporter implements LogRecordExporter {

    private static final String TARGET_ATTRIBUTE_KEY = "{PIDVALUE}";
    private static final Logger logger = LoggerFactory.getLogger(DemoLogRecordExporter.class);
    public final LogRecordExporter delegate;

    public DemoLogRecordExporter(LogRecordExporter delegate) {
        this.delegate = delegate;
    }
    @Override
    public CompletableResultCode export(Collection<LogRecordData> collection) {
        List<LogRecordData> copiedList = new ArrayList<>();
        for(LogRecordData data : collection) {
            String pidValue = data.getAttributes().get(AttributeKey.stringKey(TARGET_ATTRIBUTE_KEY));
            logger.debug("###### pidValue: " + pidValue);
            String body = data.getBody().asString();
            logger.debug("###### old body: " + body);
            String newBody = body;
            if (pidValue != null && !pidValue.isEmpty()) {
                newBody = body.replace(TARGET_ATTRIBUTE_KEY, pidValue);
                logger.debug("new body: " + newBody);
            }
            LogRecordData newLogData = new MyLogData(data, data.getAttributes(), Body.string(newBody));
            copiedList.add(newLogData);
        }
        return delegate.export(copiedList);
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
