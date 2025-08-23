# Build Stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime Stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/stemlen-0.0.1-SNAPSHOT.jar Stemlen.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Stemlen.jar"]
