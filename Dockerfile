FROM openjdk:17-jdk-slim
WORKDIR /app
COPY single-microservice.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
