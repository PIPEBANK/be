package com.pipebank.ordersystem.global.auth.service;

import com.pipebank.ordersystem.domain.web.member.entity.Member;
import com.pipebank.ordersystem.domain.web.member.repository.MemberRepository;
import com.pipebank.ordersystem.global.auth.dto.LoginRequest;
import com.pipebank.ordersystem.global.auth.dto.TokenResponse;
import com.pipebank.ordersystem.global.security.JwtTokenProvider;
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

    /**
     * 로그인 처리
     */
    public TokenResponse login(LoginRequest loginRequest) {
        log.info("로그인 시도 - 회원 ID: {}", loginRequest.getMemberId());

        // 활성화된 회원 조회
        Member member = memberRepository.findByMemberIdAndUseYn(loginRequest.getMemberId(), true)
                .orElseThrow(() -> {
                    log.error("활성화된 회원을 찾을 수 없습니다: {}", loginRequest.getMemberId());
                    return new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getMemberPw())) {
            log.error("비밀번호 불일치 - 회원 ID: {}", loginRequest.getMemberId());
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole().getAuthority());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        log.info("로그인 성공 - 회원 ID: {}, 권한: {}", member.getMemberId(), member.getRole().getDescription());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .role(member.getRole().getDescription())
                .build();
    }

    /**
     * 토큰 갱신
     */
    public TokenResponse refreshToken(String refreshToken) {
        log.info("토큰 갱신 요청");

        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.error("유효하지 않은 refresh token");
            throw new BadCredentialsException("유효하지 않은 refresh token입니다");
        }

        String memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
        
        // 활성화된 회원 확인
        Member member = memberRepository.findByMemberIdAndUseYn(memberId, true)
                .orElseThrow(() -> {
                    log.error("활성화된 회원을 찾을 수 없습니다: {}", memberId);
                    return new BadCredentialsException("유효하지 않은 사용자입니다");
                });

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole().getAuthority());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        log.info("토큰 갱신 성공 - 회원 ID: {}", member.getMemberId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .role(member.getRole().getDescription())
                .build();
    }

    /**
     * 로그아웃 처리 (토큰 무효화)
     */
    public void logout(String accessToken) {
        log.info("로그아웃 요청");
        // TODO: Redis에 블랙리스트로 토큰 저장하여 무효화 처리
        // 현재는 클라이언트에서 토큰 삭제로 처리
        log.info("로그아웃 처리 완료");
    }
} 