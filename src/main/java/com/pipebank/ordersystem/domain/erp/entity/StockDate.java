package com.pipebank.ordersystem.domain.erp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "co_stock_date")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(StockDate.StockDateId.class)
public class StockDate {

    @Id
    @Column(name = "STOCK_DATE_DCOD", columnDefinition = "char(10)", nullable = false)
    private String stockDateDcod; // 창고코드

    @Id
    @Column(name = "STOCK_DATE_ITEM", columnDefinition = "int(10)", nullable = false)
    private Integer stockDateItem; // 품목코드

    @Id
    @Column(name = "STOCK_DATE_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer stockDateSosok; // 소속

    @Id
    @Column(name = "STOCK_DATE_BUSE", columnDefinition = "int(10)", nullable = false)
    private Integer stockDateBuse; // 부서

    @Column(name = "STOCK_DATE_CNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal stockDateCnt; // 재고수량

    @Column(name = "STOCK_DATE_FDATE", nullable = false)
    private LocalDateTime stockDateFdate; // 최초등록일

    @Column(name = "STOCK_DATE_FUSER", nullable = false, length = 20)
    private String stockDateFuser; // 최초등록자

    @Column(name = "STOCK_DATE_LDATE", nullable = false)
    private LocalDateTime stockDateLdate; // 최종수정일

    @Column(name = "STOCK_DATE_LUSER", nullable = false, length = 20)
    private String stockDateLuser; // 최종수정자

    @Builder
    public StockDate(String stockDateDcod, Integer stockDateItem, Integer stockDateSosok, Integer stockDateBuse,
                    BigDecimal stockDateCnt, LocalDateTime stockDateFdate, String stockDateFuser,
                    LocalDateTime stockDateLdate, String stockDateLuser) {
        this.stockDateDcod = stockDateDcod;
        this.stockDateItem = stockDateItem;
        this.stockDateSosok = stockDateSosok;
        this.stockDateBuse = stockDateBuse;
        this.stockDateCnt = stockDateCnt;
        this.stockDateFdate = stockDateFdate;
        this.stockDateFuser = stockDateFuser;
        this.stockDateLdate = stockDateLdate;
        this.stockDateLuser = stockDateLuser;
    }

    // 비즈니스 메서드
    public String getStockKey() {
        return stockDateDcod + "-" + stockDateItem + "-" + stockDateSosok + "-" + stockDateBuse;
    }

    // 재고 보유 여부 확인
    public boolean hasStock() {
        return stockDateCnt != null && stockDateCnt.compareTo(BigDecimal.ZERO) > 0;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class StockDateId implements Serializable {
        private String stockDateDcod;
        private Integer stockDateItem;
        private Integer stockDateSosok;
        private Integer stockDateBuse;

        public StockDateId(String stockDateDcod, Integer stockDateItem, Integer stockDateSosok, Integer stockDateBuse) {
            this.stockDateDcod = stockDateDcod;
            this.stockDateItem = stockDateItem;
            this.stockDateSosok = stockDateSosok;
            this.stockDateBuse = stockDateBuse;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StockDateId that = (StockDateId) o;

            if (!stockDateDcod.equals(that.stockDateDcod)) return false;
            if (!stockDateItem.equals(that.stockDateItem)) return false;
            if (!stockDateSosok.equals(that.stockDateSosok)) return false;
            return stockDateBuse.equals(that.stockDateBuse);
        }

        @Override
        public int hashCode() {
            int result = stockDateDcod.hashCode();
            result = 31 * result + stockDateItem.hashCode();
            result = 31 * result + stockDateSosok.hashCode();
            result = 31 * result + stockDateBuse.hashCode();
            return result;
        }
    }
} 