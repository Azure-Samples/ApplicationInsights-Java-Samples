# Basic Gradle (Groovy DSL) example

To run, from this directory:

```
./gradlew build
export "APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>"
java -javaagent:build/agent/applicationinsights-agent.jar -jar build/libs/app.jar
```
