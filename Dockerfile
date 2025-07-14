FROM alpine/java:21-jre

COPY target/multimodal-rag-api-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]
