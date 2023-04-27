FROM mcr.microsoft.com/openjdk/jdk:17-mariner
COPY target/app.jar app.jar
COPY target/agent/applicationinsights-agent.jar applicationinsights-agent.jar
ENTRYPOINT ["java", "-javaagent:applicationinsights-agent.jar", "-jar", "app.jar"]
