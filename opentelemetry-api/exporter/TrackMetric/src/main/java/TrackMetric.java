import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporter;

import com.azure.monitor.opentelemetry.exporter.implementation.SemanticAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleGauge;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;

import java.util.UUID;

public class TrackMetric {
    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final OpenTelemetrySdk openTelemetry = initOpenTelemetry();
    private static final Meter meter = openTelemetry.getMeter("my meter");

    public static void main(String[] args) throws InterruptedException {
        trackDoubleHistogramWithCustomDimensions();
        Thread.sleep(5000); // wait at least 5 seconds to give batch span processor time to export

        trackGauge();
        Thread.sleep(5000); // wait at least 5 seconds to give batch span processor time to export

        openTelemetry.shutdown();
    }

    public static void trackGauge() throws InterruptedException {
        DoubleGauge gauge = meter.gaugeBuilder("gauge" + UUID.randomUUID()).build();
        gauge.set(111.0);
    }

    private static void trackDoubleHistogramWithCustomDimensions() {
        DoubleHistogram histogram =
           meter.histogramBuilder("histogram" + UUID.randomUUID())
                .build();
        Attributes attributes = Attributes.of(
            SemanticAttributes.HTTP_ROUTE, "/api",
            SemanticAttributes.HTTP_METHOD, "GET",
            SemanticAttributes.HTTP_STATUS_CODE, 200L);
        histogram.record(5.0, attributes);
        histogram.record(15.0, attributes);
        histogram.record(20.0, attributes);
    }

    private static OpenTelemetrySdk initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        AzureMonitorExporter.customize(sdkBuilder, CONNECTION_STRING);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
