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
 * 품목 제품기능 코드 엔티티 (DIV4)
 * co_item_div4 테이블 매핑
 * 제품종류(1) > 제품군(2) > 제품용도(3) > 제품기능(4) 구조의 최하위
 */
@Entity
@Table(name = "co_item_div4")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ItemDiv4.ItemDiv4Id.class)
public class ItemDiv4 {

    @Id
    @Column(name = "ITEM_DIV4_DIV1", columnDefinition = "char(1)", nullable = false)
    private String itemDiv4Div1; // 제품종류 코드 (DIV1 참조)

    @Id
    @Column(name = "ITEM_DIV4_DIV2", columnDefinition = "char(1)", nullable = false)
    private String itemDiv4Div2; // 제품군 코드 (DIV2 참조)

    @Id
    @Column(name = "ITEM_DIV4_DIV3", columnDefinition = "char(2)", nullable = false)
    private String itemDiv4Div3; // 제품용도 코드 (DIV3 참조)

    @Id
    @Column(name = "ITEM_DIV4_CODE", columnDefinition = "char(2)", nullable = false)
    private String itemDiv4Code; // 제품기능 코드

    @Column(name = "ITEM_DIV4_NAME", nullable = false, length = 100)
    private String itemDiv4Name; // 제품기능명

    @Column(name = "ITEM_DIV4_CCOD", nullable = true, columnDefinition = "char(10)")
    private String itemDiv4Ccod; // 제무분류코드 (코드관리테이블)

    @Column(name = "ITEM_DIV4_ORDER", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemDiv4Order; // 오더센터 사용유무

    @Column(name = "ITEM_DIV4_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemDiv4Use; // 사용여부

    @Column(name = "ITEM_DIV4_FDATE", nullable = false)
    private LocalDateTime itemDiv4Fdate; // 최초등록일

    @Column(name = "ITEM_DIV4_FUSER", nullable = false, length = 20)
    private String itemDiv4Fuser; // 최초등록자

    @Column(name = "ITEM_DIV4_LDATE", nullable = false)
    private LocalDateTime itemDiv4Ldate; // 최종수정일

    @Column(name = "ITEM_DIV4_LUSER", nullable = false, length = 20)
    private String itemDiv4Luser; // 최종수정자

    // 생성자
    public ItemDiv4(String itemDiv4Div1, String itemDiv4Div2, String itemDiv4Div3, String itemDiv4Code,
                   String itemDiv4Name, String itemDiv4Ccod, Integer itemDiv4Order, Integer itemDiv4Use,
                   LocalDateTime itemDiv4Fdate, String itemDiv4Fuser,
                   LocalDateTime itemDiv4Ldate, String itemDiv4Luser) {
        this.itemDiv4Div1 = itemDiv4Div1;
        this.itemDiv4Div2 = itemDiv4Div2;
        this.itemDiv4Div3 = itemDiv4Div3;
        this.itemDiv4Code = itemDiv4Code;
        this.itemDiv4Name = itemDiv4Name;
        this.itemDiv4Ccod = itemDiv4Ccod;
        this.itemDiv4Order = itemDiv4Order;
        this.itemDiv4Use = itemDiv4Use;
        this.itemDiv4Fdate = itemDiv4Fdate;
        this.itemDiv4Fuser = itemDiv4Fuser;
        this.itemDiv4Ldate = itemDiv4Ldate;
        this.itemDiv4Luser = itemDiv4Luser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return itemDiv4Use == 1;
    }

    public boolean isOrderable() {
        return itemDiv4Order == 1;
    }

    public String getDisplayName() {
        return this.itemDiv4Name;
    }

    public String getFullCode() {
        return itemDiv4Div1 + itemDiv4Div2 + itemDiv4Div3 + itemDiv4Code;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ItemDiv4Id implements Serializable {
        private String itemDiv4Div1;
        private String itemDiv4Div2;
        private String itemDiv4Div3;
        private String itemDiv4Code;

        public ItemDiv4Id(String itemDiv4Div1, String itemDiv4Div2, String itemDiv4Div3, String itemDiv4Code) {
            this.itemDiv4Div1 = itemDiv4Div1;
            this.itemDiv4Div2 = itemDiv4Div2;
            this.itemDiv4Div3 = itemDiv4Div3;
            this.itemDiv4Code = itemDiv4Code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemDiv4Id that = (ItemDiv4Id) o;

            if (!itemDiv4Div1.equals(that.itemDiv4Div1)) return false;
            if (!itemDiv4Div2.equals(that.itemDiv4Div2)) return false;
            if (!itemDiv4Div3.equals(that.itemDiv4Div3)) return false;
            return itemDiv4Code.equals(that.itemDiv4Code);
        }

        @Override
        public int hashCode() {
            int result = itemDiv4Div1.hashCode();
            result = 31 * result + itemDiv4Div2.hashCode();
            result = 31 * result + itemDiv4Div3.hashCode();
            result = 31 * result + itemDiv4Code.hashCode();
            return result;
        }
    }
} 