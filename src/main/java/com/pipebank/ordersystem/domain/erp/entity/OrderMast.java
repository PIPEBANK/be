package com.pipebank.ordersystem.domain.erp.entity;

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
    @Column(name = "ORDER_MAST_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer orderMastSosok; //소속

    @Id
    @Column(name = "ORDER_MAST_UJCD", columnDefinition = "char(10)", nullable = false)
    private String orderMastUjcd; //업장

    @Id
    @Column(name = "ORDER_MAST_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer orderMastAcno;

    @Column(name = "ORDER_MAST_CUST", columnDefinition = "int(10)", nullable = false)
    private Integer orderMastCust; //거래처

    @Column(name = "ORDER_MAST_SCUST", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastScust;   //매출 거래처

    @Column(name = "ORDER_MAST_SAWON", columnDefinition = "int(10)", nullable = false)
    private Integer orderMastSawon; //담당자

    @Column(name = "ORDER_MAST_SAWON_BUSE", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastSawonBuse; //담당자부서

    @Column(name = "ORDER_MAST_ODATE", columnDefinition = "char(8)", nullable = false)
    private String orderMastOdate;  // 납기일자

    @Column(name = "ORDER_MAST_PROJECT", columnDefinition = "int(10)", nullable = false)
    private Integer orderMastProject;  //프로젝트

    @Column(name = "ORDER_MAST_REMARK", nullable = false, length = 2000)
    private String orderMastRemark;  //비고

    @Column(name = "ORDER_MAST_FDATE", nullable = false)
    private LocalDateTime orderMastFdate;

    @Column(name = "ORDER_MAST_FUSER", nullable = false, length = 20)
    private String orderMastFuser;

    @Column(name = "ORDER_MAST_LDATE", nullable = false)
    private LocalDateTime orderMastLdate;

    @Column(name = "ORDER_MAST_LUSER", nullable = false, length = 20)
    private String orderMastLuser;

    @Column(name = "ORDER_MAST_COMADDR1", nullable = true, length = 100)
    private String orderMastComaddr1;  // 납품현장 기본주소

    @Column(name = "ORDER_MAST_COMADDR2", nullable = true, length = 100)
    private String orderMastComaddr2; // 납품현장 상세주소

    @Column(name = "ORDER_MAST_COMNAME", nullable = true, length = 100)
    private String orderMastComname;  // 현장명

    @Column(name = "ORDER_MAST_COMUNAME", nullable = true, length = 50)
    private String orderMastComuname; //인수자

    @Column(name = "ORDER_MAST_COMUTEL", nullable = true, length = 30)
    private String orderMastComutel;  //인수자연락처

    @Column(name = "ORDER_MAST_REASON", columnDefinition = "char(10)", nullable = true)
    private String orderMastReason;  // 용도구분 (코드관리테이블)

    @Column(name = "ORDER_MAST_TCOMDIV", columnDefinition = "char(10)", nullable = true)
    private String orderMastTcomdiv;  //운송구분 (코드관리테이블)

    @Column(name = "ORDER_MAST_CURRENCY", columnDefinition = "char(10)", nullable = true)
    private String orderMastCurrency; //화폐코드 (코드관리테이블)

    @Column(name = "ORDER_MAST_CURRENCY_PER", nullable = true, length = 20)
    private String orderMastCurrencyPer; //환율

    @Column(name = "ORDER_MAST_SDIV", columnDefinition = "char(10)", nullable = true)
    private String orderMastSdiv; // 출고형태코드 (코드관리테이블)

    @Column(name = "ORDER_MAST_DCUST", nullable = true, length = 200)
    private String orderMastDcust; // 수요처

    @Column(name = "ORDER_MAST_INTYPE", columnDefinition = "char(10)", nullable = true)
    private String orderMastIntype;  // 등록구분 (코드관리테이블)

    @Column(name = "ORDER_MAST_OTIME", columnDefinition = "char(2)", nullable = false)
    private String orderMastOtime;  //납기시간

    @Builder
    public OrderMast(String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno,
                    Integer orderMastCust, Integer orderMastScust, Integer orderMastSawon, Integer orderMastSawonBuse,
                    String orderMastOdate, Integer orderMastProject, String orderMastRemark,
                    LocalDateTime orderMastFdate, String orderMastFuser, LocalDateTime orderMastLdate, String orderMastLuser,
                    String orderMastComaddr1, String orderMastComaddr2, String orderMastComname, String orderMastComuname,
                    String orderMastComutel, String orderMastReason, String orderMastTcomdiv, String orderMastCurrency,
                    String orderMastCurrencyPer, String orderMastSdiv, String orderMastDcust, String orderMastIntype,
                    String orderMastOtime) {
        this.orderMastDate = orderMastDate;
        this.orderMastSosok = orderMastSosok;
        this.orderMastUjcd = orderMastUjcd;
        this.orderMastAcno = orderMastAcno;
        this.orderMastCust = orderMastCust;
        this.orderMastScust = orderMastScust;
        this.orderMastSawon = orderMastSawon;
        this.orderMastSawonBuse = orderMastSawonBuse;
        this.orderMastOdate = orderMastOdate;
        this.orderMastProject = orderMastProject;
        this.orderMastRemark = orderMastRemark;
        this.orderMastFdate = orderMastFdate;
        this.orderMastFuser = orderMastFuser;
        this.orderMastLdate = orderMastLdate;
        this.orderMastLuser = orderMastLuser;
        this.orderMastComaddr1 = orderMastComaddr1;
        this.orderMastComaddr2 = orderMastComaddr2;
        this.orderMastComname = orderMastComname;
        this.orderMastComuname = orderMastComuname;
        this.orderMastComutel = orderMastComutel;
        this.orderMastReason = orderMastReason;
        this.orderMastTcomdiv = orderMastTcomdiv;
        this.orderMastCurrency = orderMastCurrency;
        this.orderMastCurrencyPer = orderMastCurrencyPer;
        this.orderMastSdiv = orderMastSdiv;
        this.orderMastDcust = orderMastDcust;
        this.orderMastIntype = orderMastIntype;
        this.orderMastOtime = orderMastOtime;
    }

    // 비즈니스 메서드
    public String getOrderKey() {
        return orderMastDate + "-" + orderMastSosok + "-" + orderMastUjcd + "-" + orderMastAcno;
    }

    public String getFullAddress() {
        if (orderMastComaddr1 != null && orderMastComaddr2 != null) {
            return orderMastComaddr1 + " " + orderMastComaddr2;
        } else if (orderMastComaddr1 != null) {
            return orderMastComaddr1;
        } else if (orderMastComaddr2 != null) {
            return orderMastComaddr2;
        }
        return "";
    }

    public String getDisplayName() {
        return orderMastComname != null ? orderMastComname : orderMastDcust;
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