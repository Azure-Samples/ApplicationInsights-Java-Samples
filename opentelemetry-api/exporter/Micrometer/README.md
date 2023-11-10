# Track Custom Metrics using Micrometer and the Azure Monitor OpenTelemetry Exporter

This is a sample app demonstrating how to send custom metrics to Application Insights using 
Micrometer and the Azure Monitor OpenTelemetry Exporter.

How to run it: 
- Update `CONNECTION_STRING` with your Application Insights resource connection string.
- Run Micrometer class
- Verify there is a log entry that starts with something like this: 
  `{"ver":1,"name":"Metric","time":`
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
customMetrics
| where name == "my-timer"
```
