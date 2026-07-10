# --- Layer 1: The Maven Compilation Cache Engine ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy only the pom.xml to download dependencies and plugins first (Layer Caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy your source directory containing the .avsc schemas
COPY src ./src

# Compile schemas, generate sources, and package the jar file locally
RUN mvn clean package -DskipTests


# --- Layer 2: The Production Runtime Engine ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS runner

WORKDIR /workspace

# Copy the built target files and configurations from the builder layer
COPY --from=builder /app/pom.xml ./pom.xml
COPY --from=builder /app/src ./src
COPY --from=builder /app/target ./target

# Default execution: push the compiled schemas directly to the schema registry endpoint
ENTRYPOINT ["mvn", "io.confluent:kafka-schema-registry-maven-plugin:7.6.0:register"]