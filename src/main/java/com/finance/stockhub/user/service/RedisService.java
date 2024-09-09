package com.finance.stockhub.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 캐시 저장
    public void setValues(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValues(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // 캐시 조회
    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 캐시 삭제
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

}
