package com.weborder.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sa_order_mast")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(OrderMast.OrderMastId.class)
public class OrderMast {

    @Id
    @Column(name = "ORDER_MAST_DATE", columnDefinition = "char(8)", nullable = false)
    private String orderMastDate; //주문일자

    @Id
    @Column(name = "ORDER_MAST_SOSOK", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastSosok; //소속

    @Id
    @Column(name = "ORDER_MAST_UJCD", columnDefinition = "char(10)", nullable = false)
    private String orderMastUjcd; //업장

    @Id
    @Column(name = "ORDER_MAST_ACNO", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastAcno;

    @Column(name = "ORDER_MAST_CUST", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastCust; //거래처

    @Column(name = "ORDER_MAST_SAWON", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastSawon; //담당자

    @Column(name = "ORDER_MAST_ODATE", columnDefinition = "varchar(100)", nullable = false)
    private String orderMastOdate;  // 납기일자

    @Column(name = "ORDER_MAST_PROJECT", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastProject;  //프로젝트

    @Column(name = "ORDER_MAST_REMARK", nullable = false, length = 200)
    private String orderMastRemark;  //비고

    @Column(name = "ORDER_MAST_FDATE", nullable = false)
    private LocalDateTime orderMastFdate;

    @Column(name = "ORDER_MAST_FUSER", nullable = false, length = 20)
    private String orderMastFuser;

    @Column(name = "ORDER_MAST_LDATE", nullable = false)
    private LocalDateTime orderMastLdate;

    @Column(name = "ORDER_MAST_LUSER", nullable = false, length = 20)
    private String orderMastLuser;

    @Builder
    public OrderMast(String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno,
                    Integer orderMastCust, Integer orderMastSawon,
                    String orderMastOdate, Integer orderMastProject, String orderMastRemark,
                    LocalDateTime orderMastFdate, String orderMastFuser, LocalDateTime orderMastLdate, String orderMastLuser) {
        this.orderMastDate = orderMastDate;
        this.orderMastSosok = orderMastSosok;
        this.orderMastUjcd = orderMastUjcd;
        this.orderMastAcno = orderMastAcno;
        this.orderMastCust = orderMastCust;
        this.orderMastSawon = orderMastSawon;
        this.orderMastOdate = orderMastOdate;
        this.orderMastProject = orderMastProject;
        this.orderMastRemark = orderMastRemark;
        this.orderMastFdate = orderMastFdate;
        this.orderMastFuser = orderMastFuser;
        this.orderMastLdate = orderMastLdate;
        this.orderMastLuser = orderMastLuser;
    }

    // 비즈니스 메서드
    public String getOrderKey() {
        return orderMastDate + "-" + orderMastSosok + "-" + orderMastUjcd + "-" + orderMastAcno;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class OrderMastId implements Serializable {
        private String orderMastDate;
        private Integer orderMastSosok;
        private String orderMastUjcd;
        private Integer orderMastAcno;

        public OrderMastId(String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno) {
            this.orderMastDate = orderMastDate;
            this.orderMastSosok = orderMastSosok;
            this.orderMastUjcd = orderMastUjcd;
            this.orderMastAcno = orderMastAcno;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderMastId that = (OrderMastId) o;

            if (!orderMastDate.equals(that.orderMastDate)) return false;
            if (!orderMastSosok.equals(that.orderMastSosok)) return false;
            if (!orderMastUjcd.equals(that.orderMastUjcd)) return false;
            return orderMastAcno.equals(that.orderMastAcno);
        }

        @Override
        public int hashCode() {
            int result = orderMastDate.hashCode();
            result = 31 * result + orderMastSosok.hashCode();
            result = 31 * result + orderMastUjcd.hashCode();
            result = 31 * result + orderMastAcno.hashCode();
            return result;
        }
    }
} 