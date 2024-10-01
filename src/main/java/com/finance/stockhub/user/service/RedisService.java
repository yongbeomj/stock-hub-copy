package com.finance.stockhub.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setValues(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValues(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

}
