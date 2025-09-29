import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;

import io.opentelemetry.api.common.AttributeKey;
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
            AttributeKey.stringKey("http.route"), "/api",
            AttributeKey.stringKey("http.request.method"), "GET",
            AttributeKey.longKey("http.response.status_code"), 200L);
        histogram.record(5.0, attributes);
        histogram.record(15.0, attributes);
        histogram.record(20.0, attributes);
    }

    private static OpenTelemetrySdk initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        AzureMonitorAutoConfigure.customize(sdkBuilder, CONNECTION_STRING);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
