// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;

/**
 * This is one of the main entry points for Instrumentation Agent's customizations. It allows
 * configuring the {@link AutoConfigurationCustomizer}. See the {@link
 * #customize(AutoConfigurationCustomizer)} method below.
 *
 * @see AutoConfigurationCustomizerProvider
 */
@AutoService(AutoConfigurationCustomizerProvider.class)
public class DurationAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {

        autoConfiguration.addSpanExporterCustomizer((spanExporter, configProperties) -> {
            return new DurationSpanExporter(spanExporter);
        });

    }
}
