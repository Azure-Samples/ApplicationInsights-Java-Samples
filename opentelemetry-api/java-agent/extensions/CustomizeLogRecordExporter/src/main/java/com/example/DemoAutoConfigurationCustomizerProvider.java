package com.example;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanLimits;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.util.HashMap;
import java.util.Map;

/**
 * This is one of the main entry points for Instrumentation Agent's customizations. It allows
 * configuring the {@link AutoConfigurationCustomizer}. See the {@link
 * #customize(AutoConfigurationCustomizer)} method below.
 *
 * <p>Also see https://github.com/open-telemetry/opentelemetry-java/issues/2022
 *
 * @see AutoConfigurationCustomizerProvider
 */
@AutoService(AutoConfigurationCustomizerProvider.class)
public class DemoAutoConfigurationCustomizerProvider
    implements AutoConfigurationCustomizerProvider {

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {

        autoConfiguration.addLogRecordExporterCustomizer((logRecordExporter, configProperties) -> {
            return new DemoLogRecordExporter(logRecordExporter);
        });
    }

    private Map<String, String> getDefaultProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("otel.exporter.otlp.endpoint", "http://backend:8080");
        properties.put("otel.exporter.otlp.insecure", "true");
        properties.put("otel.config.max.attrs", "16");
        properties.put("otel.traces.sampler", "demo");
        return properties;
    }
}
