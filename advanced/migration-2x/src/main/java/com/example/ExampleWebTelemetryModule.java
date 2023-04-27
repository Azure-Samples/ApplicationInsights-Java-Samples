package com.example;

import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.microsoft.applicationinsights.extensibility.TelemetryModule;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.web.extensibility.modules.WebTelemetryModule;
import com.microsoft.applicationinsights.web.internal.ThreadContext;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ExampleWebTelemetryModule implements WebTelemetryModule, TelemetryModule {

    @Override
    public void initialize(TelemetryConfiguration telemetryConfiguration) {
    }

    @Override
    public void onBeginRequest(ServletRequest servletRequest, ServletResponse servletResponse) {
        RequestTelemetry requestTelemetry = ThreadContext.getRequestTelemetryContext().getHttpRequestTelemetry();
        requestTelemetry.getProperties().put("mykey", "myval");
    }

    @Override
    public void onEndRequest(ServletRequest servletRequest, ServletResponse servletResponse) {
    }
}
