package com.pipebank.ordersystem.global.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 암호화 관련 설정 클래스
 * - EncryptionUtil을 EncryptionConverter에 주입
 * - 애플리케이션 시작시 암호화 시스템 초기화
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EncryptionConfig {
    
    private final EncryptionUtil encryptionUtil;
    
    /**
     * 애플리케이션 시작시 EncryptionConverter에 EncryptionUtil 주입
     */
    @PostConstruct
    public void initializeEncryption() {
        EncryptionConverter.setEncryptionUtil(encryptionUtil);
        log.info("암호화 시스템이 초기화되었습니다.");
    }
} 