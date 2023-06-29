# Track Custom Metrics using Azure Monitor OpenTelemetry Exporter 

This is a sample app demonstrating how to send custom metrics to Application Insights using 
[Azure Monitor OpenTelemetry Exporter](https://central.sonatype.com/artifact/com.azure/azure-monitor-opentelemetry-exporter/1.0.0-beta.8).

How to run it: 
- Update `CONNECTION_STRING` with your Application Insights resource connection string.
- Run TrackMetric class
- Verify there is a log entry that starts with something like this: 
  `{"ver":1,"name":"Metric","time"`
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
customMetrics
| where name startswith "histogram"
```