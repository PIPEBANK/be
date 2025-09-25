package com.pipebank.ordersystem.global.auth.service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenTtlMillis;

    private String key(String tokenId) { return "auth:refresh:" + tokenId; }
    private String usedKey(String tokenId) { return "auth:refresh:used:" + tokenId; }
    private String userSetKey(String memberId) { return "auth:refresh:user:" + memberId; }

    public String issue(String memberId) {
        String tokenId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key(tokenId), memberId, Duration.ofMillis(refreshTokenTtlMillis));
        // 사용자별 토큰 집합 관리
        redisTemplate.opsForSet().add(userSetKey(memberId), tokenId);
        redisTemplate.expire(userSetKey(memberId), Duration.ofMillis(refreshTokenTtlMillis));
        return tokenId;
    }

    @Getter
    @AllArgsConstructor
    public static class RotationResult {
        private final String newTokenId;
        private final String memberId;
    }

    public Optional<RotationResult> consumeAndRotate(String tokenId) {
        String redisKey = key(tokenId);
        Object memberId = redisTemplate.opsForValue().get(redisKey);
        if (memberId == null) {
            // 재사용 탐지: 이미 사용된 토큰인지 체크
            Object reusedMember = redisTemplate.opsForValue().get(usedKey(tokenId));
            if (reusedMember != null) {
                return Optional.of(new RotationResult(null, (String) reusedMember)); // null은 재사용 신호
            }
            return Optional.empty();
        }
        // 이전 토큰 사용 표시(재사용 탐지용)
        redisTemplate.opsForValue().set(usedKey(tokenId), (String) memberId, Duration.ofMillis(refreshTokenTtlMillis));
        // 이전 토큰 삭제 및 사용자 세트에서 제거
        redisTemplate.delete(redisKey);
        redisTemplate.opsForSet().remove(userSetKey((String) memberId), tokenId);
        // 새 토큰 발급 및 사용자 세트에 추가
        String newTokenId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key(newTokenId), (String) memberId, Duration.ofMillis(refreshTokenTtlMillis));
        redisTemplate.opsForSet().add(userSetKey((String) memberId), newTokenId);
        redisTemplate.expire(userSetKey((String) memberId), Duration.ofMillis(refreshTokenTtlMillis));
        return Optional.of(new RotationResult(newTokenId, (String) memberId));
    }

    public void revoke(String tokenId) {
        Object memberId = redisTemplate.opsForValue().get(key(tokenId));
        if (memberId != null) {
            redisTemplate.opsForSet().remove(userSetKey((String) memberId), tokenId);
        }
        redisTemplate.delete(key(tokenId));
    }

    public void revokeAllForMember(String memberId) {
        var tokens = redisTemplate.opsForSet().members(userSetKey(memberId));
        if (tokens != null) {
            for (Object t : tokens) {
                redisTemplate.delete(key((String) t));
            }
        }
        redisTemplate.delete(userSetKey(memberId));
    }
}


