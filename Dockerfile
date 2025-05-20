# Use Java 17 runtime
FROM eclipse-temurin:17

# Set working directory inside the container
WORKDIR /app

# Copy only the Maven wrapper first, so we can fix permissions before the full project copy
COPY mvnw ./
COPY mvnw mvnw.cmd ./

# Ensure the Maven wrapper has execution permissions before copying project files
RUN chmod +x mvnw mvnw.cmd;

# Copy the rest of the project files after setting permissions
COPY . .

# Ensure execution permissions before running Maven
RUN chmod +x ./mvnw

# Run Maven build without tests
RUN ./mvnw clean install -DskipTests

# Start the Spring Boot application
CMD ["./mvnw", "spring-boot:run"]