package com.example.CdnEdgeServer.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/*
Το CacheConfiguration ρυθμίζει τον τρόπο λειτουργίας της κρυφής μνήμης Redis για Java αντικείμενα.

1. Θέτει χρονικό περιορισμό ttl (time-to-live) σε κάθε key ώστε να ανανεώνονται τα δεδομένα.
2. Δεν αποθηκεύει null δεδομένα.
3. Επιτρέπει στη Redis την αποθήκευση Java αντικειμένων.
 */

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(){

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}