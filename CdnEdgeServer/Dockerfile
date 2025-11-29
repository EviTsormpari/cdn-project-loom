# We use the OpenJDK image to run the Spring Boot app.
FROM eclipse-temurin:21-jdk

# Set the working directory in the container.
WORKDIR /app

# Copy the jar file from the host to the container.
COPY build/libs/*.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh

# Make the entrypoint script executable.
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]