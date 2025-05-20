# Use Java 17 runtime
FROM eclipse-temurin:17

# Set working directory inside the container
WORKDIR /app

# Copy the Maven wrapper script separately first to ensure permissions
COPY mvnw ./
COPY mvnw.cmd ./

# Ensure `mvnw` has execution permissions before copying other files
RUN chmod +x mvnw mvnw.cmd

# Copy the rest of the project files AFTER setting permissions
COPY . .

# Run Maven build without tests
RUN bash -c "./mvnw clean install -DskipTests"

# Start the Spring Boot application
CMD ["./mvnw", "spring-boot:run"]