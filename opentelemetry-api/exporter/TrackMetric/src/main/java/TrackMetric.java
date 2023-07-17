import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.extension.incubator.metrics.ExtendedDoubleHistogramBuilder;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

public class TrackMetric {
  private static final String CONNECTION_STRING = "<Your Connection String>";
  private static final Meter meter = initMeter();

  public static void main(String[] args) throws InterruptedException {
    trackDoubleHistogram();
    Thread.sleep(6000); // wait at least 5 seconds to give batch span processor time to export
  }

  private static void trackDoubleHistogram() {
    DoubleHistogram histogram =
        ((ExtendedDoubleHistogramBuilder) meter.histogramBuilder("histogram" + UUID.randomUUID()))
            .setAdvice(advice -> advice.setExplicitBucketBoundaries(Arrays.asList(10.0, 20.0, 30.0)))
            .build();
    histogram.record(5.0);
    histogram.record(15.0);
    histogram.record(20.0);
  }

  private static Meter initMeter() {
    // Create Azure Monitor exporter and configure OpenTelemetry meter to use this exporter
    // This should be done just once when application starts up
    MetricExporter exporter = new AzureMonitorExporterBuilder()
        .connectionString(CONNECTION_STRING)
        .buildMetricExporter();
    PeriodicMetricReader periodicMetricReader = PeriodicMetricReader
        .builder(exporter)
        .setInterval(Duration.ofSeconds(1))
        .build();
    SdkMeterProvider meterProvider = SdkMeterProvider
        .builder()
        .registerMetricReader(periodicMetricReader)
        .build();
    OpenTelemetrySdk sdk = OpenTelemetrySdk
        .builder()
        .setMeterProvider(meterProvider)
        .buildAndRegisterGlobal();
    return sdk.getMeter("my meter");
  }
}
