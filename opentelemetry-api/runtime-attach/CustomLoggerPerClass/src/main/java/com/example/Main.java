package com.example;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

  public static void main(String[] args) throws InterruptedException {
    ApplicationInsights.attach();

    // TODO (heya) fix duplicate log either by disabling log4j in Java agent
    MyLogger1 myLogger1 = new MyLogger1();
    myLogger1.trackWarn();
    Thread.sleep(6000); // wait at least 5 seconds to give batch LogRecord processor time to export

    MyLogger2 myLogger2 = new MyLogger2();
    myLogger2.trackError();
    Thread.sleep(6000); // wait at least 5 seconds to give batch LogRecord processor time to export
  }
}
