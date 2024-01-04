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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is one of the main entry points for Instrumentation Agent's customizations. It allows
 * configuring the {@link AutoConfigurationCustomizer}. See the {@link
 * #customize(AutoConfigurationCustomizer)} method below.
 *
 * @see AutoConfigurationCustomizerProvider
 */
@AutoService(AutoConfigurationCustomizerProvider.class)
public class DemoAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {

    private static final Logger logger = LoggerFactory.getLogger(DemoAutoConfigurationCustomizerProvider.class);

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        logger.debug("###### DemoAutoConfigurationCustomizerProvider.customize");

        autoConfiguration.addTracerProviderCustomizer(this::congiureTracerProvider);
        autoConfiguration.addSpanExporterCustomizer((spanExporter, configProperties) -> {
            return new DemoSpanExporter(spanExporter);
        });

    }

    private SdkTracerProviderBuilder congiureTracerProvider(SdkTracerProviderBuilder tracerProviderBuilder, ConfigProperties configProperties) {
        logger.debug("###### DemoAutoConfigurationCustomizerProvider.congiureTracerProvider");
        return tracerProviderBuilder.addSpanProcessor(SimpleSpanProcessor.create(new DemoSpanExporter(SpanExporter.composite())));
    }

}
