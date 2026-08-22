# ---- Build stage: compile the jar with Maven, discarded from the final image ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
# Download dependencies first so this layer is cached across rebuilds
# unless pom.xml actually changes.
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage: just a JRE + the jar, much smaller than the build image ----
FROM eclipse-temurin:17-jre-jammy

# Run as a non-root user inside the container (defense in depth).
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

WORKDIR /app
COPY --from=build /build/target/backend.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
