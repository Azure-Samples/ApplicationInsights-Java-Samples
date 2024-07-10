# Azure Core HTTP Client instrumentation via Application Insights Java Agent

This is a sample app demonstrating how to send an Azure core http client request to Application Insights using the Application Insights Java Agent.

To run, from this directory:

```
../../../mvnw package
export "APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>"
export APPLICATIONINSIGHTS_SELF_DIAGNOSTICS_LEVEL=debug
java -javaagent:target/agent/applicationinsights-agent.jar -jar target/app.jar
```

Verify the log entries that starts with something as follows:
DEBUG c.m.a.a.i.exporter.AgentSpanExporter - exporting span: SpanData{spanContext=ImmutableSpanContext{traceId=<ACTUAL_TRACE_ID>, spanId=<ACTUAL_SPAN_ID>, traceFlags=01, traceState=ArrayBasedTraceState{entries=[]}, remote=false, valid=true}, 
parentSpanContext=ImmutableSpanContext{traceId=00000000000000000000000000000000, spanId=0000000000000000, traceFlags=00, traceState=ArrayBasedTraceState{entries=[]}, remote=false, valid=false}, resource=Resource{schemaUrl=null, 
attributes={service.name="unknown_service:java", telemetry.sdk.language="java", telemetry.sdk.name="opentelemetry", telemetry.sdk.version="1.38.0"}}, instrumentationScopeInfo=InstrumentationScopeInfo{name=azure-core, 
version=null, schemaUrl=https://opentelemetry.io/schemas/1.17.0, attributes={}}, name=GET, kind=CLIENT, startEpochNanos=1720643114471587500, endEpochNanos=1720643117036388600, 
attributes=AttributesMap{data={thread.id=1, thread.name=main, http.method=GET, server.port=443, http.url=https://www.google.com, http.status_code=200, server.address=www.google.com}, capacity=128, totalAddedValues=7}, 
totalAttributeCount=7, events=[], totalRecordedEvents=0, links=[], totalRecordedLinks=0, status=ImmutableStatusData{statusCode=UNSET, description=}, hasEnded=true}

{"ver":1,"name":"RemoteDependency","time":"<ACTUAL_TIMESTAMP>>","iKey":"<YOUR_INSTRUMENTATION_KEY>>","tags":{"ai.internal.sdkVersion":"java:3.5.3","ai.operation.id":"<OPERATION_ID>","ai.cloud.roleInstance":"<YOUR_ROLE_INSTANCE>"},
"data":{"baseType":"RemoteDependencyData","baseData":{"ver":2,"id":"<ACTUAL_ID>>","name":"GET /","resultCode":"200","data":"https://www.google.com","type":"Http","target":"www.google.com","duration":"<ACTUAL_DURATION>>","success":true}}`

After it's finished running, go to Application Insights portal logs blade, query the following:

  ```kusto
  dependencies
  | where sdkVersion contains "3.5.3"
  ```