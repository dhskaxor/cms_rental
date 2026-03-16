package com.nt.cms.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 캐시 설정
 * 
 * <p>Redis가 사용 가능하면 Redis 캐시를, 그렇지 않으면 인메모리 캐시를 사용한다.</p>
 * <p>캐싱 대상: 세션, 권한 정보 등</p>
 * 
 * @author CMS Team
 */
@Slf4j
@Configuration
public class CacheConfig {

    /**
     * 캐시 이름 상수
     */
    public static final String CACHE_USER = "user";
    public static final String CACHE_PERMISSION = "permission";
    public static final String CACHE_ROLE = "role";
    public static final String CACHE_BOARD = "board";

    /**
     * Redis 캐시 매니저 (Redis 사용 시)
     * 
     * @param connectionFactory Redis 연결 팩토리
     * @return CacheManager
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Redis 캐시 매니저를 초기화합니다.");
        
        // 기본 캐시 설정 (1시간 TTL)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
        
        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 사용자 캐시: 30분
        cacheConfigurations.put(CACHE_USER, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 권한 캐시: 1시간
        cacheConfigurations.put(CACHE_PERMISSION, defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 역할 캐시: 1시간
        cacheConfigurations.put(CACHE_ROLE, defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 게시판 캐시: 30분
        cacheConfigurations.put(CACHE_BOARD, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * 인메모리 캐시 매니저 (Redis 미사용 시)
     * 
     * @return CacheManager
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple", matchIfMissing = true)
    public CacheManager simpleCacheManager() {
        log.info("인메모리 캐시 매니저를 초기화합니다.");
        
        return new ConcurrentMapCacheManager(
                CACHE_USER,
                CACHE_PERMISSION,
                CACHE_ROLE,
                CACHE_BOARD
        );
    }
}
