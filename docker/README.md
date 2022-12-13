# Docker example

To run, from this directory:

```
./mvnw package
docker build -t <tag> .
docker run -p 8080:8080 -e "APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>" <tag>
```
