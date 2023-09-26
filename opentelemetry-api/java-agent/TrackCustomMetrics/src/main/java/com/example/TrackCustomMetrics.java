package com.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.DoubleHistogram;

import java.util.UUID;

public class TrackCustomMetrics {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("##### start tracking custom metrics #####");
        trackCustomMetrics();
        System.out.println("##### end trackCustomMetrics #####");

        // sleep for a bit to let everything settle
        // otel metric is collected every 1 min by default, i.e. 60000 milliseconds
        // set otel.metric.export.interval=1000 to 1 second
        Thread.sleep(10000);
        System.out.println("Bye");
    }

    private static void trackCustomMetrics() {
        System.out.println("##### start recording histogram #####");
        for (int i = 0; i < 10; i++) {
            DoubleHistogram histogram =
                GlobalOpenTelemetry
                    .get()
                    .getMeter("heya-TrackCustomMetrics-" + i+1)
                    .histogramBuilder("heya-histogram-" + UUID.randomUUID()).build();
            histogram.record(5.0);
            histogram.record(15.0);
            histogram.record(20.0);
        }
        System.out.println("##### done recording histogram #####");
    }
}
