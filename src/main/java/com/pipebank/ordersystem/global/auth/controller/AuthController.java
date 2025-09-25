package com.pipebank.ordersystem.global.auth.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.web.member.service.MemberService;
import com.pipebank.ordersystem.global.auth.dto.FindMemberIdRequest;
import com.pipebank.ordersystem.global.auth.dto.FindMemberIdResponse;
import com.pipebank.ordersystem.global.auth.dto.LoginRequest;
import com.pipebank.ordersystem.global.auth.dto.TokenResponse;
import com.pipebank.ordersystem.global.auth.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    // 쿠키 설정값
    private final String refreshCookieName = System.getProperty("app.auth.cookie.name", System.getenv().getOrDefault("APP_AUTH_COOKIE_NAME", "refreshToken"));
    private final String refreshCookiePath = System.getProperty("app.auth.cookie.path", System.getenv().getOrDefault("APP_AUTH_COOKIE_PATH", "/api"));
    private final String refreshCookieDomain = System.getProperty("app.auth.cookie.domain", System.getenv().getOrDefault("APP_AUTH_COOKIE_DOMAIN", ""));
    private final boolean refreshCookieSecure = Boolean.parseBoolean(System.getProperty("app.auth.cookie.secure", System.getenv().getOrDefault("APP_AUTH_COOKIE_SECURE", "true")));
    private final String refreshCookieSameSite = System.getProperty("app.auth.cookie.samesite", System.getenv().getOrDefault("APP_AUTH_COOKIE_SAMESITE", "Lax"));
    private final long refreshCookieMaxAge = Long.parseLong(System.getProperty("app.auth.cookie.maxage", System.getenv().getOrDefault("APP_AUTH_COOKIE_MAXAGE", "604800"))); // seconds

    private void setRefreshCookie(HttpServletResponse response, String tokenId) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(refreshCookieName, tokenId)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(refreshCookieMaxAge);
        if (!refreshCookieDomain.isBlank()) builder.domain(refreshCookieDomain);
        response.addHeader("Set-Cookie", builder.build().toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(0);
        if (!refreshCookieDomain.isBlank()) builder.domain(refreshCookieDomain);
        response.addHeader("Set-Cookie", builder.build().toString());
    }

    /**
     * 로그인: accessToken JSON + refreshToken HttpOnly 쿠키
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // 간단한 레이트리밋 (IP당 1분 10회)
        String ip = requestIp();
        if (!rateLimitPass(ip)) {
            return ResponseEntity.status(429).build();
        }
        AuthService.LoginOutcome outcome = authService.login(loginRequest);
        setRefreshCookie(response, outcome.getRefreshTokenId());
        return ResponseEntity.ok(outcome.getTokenResponse());
    }

    private String requestIp() {
        return org.springframework.web.context.request.RequestContextHolder.getRequestAttributes() instanceof org.springframework.web.context.request.ServletRequestAttributes attrs
                ? attrs.getRequest().getHeader("X-Forwarded-For") != null ? attrs.getRequest().getHeader("X-Forwarded-For").split(",")[0].trim()
                    : attrs.getRequest().getRemoteAddr()
                : "unknown";
    }

    private boolean rateLimitPass(String key) {
        try {
            // 간단한 in-memory 제한 (JVM 단일 인스턴스용). 운영에서는 Redis 사용 권장.
            java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.atomic.AtomicInteger> map = RateLimitHolder.MAP;
            java.util.concurrent.atomic.AtomicInteger c = map.computeIfAbsent(key, k -> new java.util.concurrent.atomic.AtomicInteger(0));
            int v = c.incrementAndGet();
            if (v == 1) {
                java.util.Timer t = new java.util.Timer();
                t.schedule(new java.util.TimerTask(){ public void run(){ c.set(0); } }, 60_000);
            }
            return v <= 10;
        } catch (Exception e) { return true; }
    }

    private static class RateLimitHolder { private static final java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.atomic.AtomicInteger> MAP = new java.util.concurrent.ConcurrentHashMap<>(); }

    /**
     * 회원 ID 찾기 (회원명 + 사업자번호)
     */
    @PostMapping("/find-member-id")
    public ResponseEntity<FindMemberIdResponse> findMemberId(@Valid @RequestBody FindMemberIdRequest request) {
        FindMemberIdResponse response = memberService.findMemberId(request.getMemberName(), request.getCustCodeSano());
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 갱신: 쿠키 기반 refresh 회전
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Origin/Referer 검증 (쿠키 기반 CSRF 방어 보강)
        String allowed = System.getProperty("app.cors.allowed-origins", System.getenv().getOrDefault("APP_CORS_ALLOWED_ORIGINS", ""));
        if (!allowed.isBlank()) {
            String origin = request.getHeader("Origin");
            String referer = request.getHeader("Referer");
            boolean ok = false;
            for (String o : allowed.split(",")) {
                String t = o.trim();
                if (t.isEmpty()) continue;
                if (origin != null && origin.startsWith(t)) ok = true;
                if (referer != null && referer.startsWith(t)) ok = true;
            }
            if (!ok) {
                return ResponseEntity.status(403).build();
            }
        }
        String refreshTokenId = null;
        if (request.getCookies() != null) {
            refreshTokenId = Arrays.stream(request.getCookies())
                    .filter(c -> refreshCookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshTokenId == null || refreshTokenId.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        AuthService.LoginOutcome outcome = authService.rotate(refreshTokenId);
        setRefreshCookie(response, outcome.getRefreshTokenId());
        return ResponseEntity.ok(outcome.getTokenResponse());
    }

    /**
     * 로그아웃: 서버에서 refresh 폐기 후 쿠키 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshTokenId = null;
        if (request.getCookies() != null) {
            refreshTokenId = Arrays.stream(request.getCookies())
                    .filter(c -> refreshCookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshTokenId != null && !refreshTokenId.isBlank()) {
            authService.logout(refreshTokenId);
        }
        clearRefreshCookie(response);

        Map<String, String> body = new HashMap<>();
        body.put("message", "로그아웃이 완료되었습니다");
        return ResponseEntity.ok(body);
    }

    /**
     * 토큰 검증 (간단 응답)
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader("Authorization") String authorization) {
        Map<String, Boolean> body = new HashMap<>();
        try {
            body.put("valid", authorization != null && authorization.startsWith("Bearer "));
        } catch (Exception e) {
            body.put("valid", false);
        }
        return ResponseEntity.ok(body);
    }
}