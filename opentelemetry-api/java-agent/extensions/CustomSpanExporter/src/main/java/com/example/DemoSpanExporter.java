package com.example;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DemoSpanExporter implements SpanExporter {

    private static final String MY_CUSTOM_ATTRIBUTE_KEY = "myCustomAttributeKey";
    private static final String MY_CUSTOM_ATTRIBUTE_VALUE = "myCustomAttributeValue";

    private static final Logger logger = LoggerFactory.getLogger(DemoSpanExporter.class);
    public final SpanExporter delegate;

    public DemoSpanExporter(SpanExporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        logger.debug("###### DemoSpanExporter.export called");
        List<SpanData> copiedList = new ArrayList<>();
        for(SpanData spanData : collection) {
            List<EventData> events = spanData.getEvents();
            if (events != null && !events.isEmpty()) {
                for (EventData eventData : events) {
                    if (eventData.getName().contains("Status code 404")) {
                        logger.debug("###### found event with name= " + eventData.getName());
                        Attributes attributes = Attributes.builder()
                                .putAll(spanData.getAttributes())
                                .put(MY_CUSTOM_ATTRIBUTE_KEY, MY_CUSTOM_ATTRIBUTE_VALUE)
                                .build();
                        SpanData customSpanData = new DemoSpanData(spanData, attributes);
                        copiedList.add(customSpanData);
                    } else {
                        copiedList.add(spanData);
                    }
                }
            }
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

    @Override
    public void close() {
        SpanExporter.super.close();
    }
}
