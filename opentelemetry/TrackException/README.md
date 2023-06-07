# Track Exceptions using Azure Monitor OpenTelemetry Exporter 

This is a sample app demonstrating how to send exceptions to Application Insights using 
[Azure Monitor OpenTelemetry Exporter](https://central.sonatype.com/artifact/com.azure/azure-monitor-opentelemetry-exporter/1.0.0-beta.8).

How to run it: 
- Replace `<APPLICATION_INSIGHTS_CONNECTION_STRING>` placeholder in `TrackException.java @ line 58` with an actual Application Insights resource connection string.
- Run TrackException class
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
exceptions
| where timestamp > ago(5m)
```