# Use OpenTelemetry Java Agent Extensions to keep original log after Telemetry Processor is used to extract part of log message as a custom dimension

This is a sample app demonstrating how to alter log message to Application Insights.

To run, from this directory:
```
../../../mvnw package
export "APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>"
export APPLICATIONINSIGHTS_SELF_DIAGNOSTICS_LEVEL=debug

java -javaagent:target/agent/applicationinsights-agent.jar -Dotel.javaagent.extensions=../extensions/CustomizeLogRecordExporter/target/CustomizeLogRecordExporter-1.0-SNAPSHOT.jar -jar target/app.jar
```

Verify there is a log entry that starts with something as follows:
  `{"ver":1,"name":"Message","time":`

After it's finished running, go to Application Insights portal logs blade, query the following:

  ```kusto
  traces
  | where timestamp > ago(10m)
  | where message contains "PID"
  ```