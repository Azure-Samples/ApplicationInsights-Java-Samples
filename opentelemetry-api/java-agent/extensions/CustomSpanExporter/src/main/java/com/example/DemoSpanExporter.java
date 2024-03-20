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
import java.util.concurrent.TimeUnit;

public class DemoSpanExporter implements SpanExporter {

    private static final String MY_CUSTOM_ATTRIBUTE_KEY_2 = "myCustomAttributeKey2";
    private static final String MY_CUSTOM_ATTRIBUTE_VALUE_2 = "myCustomAttributeValue2";

    public final SpanExporter delegate;


    public DemoSpanExporter(SpanExporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        List<SpanData> copiedList = new ArrayList<>();
        for(SpanData spanData : collection) {
            List<EventData> events = spanData.getEvents();
            if (events != null && !events.isEmpty()) {
                for (EventData eventData : events) {
                    if (eventData.getName().trim().contains("Status code 404")) {
                        System.out.println("#### Found event with name: " + eventData.getName());
                        Attributes attributes = Attributes.builder()
                                .putAll(spanData.getAttributes())
                                .put(MY_CUSTOM_ATTRIBUTE_KEY_2, MY_CUSTOM_ATTRIBUTE_VALUE_2)
                                .build();
                        SpanData customSpanData = new DemoSpanData(spanData, attributes);
                        copiedList.add(customSpanData);
                    } else {
                        System.out.println("#### Found event with name: " + eventData.getName());
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
}
