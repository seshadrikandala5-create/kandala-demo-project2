FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar single-microservice.jar
ENTRYPOINT ["java", "-jar", "single-microservice.jar"]
