package com.exmaple;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.extension.incubator.metrics.ExtendedDoubleHistogramBuilder;

import java.util.Arrays;

public class SendCustomMetrics {

  public static void main(String[] args) throws InterruptedException {
    sendDoubleHistogramMetric();
    Thread.sleep(8000); // wait at least 5 seconds to give batch span processor time to export
  }

  private static void sendDoubleHistogramMetric() {

    InMemoryMetricReader reader = InMemoryMetricReader.create();
    meterProvider = SdkMeterProvider.builder().registerMetricReader(reader).build();
    DoubleHistogram doubleHistogram =
        ((ExtendedDoubleHistogramBuilder) meterProvider
            .get("meter")
            .histogramBuilder("histogram"))
            .setAdvice(
                advice -> advice.setExplicitBucketBoundaries(Arrays.asList(10.0, 20.0, 30.0)))
            .build();


    Meter meter = GlobalOpenTelemetry.getMeter("OTEL.AzureMonitor.Demo");
    ExtendedDoubleHistogramBuilder builder = (ExtendedDoubleHistogramBuilder) meter.histogramBuilder("my-histogram");
    builder.setAdvice(advice -> advice.setExplicitBucketBoundaries(Arrays.asList(10.0, 20.0, 30.0)));
    DoubleHistogram histogram = builder.build();
    histogram.record(3.14);
  }
}
