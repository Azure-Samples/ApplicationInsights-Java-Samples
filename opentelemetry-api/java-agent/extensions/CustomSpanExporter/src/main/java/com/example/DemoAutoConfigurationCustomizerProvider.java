// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;

/**
 * This is one of the main entry points for Instrumentation Agent's customizations. It allows
 * configuring the {@link AutoConfigurationCustomizer}. See the {@link
 * #customize(AutoConfigurationCustomizer)} method below.
 *
 * @see AutoConfigurationCustomizerProvider
 */
@AutoService(AutoConfigurationCustomizerProvider.class)
public class DemoAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        System.out.println("###### DemoAutoConfigurationCustomizerProvider.customize");

//        autoConfiguration.addTracerProviderCustomizer(this::congiureTracerProvider);
        autoConfiguration.addSpanExporterCustomizer((spanExporter, configProperties) -> {
            return new DemoSpanExporter(spanExporter);
        });

    }

//    private SdkTracerProviderBuilder congiureTracerProvider(SdkTracerProviderBuilder tracerProviderBuilder, ConfigProperties configProperties) {
//        System.out.println("###### DemoAutoConfigurationCustomizerProvider.congiureTracerProvider");
//        return tracerProviderBuilder.addSpanProcessor(SimpleSpanProcessor.create(new DemoSpanExporter(SpanExporter.composite())));
//    }

}
