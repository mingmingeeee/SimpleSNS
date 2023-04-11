package com.min.sns.repository;

import com.min.sns.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

// redis expired time 걸어주기
// 안 쓰는 data면 cache 저장할 이유가...? 서버 부하 줄여주기 위해 나온건뎅..
// redis 공간 효율적으로 활용하기 위함 !!!

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(User user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {} , {}", key, user.toString());
        // setIfAbsent: 있으면 set ㄴㄴ
        userRedisTemplate.opsForValue().setIfAbsent(key, user, USER_CACHE_TTL);
    }

    public Optional<User> getUser(String userName) {
        String key = getKey(userName);

        User user = userRedisTemplate.opsForValue().get(key);
        log.info("Get Data from Redis {} , {}", key, user);

        return Optional.ofNullable(user);
    }

    // 정확히 어떤 값인지 key 값 붙여주는 게 좋음
    private String getKey(String userName) {
        return "User:" + userName;
    }

}
