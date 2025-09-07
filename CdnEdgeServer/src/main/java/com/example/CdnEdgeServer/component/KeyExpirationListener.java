package com.example.CdnEdgeServer.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
Το component KeyExpirationListener είναι υπεύθυνο για να "ακούει" τα events λήξης/διαγραφής των κλειδιών της Redis.
Όταν ληφθεί event για ένα συγκεκριμένο key, καλείται η συνάρτηση deleteLocalFile.
Η deleteLocalFile διαγράφει το αρχείο από τον τοπικό φάκελο του project.
Έτσι επιτυγχάνεται ο συγχρονισμός της κρυφής μνήμης και του τοπικού φακέλου του project με τα αρχεία.
 */

@Component
public class KeyExpirationListener implements MessageListener {

    @Value("${edge.local.filepath}")
    private String edgeLocalFilepath;

    private static final Logger logger = LoggerFactory.getLogger(KeyExpirationListener.class);

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String key = new String(message.getBody());
        String filename = key.contains("::") ? key.substring(key.indexOf("::") + 2) : key;
        String channel = new String(message.getChannel());

        if (channel.contains(":expired")) logger.debug("expired key: {}", key);
        else if (channel.contains(":evicted")) logger.debug("evicted key: {}", key);
        else if (channel.contains(":del")) logger.debug("deleted key: {}", key);
        else logger.debug("Other event on key: {} (channel: {})", key, channel);

        try {
            deleteLocalFile(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteLocalFile(String key) throws IOException {
        Path path = Paths.get(edgeLocalFilepath).resolve(key);

        try {
            boolean deleted = Files.deleteIfExists(path);

            if(deleted) {
                logger.info("Deleted local file for key: {}", key);
            } else {
                logger.warn("Local file not found for key: {}", key);
            }
        } catch (IOException e) {
            logger.error("Failed to delete file for key {}: {}", key, e.getMessage());
            throw new IOException("Failed to delete local file: " + key, e);
        }
    }
}