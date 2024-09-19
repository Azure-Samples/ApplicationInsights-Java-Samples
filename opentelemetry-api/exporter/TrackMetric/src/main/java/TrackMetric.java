import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.implementation.SemanticAttributes;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;

import java.util.UUID;

public class TrackMetric {
    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();
    private static final Meter meter = openTelemetry.getMeter("my meter");

    public static void main(String[] args) throws InterruptedException {
        trackDoubleHistogram();
        Thread.sleep(5000); // wait at least 5 seconds to give batch span processor time to export
    }

    private static void trackDoubleHistogram() {
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

    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        new AzureMonitorExporterBuilder()
            .connectionString(CONNECTION_STRING)
            .install(sdkBuilder);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
