package com.min.sns.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 다중 인스턴스일 경우 그 위에 더 넣어야 함
@Slf4j
@Repository
public class EmitterRepository {

    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    // user + browser 저장
    public SseEmitter save(Integer userId, SseEmitter sseEmitter) {
        final String key = getKey(userId);
        emitterMap.put(key, sseEmitter);

        log.info("Set sseEmitter {}", userId);
        return sseEmitter;
    }

    // 맞는 browser 찾기
    public Optional<SseEmitter> get(Integer userId) {
        final String key = getKey(userId);
        return Optional.ofNullable(emitterMap.get(key));
    }

    public void delete(Integer userId) {
        emitterMap.remove(getKey(userId));
    }

    private String getKey(Integer userId) {
        return "Emitter:UID:" + userId;
    }

}
