# ===== Stage 1: Build JAR =====
FROM maven:3.8.4-openjdk AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build Spring Boot app without running tests
RUN mvn clean package -DskipTests

# ===== Stage 2: Run JAR =====
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/remove-bg-0.0.1-SNAPSHOT.jar /app/remove-bg-0.0.1-SNAPSHOT.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/remove-bg-0.0.1-SNAPSHOT.jar"]
