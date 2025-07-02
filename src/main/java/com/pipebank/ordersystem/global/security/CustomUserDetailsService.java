package com.pipebank.ordersystem.global.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pipebank.ordersystem.domain.web.member.entity.Member;
import com.pipebank.ordersystem.domain.web.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 정보 로드 시도: {}", username);
        
        Member member = memberRepository.findByMemberIdAndUseYn(username, true)
                .orElseThrow(() -> {
                    log.error("활성화된 사용자를 찾을 수 없습니다: {}", username);
                    return new UsernameNotFoundException("활성화된 사용자를 찾을 수 없습니다: " + username);
                });

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().getAuthority()));

        log.debug("사용자 정보 로드 완료: {} (권한: {})", username, member.getRole().getAuthority());

        return User.builder()
                .username(member.getMemberId())
                .password(member.getMemberPw())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!member.getUseYn())
                .build();
    }
} 