# Use Java 17 runtime
FROM eclipse-temurin:17

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Ensure `mvnw` has execution permissions before running it
RUN chmod +x mvnw

# Run Maven build
RUN ./mvnw clean install

# Start the Spring Boot application
CMD ["./mvnw", "spring-boot:run"]