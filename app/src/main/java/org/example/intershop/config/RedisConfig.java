package org.example.intershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value( "${spring.cache.ttl}")
    private Duration ttl;

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheCustomizer() {
        return builder -> builder.cacheDefaults(
            RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl( ttl)
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializer.json()
                    )
                )
        );
    }

}
