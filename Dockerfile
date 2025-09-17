# Simple single-stage build for Render.com
FROM gradle:8-jdk17 AS build

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew buildFatJar --no-daemon --stacktrace

# Runtime stage
FROM amazoncorretto:17-alpine AS runtime

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variable for port (Render.com requirement)
ENV PORT=8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]