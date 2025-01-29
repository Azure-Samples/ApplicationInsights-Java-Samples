import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;

import java.util.concurrent.TimeUnit;

public class Micrometer {
    private static final String CONNECTION_STRING = "<Your Connection String>";
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();

    public static void main(String[] args) throws InterruptedException {
        MeterRegistry meterRegistry = OpenTelemetryMeterRegistry.builder(openTelemetry).build();

        Metrics.addRegistry(meterRegistry);

        recordTimer();

        // need to sleep until JVM shutdown flush is fixed in beta.15 release
        // (https://github.com/Azure/azure-sdk-for-java/pull/37618)
        Thread.sleep(65000);
    }

    private static void recordTimer() {
        Metrics.timer("my-timer").record(10, TimeUnit.MILLISECONDS);
    }

    private static OpenTelemetry initOpenTelemetry() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        AzureMonitorAutoConfigure.customize(sdkBuilder, CONNECTION_STRING);
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
}
