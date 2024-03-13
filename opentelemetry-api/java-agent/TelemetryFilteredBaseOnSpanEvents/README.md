# Use OpenTelemetry Java Agent Extensions to filter telemetry based on span events

This is a sample app demonstrating how to add a custom dimension to telemetry based on span events.
And then use telemetry processor to filter telemetry based on the new custom dimension.

## Build the extension artifact

To build the extension artifact, from this directory:

`cd ../extensions/CustomSpanExporter`
`../../../../mvnw package`

To run the application with the extension and Java agent, from this directory:

`cd ../../TelemetryFilteredBaseOnSpanEvents`

```
../../../mvnw package
export APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>"
export APPLICATIONINSIGHTS_SELF_DIAGNOSTICS_LEVEL=debug

 java -javaagent:target/agent/applicationinsights-agent.jar -Dotel.javaagent.extensions=../extensions/CustomSpanExporter/target/CustomSpanExporter-1.0-SNAPSHOT.jar -jar target/app.jar
```

Verify there is a log entry that starts with something as follows:
  `{"ver":1,"name":"RemoteDependency","time":`

After it's finished running, go to Application Insights portal logs blade, query the following:

  ```kusto
  dependencies
  | where name == 'mySpan'
  ```

Verify that there are 3 custom dimensions in the result:
`controlledError`:`true`, // this is added by the java agent telemetry processor
`myCustomAttributeKey`: `myCustomAttributeValue`, // this is added by the application when creating the span
`myCustomAttributeKey2`: `myCustomAttributeValue2`. // this is added by the CustomSpanExporter extension
