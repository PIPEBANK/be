package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사원 정보 엔티티
 * IN_INSA_MAST 테이블 매핑
 */
@Entity
@Table(name = "IN_INSA_MAST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsaMast {

    @Id
    @Column(name = "INSA_MAST_SANO")
    private Integer insaMastSano;

    @Column(name = "INSA_MAST_KNAM", length = 50, nullable = false)
    private String insaMastKnam;

    // 생성자
    public InsaMast(Integer insaMastSano, String insaMastKnam) {
        this.insaMastSano = insaMastSano;
        this.insaMastKnam = insaMastKnam;
    }

    // 표시명 반환 (사원명)
    public String getDisplayName() {
        return this.insaMastKnam;
    }

    @Override
    public String toString() {
        return String.format("InsaMast{sano=%d, name='%s'}", insaMastSano, insaMastKnam);
    }
} 