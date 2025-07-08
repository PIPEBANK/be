package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "sa_ship_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ShipOrder.ShipOrderId.class)
public class ShipOrder {

    @Id
    @Column(name = "SHIP_ORDER_DATE", columnDefinition = "char(8)", nullable = false)
    private String shipOrderDate; // 출하일자

    @Id
    @Column(name = "SHIP_ORDER_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer shipOrderSosok; // 소속

    @Id
    @Column(name = "SHIP_ORDER_UJCD", columnDefinition = "char(10)", nullable = false)
    private String shipOrderUjcd; // 업장

    @Id
    @Column(name = "SHIP_ORDER_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer shipOrderAcno; // 출하번호

    @Id
    @Column(name = "SHIP_ORDER_SEQ", columnDefinition = "int(10)", nullable = false)
    private Integer shipOrderSeq; // 순번

    @Column(name = "SHIP_ORDER_ODATE", columnDefinition = "char(8)", nullable = false)
    private String shipOrderOdate; // 주문일자

    @Column(name = "SHIP_ORDER_OACNO", columnDefinition = "int(10)", nullable = false)
    private Integer shipOrderOacno; // 주문번호

    @Column(name = "SHIP_ORDER_OSEQ", columnDefinition = "int(10)", nullable = false)
    private Integer shipOrderOseq; // 주문순번

    @Builder
    public ShipOrder(String shipOrderDate, Integer shipOrderSosok, String shipOrderUjcd, Integer shipOrderAcno,
                     Integer shipOrderSeq, String shipOrderOdate, Integer shipOrderOacno, Integer shipOrderOseq) {
        this.shipOrderDate = shipOrderDate;
        this.shipOrderSosok = shipOrderSosok;
        this.shipOrderUjcd = shipOrderUjcd;
        this.shipOrderAcno = shipOrderAcno;
        this.shipOrderSeq = shipOrderSeq;
        this.shipOrderOdate = shipOrderOdate;
        this.shipOrderOacno = shipOrderOacno;
        this.shipOrderOseq = shipOrderOseq;
    }

    // 비즈니스 메서드
    public String getShipOrderKey() {
        return shipOrderDate + "-" + shipOrderSosok + "-" + shipOrderUjcd + "-" + shipOrderAcno + "-" + shipOrderSeq;
    }

    public String getShipMastKey() {
        return shipOrderDate + "-" + shipOrderSosok + "-" + shipOrderUjcd + "-" + shipOrderAcno;
    }

    public String getOrderKey() {
        return shipOrderOdate + "-" + shipOrderSosok + "-" + shipOrderUjcd + "-" + shipOrderOacno;
    }

    public String getOrderTranKey() {
        return shipOrderOdate + "-" + shipOrderSosok + "-" + shipOrderUjcd + "-" + shipOrderOacno + "-" + shipOrderOseq;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ShipOrderId implements Serializable {
        private String shipOrderDate;
        private Integer shipOrderSosok;
        private String shipOrderUjcd;
        private Integer shipOrderAcno;
        private Integer shipOrderSeq;

        public ShipOrderId(String shipOrderDate, Integer shipOrderSosok, String shipOrderUjcd, Integer shipOrderAcno, Integer shipOrderSeq) {
            this.shipOrderDate = shipOrderDate;
            this.shipOrderSosok = shipOrderSosok;
            this.shipOrderUjcd = shipOrderUjcd;
            this.shipOrderAcno = shipOrderAcno;
            this.shipOrderSeq = shipOrderSeq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ShipOrderId that = (ShipOrderId) o;

            if (!shipOrderDate.equals(that.shipOrderDate)) return false;
            if (!shipOrderSosok.equals(that.shipOrderSosok)) return false;
            if (!shipOrderUjcd.equals(that.shipOrderUjcd)) return false;
            if (!shipOrderAcno.equals(that.shipOrderAcno)) return false;
            return shipOrderSeq.equals(that.shipOrderSeq);
        }

        @Override
        public int hashCode() {
            int result = shipOrderDate.hashCode();
            result = 31 * result + shipOrderSosok.hashCode();
            result = 31 * result + shipOrderUjcd.hashCode();
            result = 31 * result + shipOrderAcno.hashCode();
            result = 31 * result + shipOrderSeq.hashCode();
            return result;
        }
    }
} 