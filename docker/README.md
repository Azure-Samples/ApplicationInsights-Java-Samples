# Docker example

To run, from this directory:

```
./mvnw package
export APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>
java -javaagent:target/agent/applicationinsights-agent.jar -jar target/simple-1.0-SNAPSHOT.jar
```
