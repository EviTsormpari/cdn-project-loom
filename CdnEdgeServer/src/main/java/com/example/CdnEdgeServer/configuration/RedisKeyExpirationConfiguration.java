package com.example.CdnEdgeServer.configuration;

import com.example.CdnEdgeServer.component.KeyExpirationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/*
Το RedisKeyExpirationConfiguration δημιουργεί τα events λήξης/διαγραφής των κλειδιών της Redis.

1. Δημιουργία κλειδιού expired για τη διαγραφή κλειδιών λόγω του χρονικού περιορισμού TTL.
2. Δημιουργία κλειδιού evicted για τη διαγραφή κλειδιών λόγω πολιτικής αντικατάστασης αρχείων (π.χ LRU).
3. Δημιουργία κλειδιού deleted για τη διαγραφή κλειδιών λόγω ενημέρωσης διαγραφής από τον κεντρικό διακομιστή.

Με αυτά τα events ο διακομιστής κρυφής μνήμης μπορεί να συγχρονίσει τη cache και τον τοπικό φάκελο με την
πραγματική κατάσταση της Redis.
 */

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisKeyExpirationConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpirationConfiguration.class);

    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory, KeyExpirationListener keyExpirationListener) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.addMessageListener(keyExpirationListener, new PatternTopic("__keyevent@*__:expired"));
        listenerContainer.addMessageListener(keyExpirationListener, new PatternTopic("__keyevent@*__:evicted"));
        listenerContainer.addMessageListener(keyExpirationListener, new PatternTopic("__keyevent@*__:del"));
        listenerContainer.setErrorHandler(e -> logger.error("There was an error in redis key expiration listener container", e));

        return listenerContainer;
    }
}