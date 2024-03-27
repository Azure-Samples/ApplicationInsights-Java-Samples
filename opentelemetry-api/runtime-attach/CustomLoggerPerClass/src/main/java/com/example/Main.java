package com.example;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import io.micrometer.core.instrument.Metrics;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ApplicationInsights.attach();

        MyLogger1 myLogger1 = new MyLogger1();
        myLogger1.trackWarn();
        Thread.sleep(6000); // wait at least 5 seconds to give batch LogRecord processor time to export

        MyLogger2 myLogger2 = new MyLogger2();
        myLogger2.trackError();
        Thread.sleep(6000); // wait at least 5 seconds to give batch LogRecord processor time to export


        Metrics.counter("test.counter.exclude.me").increment();
        Thread.sleep(6000);
    }
}
