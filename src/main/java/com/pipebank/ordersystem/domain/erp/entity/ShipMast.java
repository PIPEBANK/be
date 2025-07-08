package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sa_ship_mast")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ShipMast.ShipMastId.class)
public class ShipMast {

    @Id
    @Column(name = "SHIP_MAST_DATE", columnDefinition = "char(8)", nullable = false)
    private String shipMastDate; // 출하일자

    @Id
    @Column(name = "SHIP_MAST_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastSosok; // 소속

    @Id
    @Column(name = "SHIP_MAST_UJCD", columnDefinition = "char(10)", nullable = false)
    private String shipMastUjcd; // 업장

    @Id
    @Column(name = "SHIP_MAST_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastAcno; // 출하번호

    @Column(name = "SHIP_MAST_CUST", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastCust; // 거래처

    @Column(name = "SHIP_MAST_NADDR", length = 200, nullable = true)
    private String shipMastNaddr; // 납품주소

    @Column(name = "SHIP_MAST_SAWON", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastSawon; // 담당자

    @Column(name = "SHIP_MAST_SAWON_BUSE", columnDefinition = "int(11)", nullable = false)
    private Integer shipMastSawonBuse; // 담당자부서

    @Column(name = "SHIP_MAST_TDIV", columnDefinition = "char(10)", nullable = false)
    private String shipMastTdiv; // 거래구분

    @Column(name = "SHIP_MAST_PROJECT", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastProject; // 프로젝트

    @Column(name = "SHIP_MAST_CARNO", length = 50, nullable = false)
    private String shipMastCarno; // 차량번호

    @Column(name = "SHIP_MAST_REMARK", length = 2000, nullable = false)
    private String shipMastRemark; // 비고

    @Column(name = "SHIP_MAST_FDATE", nullable = false)
    private LocalDateTime shipMastFdate; // 최초등록일

    @Column(name = "SHIP_MAST_FUSER", length = 20, nullable = false)
    private String shipMastFuser; // 최초등록자

    @Column(name = "SHIP_MAST_LDATE", nullable = false)
    private LocalDateTime shipMastLdate; // 최종수정일

    @Column(name = "SHIP_MAST_LUSER", length = 20, nullable = false)
    private String shipMastLuser; // 최종수정자

    @Column(name = "SHIP_MAST_TCOMNAME", length = 50, nullable = true)
    private String shipMastTcomname; // 운송회사명

    @Column(name = "SHIP_MAST_TTEL", length = 20, nullable = true)
    private String shipMastTtel; // 운송회사전화

    @Column(name = "SHIP_MAST_TNAME", length = 20, nullable = true)
    private String shipMastTname; // 운송기사명

    @Column(name = "SHIP_MAST_SNAME", length = 20, nullable = true)
    private String shipMastSname; // 출하담당자명

    @Column(name = "SHIP_MAST_STEL", length = 20, nullable = true)
    private String shipMastStel; // 출하담당자전화

    @Column(name = "SHIP_MAST_SHIPPING", precision = 18, scale = 0, nullable = true)
    private BigDecimal shipMastShipping; // 운송비

    @Column(name = "SHIP_MAST_CARTON", columnDefinition = "char(10)", nullable = true)
    private String shipMastCarton; // 포장구분

    @Column(name = "SHIP_MAST_COMADDR1", length = 100, nullable = true)
    private String shipMastComaddr1; // 현장주소1

    @Column(name = "SHIP_MAST_COMADDR2", length = 100, nullable = true)
    private String shipMastComaddr2; // 현장주소2

    @Column(name = "SHIP_MAST_COMNAME", length = 50, nullable = true)
    private String shipMastComname; // 현장명

    @Column(name = "SHIP_MAST_COMUNAME", length = 100, nullable = true)
    private String shipMastComuname; // 인수자명

    @Column(name = "SHIP_MAST_COMUTEL", length = 30, nullable = true)
    private String shipMastComutel; // 인수자전화

    @Column(name = "SHIP_MAST_TCOMDIV", columnDefinition = "char(10)", nullable = true)
    private String shipMastTcomdiv; // 운송구분

    @Column(name = "SHIP_MAST_TCOMDIV2", columnDefinition = "char(10)", nullable = true)
    private String shipMastTcomdiv2; // 운송구분2

    @Column(name = "SHIP_MAST_PCOD", columnDefinition = "char(10)", nullable = true)
    private String shipMastPcod; // 포장코드

    @Column(name = "SHIP_MAST_CCOD", columnDefinition = "char(10)", nullable = true)
    private String shipMastCcod; // 운송코드

    @Column(name = "SHIP_MAST_OTIME", columnDefinition = "char(2)", nullable = false)
    private String shipMastOtime; // 출하시간

    @Column(name = "SHIP_MAST_CTNO", length = 30, nullable = true)
    private String shipMastCtno; // 컨테이너번호

    @Column(name = "SHIP_MAST_SEALNO", length = 30, nullable = true)
    private String shipMastSealno; // 봉인번호

    @Builder
    public ShipMast(String shipMastDate, Integer shipMastSosok, String shipMastUjcd, Integer shipMastAcno,
                    Integer shipMastCust, String shipMastNaddr, Integer shipMastSawon, Integer shipMastSawonBuse,
                    String shipMastTdiv, Integer shipMastProject, String shipMastCarno, String shipMastRemark,
                    LocalDateTime shipMastFdate, String shipMastFuser, LocalDateTime shipMastLdate, String shipMastLuser,
                    String shipMastTcomname, String shipMastTtel, String shipMastTname, String shipMastSname,
                    String shipMastStel, BigDecimal shipMastShipping, String shipMastCarton, String shipMastComaddr1,
                    String shipMastComaddr2, String shipMastComname, String shipMastComuname, String shipMastComutel,
                    String shipMastTcomdiv, String shipMastTcomdiv2, String shipMastPcod, String shipMastCcod,
                    String shipMastOtime, String shipMastCtno, String shipMastSealno) {
        this.shipMastDate = shipMastDate;
        this.shipMastSosok = shipMastSosok;
        this.shipMastUjcd = shipMastUjcd;
        this.shipMastAcno = shipMastAcno;
        this.shipMastCust = shipMastCust;
        this.shipMastNaddr = shipMastNaddr;
        this.shipMastSawon = shipMastSawon;
        this.shipMastSawonBuse = shipMastSawonBuse;
        this.shipMastTdiv = shipMastTdiv;
        this.shipMastProject = shipMastProject;
        this.shipMastCarno = shipMastCarno;
        this.shipMastRemark = shipMastRemark;
        this.shipMastFdate = shipMastFdate;
        this.shipMastFuser = shipMastFuser;
        this.shipMastLdate = shipMastLdate;
        this.shipMastLuser = shipMastLuser;
        this.shipMastTcomname = shipMastTcomname;
        this.shipMastTtel = shipMastTtel;
        this.shipMastTname = shipMastTname;
        this.shipMastSname = shipMastSname;
        this.shipMastStel = shipMastStel;
        this.shipMastShipping = shipMastShipping;
        this.shipMastCarton = shipMastCarton;
        this.shipMastComaddr1 = shipMastComaddr1;
        this.shipMastComaddr2 = shipMastComaddr2;
        this.shipMastComname = shipMastComname;
        this.shipMastComuname = shipMastComuname;
        this.shipMastComutel = shipMastComutel;
        this.shipMastTcomdiv = shipMastTcomdiv;
        this.shipMastTcomdiv2 = shipMastTcomdiv2;
        this.shipMastPcod = shipMastPcod;
        this.shipMastCcod = shipMastCcod;
        this.shipMastOtime = shipMastOtime;
        this.shipMastCtno = shipMastCtno;
        this.shipMastSealno = shipMastSealno;
    }

    // 비즈니스 메서드
    public String getShipKey() {
        return shipMastDate + "-" + shipMastSosok + "-" + shipMastUjcd + "-" + shipMastAcno;
    }

    public String getFullAddress() {
        if (shipMastComaddr1 != null && shipMastComaddr2 != null) {
            return shipMastComaddr1 + " " + shipMastComaddr2;
        } else if (shipMastComaddr1 != null) {
            return shipMastComaddr1;
        } else if (shipMastComaddr2 != null) {
            return shipMastComaddr2;
        }
        return "";
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ShipMastId implements Serializable {
        private String shipMastDate;
        private Integer shipMastSosok;
        private String shipMastUjcd;
        private Integer shipMastAcno;

        public ShipMastId(String shipMastDate, Integer shipMastSosok, String shipMastUjcd, Integer shipMastAcno) {
            this.shipMastDate = shipMastDate;
            this.shipMastSosok = shipMastSosok;
            this.shipMastUjcd = shipMastUjcd;
            this.shipMastAcno = shipMastAcno;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ShipMastId that = (ShipMastId) o;

            if (!shipMastDate.equals(that.shipMastDate)) return false;
            if (!shipMastSosok.equals(that.shipMastSosok)) return false;
            if (!shipMastUjcd.equals(that.shipMastUjcd)) return false;
            return shipMastAcno.equals(that.shipMastAcno);
        }

        @Override
        public int hashCode() {
            int result = shipMastDate.hashCode();
            result = 31 * result + shipMastSosok.hashCode();
            result = 31 * result + shipMastUjcd.hashCode();
            result = 31 * result + shipMastAcno.hashCode();
            return result;
        }
    }
} 