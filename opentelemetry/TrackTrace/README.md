# Track Traces using Azure Monitor OpenTelemetry Exporter 

This is a sample app demonstrating how to send logs to Application Insights using 
[Azure Monitor OpenTelemetry Exporter](https://central.sonatype.com/artifact/com.azure/azure-monitor-opentelemetry-exporter/1.0.0-beta.8).

How to run it: 
- Replace `<APPLICATION_INSIGHTS_CONNECTION_STRING>` placeholder in `TrackTrace.java @ line 84` with an actual Application Insights resource connection string.
- Run TrackTrace class
- Verify an array of logs start with `{"ver":1,"name":"Message","time":"2023-06-07T18:24:38.058Z","iKey":"<INSTRUMENTATION_KEY>>"` were present in the console log 
that were sent to Application Insights ingestion service.
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
traces
| where timestamp > ago(10m)
| where message == "trackWithLogback - a slf4j log message with custom attributes" or message == "trackWithLogback - a slf4j log message 2 without custom attributes"

traces
| where timestamp > ago(10m)
| where message == "trackWithLog4j2 - it's a log4j2 message with custom attributes" or message == "trackWithLog4j2 - a log4j log message without custom attributes"
```