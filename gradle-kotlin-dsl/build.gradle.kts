plugins {
  java
  id("org.springframework.boot") version "2.7.9"
  id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
  mavenCentral()
}

// create a separate configuration for the agent since it should not be a normal dependency
val agent: Configuration by configurations.creating

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  agent("com.microsoft.azure:applicationinsights-agent:3.4.11")
}

val copyAgent = tasks.register<Copy>("copyAgent") {
  from(agent.singleFile)
  into(layout.buildDirectory.dir("agent"))
  rename("applicationinsights-agent-.*\\.jar", "applicationinsights-agent.jar")
}

tasks {
  bootJar {
    archiveFileName.set("app.jar")
  }
  assemble {
    dependsOn(copyAgent)
  }
}
