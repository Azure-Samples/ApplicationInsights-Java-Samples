package com.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.DoubleUpDownCounter;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.ObservableDoubleGauge;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

public class TrackCustomMetrics {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("##### start tracking Histogram #####");
        trackHistogram();
        System.out.println("##### end tracking Histogram #####");

        System.out.println("##### start tracking Double Gauge #####");
        trackObservableDoubleGauge();
        System.out.println("##### end tracking Double Gauge #####");

        System.out.println("##### start tracking long counter #####");
        trackCounter();
        System.out.println("##### end tracking long counter #####");

        System.out.println("##### start tracking long counter #####");
        trackDoubleUpDownCounter();
        System.out.println("##### end tracking long counter #####");


        // sleep for a bit to let everything settle
        // otel metric is collected every 1 min by default, i.e. 60000 milliseconds
        // set otel.metric.export.interval=1000 to 1 second
        Thread.sleep(30000);
        System.out.println("Bye");
    }

    private static void trackHistogram() {
        System.out.println("##### start recording histogram #####");
        DoubleHistogram doubleHistogram =
            GlobalOpenTelemetry
                .get()
                .getMeter("heya-track-double-histogram")
                .histogramBuilder("heya-double-histogram").build();
        doubleHistogram.record(5.0);
        doubleHistogram.record(15.0);
        doubleHistogram.record(20.0);

        LongHistogram longHistogram =
            GlobalOpenTelemetry
                .get()
                .getMeter("heya-track-long-histogram")
                .histogramBuilder("heya-long-histogram").ofLongs().build();
        longHistogram.record(5L);
        longHistogram.record(15L);
        longHistogram.record(20L);
        System.out.println("##### done recording histogram #####");
    }

    private static void trackObservableDoubleGauge() {
        ObservableDoubleGauge doubleGauge = GlobalOpenTelemetry
            .get()
            .getMeter("fully.qualified.gauge")
            .gaugeBuilder("heya-double-gauge")
                .setUnit("ms").buildWithCallback(
                     result -> result.record(Runtime.getRuntime().totalMemory(),  Attributes.of(AttributeKey.stringKey("gaugeKey"), "gaugeValue")));
    }

    private static void trackCounter() {
        LongCounter longCounter = GlobalOpenTelemetry
            .get()
            .getMeter("heya-track-counter")
            .counterBuilder("heya-counter")
            .build();
        longCounter.add(13L, Attributes.of(AttributeKey.stringKey("counterKey"), "counterValue"));
    }

    private static void trackDoubleUpDownCounter() {
        DoubleUpDownCounter doubleUpDownCounter = GlobalOpenTelemetry
            .get()
            .getMeter("heya-track-double-upDownCounter")
            .upDownCounterBuilder("heya-double-upDownCounter")
            .ofDoubles()
            .build();
        doubleUpDownCounter.add(13L, Attributes.of(AttributeKey.stringKey("upDownCounterKey"), "upDownCounterValue"));
    }

    private static List<ThreadCpuTimeMeasurement> getThreadCpuTime() {
        List<ThreadCpuTimeMeasurement> measurements = new ArrayList<>();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadMXBean.getAllThreadIds();

        for (long threadId : threadIds) {
            long cpuTime = threadMXBean.getThreadCpuTime(threadId);
            if (cpuTime != -1) {
                measurements.add(new ThreadCpuTimeMeasurement(cpuTime / 1_000_000.0, threadId));
            }
        }

        return measurements;
    }

    private static class ThreadCpuTimeMeasurement {
        private final double cpuTime;
        private final long threadId;

        public ThreadCpuTimeMeasurement(double cpuTime, long threadId) {
            this.cpuTime = cpuTime;
            this.threadId = threadId;
        }

        public double getCpuTime() {
            return cpuTime;
        }

        public long getThreadId() {
            return threadId;
        }
    }
}
