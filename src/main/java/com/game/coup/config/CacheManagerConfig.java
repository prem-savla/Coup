package com.game.coup.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
public class CacheManagerConfig {

    @Bean
    public CacheManager cacheManager(
            CaffeineCacheManager caffeineCacheManager,
            RedisCacheManager redisCacheManager) {

        CompositeCacheManager manager =
                new CompositeCacheManager(
                        caffeineCacheManager,
                        redisCacheManager
                );

        manager.setFallbackToNoOpCache(false);
        return manager;
    }
}