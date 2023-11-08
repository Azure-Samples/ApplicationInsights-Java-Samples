import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.implementation.SemanticAttributes;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.extension.incubator.metrics.ExtendedDoubleHistogramBuilder;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;

import java.util.UUID;

import static java.util.Arrays.asList;

public class TrackMetric {
    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();
    private static final Meter meter = openTelemetry.getMeter("my meter");

    public static void main(String[] args) throws InterruptedException {
        trackDoubleHistogram();
        Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
    }

    private static void trackDoubleHistogram() {
        DoubleHistogram histogram =
            ((ExtendedDoubleHistogramBuilder) meter.histogramBuilder("histogram" + UUID.randomUUID()))
                .setAttributesAdvice(asList(
                    SemanticAttributes.HTTP_ROUTE,
                    SemanticAttributes.HTTP_METHOD,
                    SemanticAttributes.HTTP_STATUS_CODE))
                .build();
        histogram.record(5.0);
        histogram.record(15.0);
        histogram.record(20.0);
    }

    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        new AzureMonitorExporterBuilder()
            .connectionString(CONNECTION_STRING)
            .build(sdkBuilder);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
