// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.trace.data.DelegatingSpanData;
import io.opentelemetry.sdk.trace.data.SpanData;

public class DemoSpanData extends DelegatingSpanData {
    private final Attributes attributes;

    public DemoSpanData(SpanData delegate, Attributes attributes) {
        super(delegate);
        this.attributes = attributes;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }
}

