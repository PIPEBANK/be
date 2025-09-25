package com.pipebank.ordersystem.global.auth.service;

import com.pipebank.ordersystem.domain.web.member.entity.Member;
import com.pipebank.ordersystem.domain.web.member.repository.MemberRepository;
import com.pipebank.ordersystem.global.auth.dto.LoginRequest;
import com.pipebank.ordersystem.global.auth.dto.TokenResponse;
import com.pipebank.ordersystem.global.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Getter
    @AllArgsConstructor
    public static class LoginOutcome {
        private final TokenResponse tokenResponse;
        private final String refreshTokenId; // opaque token id stored in Redis

        public TokenResponse getTokenResponse() {
            return tokenResponse;
        }

        public String getRefreshTokenId() {
            return refreshTokenId;
        }
    }

    /**
     * 로그인 처리: accessToken 발급 + refresh opaque 토큰 발급(회전 기반)
     */
    public LoginOutcome login(LoginRequest loginRequest) {
        log.info("로그인 시도 - 회원 ID: {}", loginRequest.getMemberId());

        Member member = memberRepository.findByMemberIdAndUseYn(loginRequest.getMemberId(), true)
                .orElseThrow(() -> {
                    log.error("활성화된 회원을 찾을 수 없습니다: {}", loginRequest.getMemberId());
                    return new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getMemberPw())) {
            log.error("비밀번호 불일치 - 회원 ID: {}", loginRequest.getMemberId());
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole().getAuthority(), member.getTokenVersion());
        String refreshTokenId = refreshTokenService.issue(member.getMemberId());

        log.info("로그인 성공 - 회원 ID: {}, 권한: {}", member.getMemberId(), member.getRole().getDescription());

        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .role(member.getRole().getDescription())
                .build();

        return new LoginOutcome(response, refreshTokenId);
    }

    /**
     * refresh opaque 토큰을 소비하고 회전하여 새 access/refresh 발급
     */
    public LoginOutcome rotate(String refreshTokenId) {
        log.info("토큰 갱신 요청");
        RefreshTokenService.RotationResult result = refreshTokenService.consumeAndRotate(refreshTokenId)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 refresh token입니다"));

        String memberId = result.getMemberId();
        Member member = memberRepository.findByMemberIdAndUseYn(memberId, true)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 사용자입니다"));

        // 재사용 탐지: newTokenId가 null이면 탈취 의심 → 전역 무효화
        if (result.getNewTokenId() == null) {
            log.warn("Refresh token 재사용 탐지 - memberId: {}", memberId);
            // 모든 refresh 폐기 + tokenVersion 증가로 access 전역 무효화
            refreshTokenService.revokeAllForMember(memberId);
            member.bumpTokenVersion("system");
            memberRepository.save(member);
            throw new BadCredentialsException("세션이 만료되었습니다. 다시 로그인해 주세요.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole().getAuthority(), member.getTokenVersion());

        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .role(member.getRole().getDescription())
                .build();

        return new LoginOutcome(response, result.getNewTokenId());
    }

    /**
     * 로그아웃 처리 (refresh opaque 토큰 폐기)
     */
    public void logout(String refreshTokenId) {
        log.info("로그아웃 요청");
        refreshTokenService.revoke(refreshTokenId);
        log.info("로그아웃 처리 완료");
    }
}