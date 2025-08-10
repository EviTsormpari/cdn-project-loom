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

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisKeyExpirationConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpirationConfiguration.class);

    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory, KeyExpirationListener keyExpirationListener) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.addMessageListener(keyExpirationListener, new PatternTopic("__keyevent@*__:expired")); //listener gia pattern logw expiration apo ttl
        listenerContainer.addMessageListener(keyExpirationListener, new PatternTopic("__keyevent@*__:evicted")); //listener gia pattern evicted logw lru antikatastasis
        listenerContainer.setErrorHandler(e -> logger.error("There was an error in redis key expiration listener container", e));

        return listenerContainer;
    }
}