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
 * 품목 제품군 코드 엔티티 (DIV2)
 * co_item_div2 테이블 매핑
 * 제품종류(1) > 제품군(2) > 제품용도(3) > 제품기능(4) 구조의 2단계
 */
@Entity
@Table(name = "co_item_div2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ItemDiv2.ItemDiv2Id.class)
public class ItemDiv2 {

    @Id
    @Column(name = "ITEM_DIV2_DIV1", columnDefinition = "char(1)", nullable = false)
    private String itemDiv2Div1; // 제품종류 코드 (DIV1 참조)

    @Id
    @Column(name = "ITEM_DIV2_CODE", columnDefinition = "char(1)", nullable = false)
    private String itemDiv2Code; // 제품군 코드

    @Column(name = "ITEM_DIV2_NAME", nullable = false, length = 100)
    private String itemDiv2Name; // 제품군명

    @Column(name = "ITEM_DIV2_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemDiv2Use; // 사용여부

    @Column(name = "ITEM_DIV2_FDATE", nullable = false)
    private LocalDateTime itemDiv2Fdate; // 최초등록일

    @Column(name = "ITEM_DIV2_FUSER", nullable = false, length = 20)
    private String itemDiv2Fuser; // 최초등록자

    @Column(name = "ITEM_DIV2_LDATE", nullable = false)
    private LocalDateTime itemDiv2Ldate; // 최종수정일

    @Column(name = "ITEM_DIV2_LUSER", nullable = false, length = 20)
    private String itemDiv2Luser; // 최종수정자

    // 생성자
    public ItemDiv2(String itemDiv2Div1, String itemDiv2Code, String itemDiv2Name, Integer itemDiv2Use,
                   LocalDateTime itemDiv2Fdate, String itemDiv2Fuser,
                   LocalDateTime itemDiv2Ldate, String itemDiv2Luser) {
        this.itemDiv2Div1 = itemDiv2Div1;
        this.itemDiv2Code = itemDiv2Code;
        this.itemDiv2Name = itemDiv2Name;
        this.itemDiv2Use = itemDiv2Use;
        this.itemDiv2Fdate = itemDiv2Fdate;
        this.itemDiv2Fuser = itemDiv2Fuser;
        this.itemDiv2Ldate = itemDiv2Ldate;
        this.itemDiv2Luser = itemDiv2Luser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return itemDiv2Use == 1;
    }

    public String getDisplayName() {
        return this.itemDiv2Name;
    }

    public String getFullCode() {
        return itemDiv2Div1 + itemDiv2Code;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ItemDiv2Id implements Serializable {
        private String itemDiv2Div1;
        private String itemDiv2Code;

        public ItemDiv2Id(String itemDiv2Div1, String itemDiv2Code) {
            this.itemDiv2Div1 = itemDiv2Div1;
            this.itemDiv2Code = itemDiv2Code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemDiv2Id that = (ItemDiv2Id) o;

            if (!itemDiv2Div1.equals(that.itemDiv2Div1)) return false;
            return itemDiv2Code.equals(that.itemDiv2Code);
        }

        @Override
        public int hashCode() {
            int result = itemDiv2Div1.hashCode();
            result = 31 * result + itemDiv2Code.hashCode();
            return result;
        }
    }
} 