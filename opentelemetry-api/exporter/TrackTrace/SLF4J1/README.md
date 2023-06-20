# Log4j2

This is a sample app demonstrating how to send log4j2 logs to Application Insights using
[Azure Monitor OpenTelemetry Exporter](https://central.sonatype.com/artifact/com.azure/azure-monitor-opentelemetry-exporter/1.0.0-beta.8).

How to run it:
- Update `CONNECTION_STRING` with your Application Insights resource connection string.
- Run TrackLog4j2 class
- Verify an array of logs start with `{"ver":1,"name":"Message","time":"<TIME>","iKey":"<INSTRUMENTATION_KEY>"` were present in the console log
  that were sent to Application Insights ingestion service.
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
traces
| where message == "trackWithSlf4j_1 - a slf4j_1 log message with MDC"
or message == "trackWithSlf4j_1 - a slf4j_1 log message without MDC"
```