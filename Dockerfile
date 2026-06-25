# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Maven Wrapper and POM first (layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Run ──
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (platforms override via $PORT)
EXPOSE 8080

# Activate production profile
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
