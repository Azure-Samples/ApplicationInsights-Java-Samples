FROM eclipse-temurin:17
COPY target/app.jar app.jar
COPY target/agent/applicationinsights-agent.jar applicationinsights-agent.jar
ENTRYPOINT ["java", "-javaagent:applicationinsights-agent.jar", "-jar", "app.jar"]
