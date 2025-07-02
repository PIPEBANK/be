package com.pipebank.ordersystem.global.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA AttributeConverter를 이용한 자동 암호화/복호화
 * - 엔티티 저장시: 평문 → 암호화 → DB 저장
 * - 엔티티 조회시: DB 조회 → 복호화 → 평문
 * 
 * 사용법:
 * @Convert(converter = EncryptionConverter.class)
 * private String phoneNumber;
 */
@Slf4j
@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {
    
    private static EncryptionUtil encryptionUtil;
    
    // Spring Bean 주입을 위한 정적 메서드
    public static void setEncryptionUtil(EncryptionUtil encryptionUtil) {
        EncryptionConverter.encryptionUtil = encryptionUtil;
    }
    
    /**
     * 엔티티 → DB 저장시 호출 (암호화)
     * @param attribute 엔티티의 평문 데이터
     * @return 암호화된 데이터 (Base64)
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (encryptionUtil == null) {
            log.warn("EncryptionUtil이 초기화되지 않았습니다. 평문으로 저장됩니다.");
            return attribute;
        }
        
        try {
            String encrypted = encryptionUtil.encrypt(attribute);
            log.debug("데이터 암호화 완료 (길이: {} → {})", 
                attribute != null ? attribute.length() : 0,
                encrypted != null ? encrypted.length() : 0);
            return encrypted;
        } catch (Exception e) {
            log.error("암호화 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * DB → 엔티티 조회시 호출 (복호화)
     * @param dbData DB의 암호화된 데이터
     * @return 복호화된 평문 데이터
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (encryptionUtil == null) {
            log.warn("EncryptionUtil이 초기화되지 않았습니다. 암호화된 데이터가 그대로 반환됩니다.");
            return dbData;
        }
        
        try {
            String decrypted = encryptionUtil.decrypt(dbData);
            log.debug("데이터 복호화 완료 (길이: {} → {})", 
                dbData != null ? dbData.length() : 0,
                decrypted != null ? decrypted.length() : 0);
            return decrypted;
        } catch (Exception e) {
            log.error("복호화 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
} 