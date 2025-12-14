# Εικόνα OpenJDK για την εκτέλεση της εφαρμογής Spring Boot.
FROM eclipse-temurin:21-jdk

# Ορισμός του φακέλου εργασίας μέσα στο container.
WORKDIR /app

# Αντιγραφή του αρχείου jar από το σύστημα υποδοχής στο container.
COPY build/libs/*.jar app.jar

# Εκκίνηση της εφαρμογής
ENTRYPOINT ["java", "-jar", "app.jar"]