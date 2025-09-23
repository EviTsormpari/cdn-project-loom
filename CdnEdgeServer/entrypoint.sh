#!/bin/sh

# Διαγραφή όλων των αρχείων στον φάκελο /app/files
# στην περίπτωση που υπάρχουν παλιά αρχεία από προηγούμενες εκτελέσεις.
rm -f /app/files/*

# Εκκίνηση της Spring Boot εφαρμογής.
exec java -jar CdnEdgeServer.jar