package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소속 코드 엔티티
 * CO_SOSOK_CODE 테이블 매핑
 */
@Entity
@Table(name = "CO_SOSOK_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SosokCode {

    @Id
    @Column(name = "SOSOK_CODE_CODE")
    private Integer sosokCodeCode;

    @Column(name = "SOSOK_CODE_NAME", length = 50, nullable = false)
    private String sosokCodeName;

    // 생성자
    public SosokCode(Integer sosokCodeCode, String sosokCodeName) {
        this.sosokCodeCode = sosokCodeCode;
        this.sosokCodeName = sosokCodeName;
    }

    // 표시명 반환 (코드명)
    public String getDisplayName() {
        return this.sosokCodeName;
    }

    @Override
    public String toString() {
        return String.format("SosokCode{code=%d, name='%s'}", sosokCodeCode, sosokCodeName);
    }
} 