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

//Auto to component einai o listener o opoios akouei otan ena kleidi stin redis ginetai expired
//Molis ginei expired diagrafei to arxeio apo ton topiko disko
//etsi yparxei sygxronismos tis redis me to filesystem
@Component
public class KeyExpirationListener implements MessageListener {

    @Value("${edge.local.filepath}")
    private String edgeLocalFilepath;

    private static final Logger logger = LoggerFactory.getLogger(KeyExpirationListener.class);

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String key = new String(message.getBody());
        String filename = key.contains("::") ? key.substring(key.indexOf("::") + 2) : key;
        String channel = new String(message.getChannel()); //to channel einai to an einai expired i evicted

        if (channel.contains(":expired")) logger.debug("expired key: {}", key);
        else if (channel.contains(":evicted")) logger.debug("evicted key: {}", key);
        else if (channel.contains(":del")) logger.debug("deleted key: {}", key);
        else logger.debug("Other event on key: {} (channel: {})", key, channel);

        deleteLocalFile(filename);
    }

    private void deleteLocalFile(String key) {
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
        }
    }
}