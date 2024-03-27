# Use OpenTelemetry Java Agent Extensions to filter telemetry based on request duration longer than 5 seconds

This is a sample app demonstrating how to filter request telemetry based on request duration.

## Build the extension artifact

To build the extension artifact, from this directory:

`cd ../extensions/FilterSpanBasedOnDuration`
`../../../../mvnw package`

To run the application with the extension and Java agent, from this directory:

`cd ../../TelemetryFilteredBaseOnRequestDuration`

```
../../../mvnw package
export APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>"
export APPLICATIONINSIGHTS_SELF_DIAGNOSTICS_LEVEL=debug

 java -javaagent:target/agent/applicationinsights-agent.jar -Dotel.javaagent.extensions=../extensions/FilterSpanBasedOnDuration/target/FilterSpanBasedOnDuration-1.0-SNAPSHOT.jar -jar target/app.jar
```

Send a request with longer than 5 seconds duration to the application: `curl http://localhost:8080/demo/longDuration`
Send a request with shorter than 5 seconds duration to the application: `curl http://localhost:8080/demo/shortDuration`

Verify there is a log entry that starts with something as follows:
  `{"ver":1,"name":"Request","time":`

After it's finished running, go to Application Insights portal logs blade, query the following:

  ```kusto
  requests
  | where name == 'myShortDurationSpan'
  ```

Verify that request with longer than 5 seconds duration is not emitted, i.e. requests table should not have any entry with name `myShortDurationSpan` and none with name `myLongDurationSpan`.
