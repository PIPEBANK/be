package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 부서 코드 엔티티
 * CO_BUSE_CODE 테이블 매핑
 */
@Entity
@Table(name = "CO_BUSE_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuseCode {

    @Id
    @Column(name = "BUSE_CODE_CODE")
    private Integer buseCodeCode;

    @Column(name = "BUSE_CODE_NAME", length = 50, nullable = false)
    private String buseCodeName;

    // 생성자
    public BuseCode(Integer buseCodeCode, String buseCodeName) {
        this.buseCodeCode = buseCodeCode;
        this.buseCodeName = buseCodeName;
    }

    // 표시명 반환 (부서명)
    public String getDisplayName() {
        return this.buseCodeName;
    }

    @Override
    public String toString() {
        return String.format("BuseCode{code=%d, name='%s'}", buseCodeCode, buseCodeName);
    }
} 