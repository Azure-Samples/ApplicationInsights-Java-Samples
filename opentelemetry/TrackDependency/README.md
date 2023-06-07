# Track Dependencies using Azure Monitor OpenTelemetry Exporter 

This is a sample app demonstrating how to send custom dependencies to Application Insights using 
[Azure Monitor OpenTelemetry Exporter](https://central.sonatype.com/artifact/com.azure/azure-monitor-opentelemetry-exporter/1.0.0-beta.8).

How to run it: 
- Replace `<APPLICATION_INSIGHTS_CONNECTION_STRING>` placeholder in `TrackDependency.java @ line 41` with an actual Application Insights resource connection string.
- Run TrackDependency class
- After it's finished running, go to Application Insights portal logs blade, query the following:

```kusto
dependencies
| where name == 'trackDependency'
```