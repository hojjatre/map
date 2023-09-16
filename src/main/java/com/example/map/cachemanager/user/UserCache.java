package com.example.map.cachemanager.user;

import com.example.map.config.RedisConfig;
import com.example.map.dto.user.UserInfoResponse;
import lombok.Getter;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserCache {
    RMapCache<String, String> userCache;
    private final RedisConfig redisConfig;
    private final String nameCache = "token";

    public UserCache(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public void tokenCache(UserInfoResponse userInfoResponse){
        userCache = redisConfig.redissonClient().getMapCache(nameCache);
        userCache.put(userInfoResponse.getUsername(), userInfoResponse.getToken(), 60, TimeUnit.MINUTES);
    }

    public RMapCache<String, String> getUserCache() {
        userCache = redisConfig.redissonClient().getMapCache(nameCache);
        return userCache;
    }
}
