# We use the OpenJDK image to run the Spring Boot app
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the host to the container
COPY build/libs/CdnOriginServer-0.0.1-SNAPSHOT.jar CdnOriginServer.jar

# Run the application
ENTRYPOINT ["java", "-jar", "CdnOriginServer.jar"]