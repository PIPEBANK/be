package com.pipebank.ordersystem.domain.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 품목 제품종류 코드 엔티티 (DIV1)
 * co_item_div1 테이블 매핑
 * 제품종류(1) > 제품군(2) > 제품용도(3) > 제품기능(4) 구조의 최상위
 */
@Entity
@Table(name = "co_item_div1")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDiv1 {

    @Id
    @Column(name = "ITEM_DIV1_CODE", columnDefinition = "char(1)")
    private String itemDiv1Code; // 제품종류 코드

    @Column(name = "ITEM_DIV1_NAME", nullable = false, length = 100)
    private String itemDiv1Name; // 제품종류명

    @Column(name = "ITEM_DIV1_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemDiv1Use; // 사용여부

    @Column(name = "ITEM_DIV1_FDATE", nullable = false)
    private LocalDateTime itemDiv1Fdate; // 최초등록일

    @Column(name = "ITEM_DIV1_FUSER", nullable = false, length = 20)
    private String itemDiv1Fuser; // 최초등록자

    @Column(name = "ITEM_DIV1_LDATE", nullable = false)
    private LocalDateTime itemDiv1Ldate; // 최종수정일

    @Column(name = "ITEM_DIV1_LUSER", nullable = false, length = 20)
    private String itemDiv1Luser; // 최종수정자

    // 생성자
    public ItemDiv1(String itemDiv1Code, String itemDiv1Name, Integer itemDiv1Use,
                   LocalDateTime itemDiv1Fdate, String itemDiv1Fuser,
                   LocalDateTime itemDiv1Ldate, String itemDiv1Luser) {
        this.itemDiv1Code = itemDiv1Code;
        this.itemDiv1Name = itemDiv1Name;
        this.itemDiv1Use = itemDiv1Use;
        this.itemDiv1Fdate = itemDiv1Fdate;
        this.itemDiv1Fuser = itemDiv1Fuser;
        this.itemDiv1Ldate = itemDiv1Ldate;
        this.itemDiv1Luser = itemDiv1Luser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return itemDiv1Use == 1;
    }

    public String getDisplayName() {
        return this.itemDiv1Name;
    }

    @Override
    public String toString() {
        return String.format("ItemDiv1{code='%s', name='%s'}", itemDiv1Code, itemDiv1Name);
    }
} 