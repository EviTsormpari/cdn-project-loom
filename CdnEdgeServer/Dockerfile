# We use the OpenJDK image to run the Spring Boot app.
FROM openjdk:21-jdk-slim

# Set the working directory in the container.
WORKDIR /app

# Copy the jar file from the host to the container.
COPY build/libs/CdnEdgeServer-0.0.1-SNAPSHOT.jar CdnEdgeServer.jar
COPY entrypoint.sh /app/entrypoint.sh

# Make the entrypoint script executable.
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]