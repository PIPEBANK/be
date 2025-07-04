package com.pipebank.ordersystem.domain.erp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 품목 제품용도 코드 엔티티 (DIV3)
 * co_item_div3 테이블 매핑
 * 제품종류(1) > 제품군(2) > 제품용도(3) > 제품기능(4) 구조의 3단계
 */
@Entity
@Table(name = "co_item_div3")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ItemDiv3.ItemDiv3Id.class)
public class ItemDiv3 {

    @Id
    @Column(name = "ITEM_DIV3_DIV1", columnDefinition = "char(1)", nullable = false)
    private String itemDiv3Div1; // 제품종류 코드 (DIV1 참조)

    @Id
    @Column(name = "ITEM_DIV3_DIV2", columnDefinition = "char(1)", nullable = false)
    private String itemDiv3Div2; // 제품군 코드 (DIV2 참조)

    @Id
    @Column(name = "ITEM_DIV3_CODE", columnDefinition = "char(2)", nullable = false)
    private String itemDiv3Code; // 제품용도 코드

    @Column(name = "ITEM_DIV3_NAME", nullable = false, length = 100)
    private String itemDiv3Name; // 제품용도명

    @Column(name = "ITEM_DIV3_DCOD", nullable = false, columnDefinition = "char(10)")
    private String itemDiv3Dcod; // D코드 (추가 구분코드)

    @Column(name = "ITEM_DIV3_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemDiv3Use; // 사용여부

    @Column(name = "ITEM_DIV3_FDATE", nullable = false)
    private LocalDateTime itemDiv3Fdate; // 최초등록일

    @Column(name = "ITEM_DIV3_FUSER", nullable = false, length = 20)
    private String itemDiv3Fuser; // 최초등록자

    @Column(name = "ITEM_DIV3_LDATE", nullable = false)
    private LocalDateTime itemDiv3Ldate; // 최종수정일

    @Column(name = "ITEM_DIV3_LUSER", nullable = false, length = 20)
    private String itemDiv3Luser; // 최종수정자

    // 생성자
    public ItemDiv3(String itemDiv3Div1, String itemDiv3Div2, String itemDiv3Code, String itemDiv3Name,
                   String itemDiv3Dcod, Integer itemDiv3Use, LocalDateTime itemDiv3Fdate, String itemDiv3Fuser,
                   LocalDateTime itemDiv3Ldate, String itemDiv3Luser) {
        this.itemDiv3Div1 = itemDiv3Div1;
        this.itemDiv3Div2 = itemDiv3Div2;
        this.itemDiv3Code = itemDiv3Code;
        this.itemDiv3Name = itemDiv3Name;
        this.itemDiv3Dcod = itemDiv3Dcod;
        this.itemDiv3Use = itemDiv3Use;
        this.itemDiv3Fdate = itemDiv3Fdate;
        this.itemDiv3Fuser = itemDiv3Fuser;
        this.itemDiv3Ldate = itemDiv3Ldate;
        this.itemDiv3Luser = itemDiv3Luser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return itemDiv3Use == 1;
    }

    public String getDisplayName() {
        return this.itemDiv3Name;
    }

    public String getFullCode() {
        return itemDiv3Div1 + itemDiv3Div2 + itemDiv3Code;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ItemDiv3Id implements Serializable {
        private String itemDiv3Div1;
        private String itemDiv3Div2;
        private String itemDiv3Code;

        public ItemDiv3Id(String itemDiv3Div1, String itemDiv3Div2, String itemDiv3Code) {
            this.itemDiv3Div1 = itemDiv3Div1;
            this.itemDiv3Div2 = itemDiv3Div2;
            this.itemDiv3Code = itemDiv3Code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemDiv3Id that = (ItemDiv3Id) o;

            if (!itemDiv3Div1.equals(that.itemDiv3Div1)) return false;
            if (!itemDiv3Div2.equals(that.itemDiv3Div2)) return false;
            return itemDiv3Code.equals(that.itemDiv3Code);
        }

        @Override
        public int hashCode() {
            int result = itemDiv3Div1.hashCode();
            result = 31 * result + itemDiv3Div2.hashCode();
            result = 31 * result + itemDiv3Code.hashCode();
            return result;
        }
    }
} 