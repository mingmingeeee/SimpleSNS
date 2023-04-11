package com.min.sns.configuration;

import com.min.sns.model.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties; // yml 안이 "spring.redis"

    // redis command 쉽게 사용할 수 있도록 해주는 template
    // RedisTemplate<Key, Value>

    // Cache 고려할 점
    // 1. 데이터가 자주 변하는 건 놉
    // 2. 자주 사용하는 데이터 사용하면 좋음 (DB 부하 적어짐)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());

        // uri를 가지고 configuration 만들고 connection을 만들어주는 factory에 넣어주고
        // factory를 반환하면 실제 connection 일어날 때 factory에서 함 ~ !
        RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet(); // initial

        return factory;
    }

    // User data!
    // 지금은 변경할 수 없는 상태, 로직 추가된다 하더라도 비교적 적게 변하는 데이터
    // 자주 사용함 (Filter, 등)
    @Bean
    public RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();

        // opsForValue: 기본적인 get, set 도와주는 method

        // redis 정보 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // data redis에 저장할 때 Serialize 해서 넣어줌
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));


        return redisTemplate;
    }

}
