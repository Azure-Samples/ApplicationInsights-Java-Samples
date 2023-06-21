# Customize Logs Using Class Specific Logging Level

This is a sample app demonstrating how to send logs for class specific logging level by [enabling Application Insights Java programmatically](https://learn.microsoft.com/en-us/azure/azure-monitor/app/java-spring-boot#enabling-programmatically).

How to run it:
- Update `<YOUR CONNECTION STRING>` with your Application Insights resource connection string in `/resources/applicationinsights.json`.
- Run `mvn clean package` via the git bash console
- Run Main class
- Verify there are a few log entries that start with something as follows:
  `{"ver":1,"name":"Exception","time":`
- After it's finished running, go to Application Insights portal logs blade, query the following:

  ```kusto
  traces
  | where timestamp > ago(10m)
  | where message == 'WARN log message from MyLogger1 without custom attributes'
  or message == 'ERROR log message from MyLogger2 without custom attributes'
  ```
  Note: MyLogger1 is using logging level WARN, `"INFO log message from MyLogger1 with custom attributes will get ignored"` logged at INFO level will get ignored.  
  Similarly, MyLogger2 is using logging level ERROR, `"DEBUG log message from MyLogger2 with custom attributes will get ignored"` logged at DEBUG level will get ignored.

  Thus, query the following is expected to return nothing:
  ```
  traces
  | where message == 'INFO log message from MyLogger1 with custom attributes will get ignored'
  or message == 'DEBUG log message from MyLogger2 with custom attributes will get ignored'
  ```