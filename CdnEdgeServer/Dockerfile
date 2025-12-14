# Εικόνα OpenJDK για την εκτέλεση της εφαρμογής Spring Boot.
FROM eclipse-temurin:21-jdk

# Ορισμός του φακέλου εργασίας μέσα στο container.
WORKDIR /app

# Αντιγραφή του αρχείου jar από το σύστημα υποδοχής στο container.
COPY build/libs/*.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh

# Παραχώρηση δικαιωμάτων εκτέλεσης στο script εκκίνησης.
RUN chmod +x /app/entrypoint.sh

# Ορισμός του script εκκίνησης και εκκίνηση της εφαρμογής.
ENTRYPOINT ["/app/entrypoint.sh"]