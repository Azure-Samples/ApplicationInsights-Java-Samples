# Track Exceptions using Azure Monitor OpenTelemetry Exporter

This is a sample app demonstrating how to send exceptions to Application Insights using
[Azure Monitor OpenTelemetry SDK Autoconfigure Distro](https://central.sonatype.com/artifact/com.azure/azure-monitor-opentelemetry-autoconfigure).

How to run it:
- Update `CONNECTION_STRING` with your Application Insights resource connection string.
- Run TrackException class
- Verify there is a log entry that starts with something as follows:
  `{"ver":1,"name":"Exception","time":`
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
exceptions
| where timestamp > ago(5m)
```