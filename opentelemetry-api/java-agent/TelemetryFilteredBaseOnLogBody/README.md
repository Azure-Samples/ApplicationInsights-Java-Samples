# Use OpenTelemetry Java Agent Extensions to filter logs based on body content

This is a sample app demonstrating how to filter logs based on their body content using a custom `LogRecordExporter`.

## Build the extension artifact

To build the extension artifact, from this directory:

`cd ../extensions/FilterLogBasedOnBody`
`../../../../mvnw package`

To run the application with the extension and Java agent, from this directory:

`cd ../../TelemetryFilteredBaseOnLogBody`

```
../../../mvnw package
export APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>
export APPLICATIONINSIGHTS_SELF_DIAGNOSTICS_LEVEL=debug

 java -javaagent:target/agent/applicationinsights-agent.jar -Dotel.javaagent.extensions=../extensions/FilterLogBasedOnBody/target/FilterLogBasedOnBody-1.0-SNAPSHOT.jar -jar target/app.jar
```

At the application startup, Spring Boot logs an "Initializing Spring embedded WebApplicationContext" message.

The OpenTelemetry extension excludes this log from the telemetry data. 