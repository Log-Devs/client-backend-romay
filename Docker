# Use Java 17 runtime
FROM eclipse-temurin:21

# Set working directory inside the container
WORKDIR /app

# Copy all project files to the container
COPY . .

# Run Maven build inside the container
RUN ./mvnw clean install

# Start the Spring Boot application
CMD ["./mvnw", "spring-boot:run"]
