# Use Java 17 runtime
FROM eclipse-temurin:17

# Set working directory inside the container
WORKDIR /app

# Copy the Maven wrapper first
COPY mvnw ./
COPY mvnw mvnw.cmd ./

# Ensure the Maven wrapper is executable before copying other files
RUN chmod +x mvnw

# Copy the rest of the project files after setting permissions
COPY . .

# Run Maven build without tests
RUN /bin/sh -c "./mvnw clean install -DskipTests"

# Start the Spring Boot application
CMD ["./mvnw", "spring-boot:run"]