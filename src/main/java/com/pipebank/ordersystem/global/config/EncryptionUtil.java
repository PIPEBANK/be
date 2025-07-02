package com.pipebank.ordersystem.global.config;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * AES-GCM 암호화/복호화 유틸리티 클래스
 * - AES-256-GCM 알고리즘 사용 (보안성 강화)
 * - Base64 인코딩으로 DB 저장
 * - IV(Initialization Vector) 자동 생성
 */
@Slf4j
@Component
public class EncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // GCM 표준 IV 길이
    private static final int GCM_TAG_LENGTH = 16; // GCM 태그 길이
    
    private final SecretKey secretKey;
    
    public EncryptionUtil(@Value("${app.encryption.secret-key}") String secretKeyString) {
        this.secretKey = new SecretKeySpec(
            Base64.getDecoder().decode(secretKeyString), 
            ALGORITHM
        );
    }
    
    /**
     * 문자열 암호화
     * @param plainText 평문
     * @return Base64 인코딩된 암호화 문자열 (IV + 암호화된 데이터)
     */
    public String encrypt(String plainText) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        
        try {
            // IV 생성
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            
            // 암호화 설정
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            // 암호화 실행
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // IV + 암호화된 데이터 결합
            byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);
            
            // Base64 인코딩하여 반환
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            log.error("암호화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("데이터 암호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 문자열 복호화
     * @param encryptedText Base64 인코딩된 암호화 문자열
     * @return 복호화된 평문
     */
    public String decrypt(String encryptedText) {
        if (!StringUtils.hasText(encryptedText)) {
            return encryptedText;
        }
        
        try {
            // Base64 디코딩
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // IV와 암호화된 데이터 분리
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
            System.arraycopy(encryptedWithIv, iv.length, encryptedData, 0, encryptedData.length);
            
            // 복호화 설정
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            // 복호화 실행
            byte[] decryptedData = cipher.doFinal(encryptedData);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("복호화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("데이터 복호화에 실패했습니다.", e);
        }
    }
    

} 