FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/cuenta-service-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]
