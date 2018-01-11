package com.irfanstore.irfanstoreratelimitrouteservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate< String, Object > template;

    public Object getValue(String key) {
        return template.opsForValue().get(key);
    }

    public void setValue(String key, int value,int expiry) {
        template.opsForValue().increment(key, value);
    }

    public void setExpiry(String key, int expiry) {
        template.expire(key, expiry, TimeUnit.MINUTES);
    }
}
