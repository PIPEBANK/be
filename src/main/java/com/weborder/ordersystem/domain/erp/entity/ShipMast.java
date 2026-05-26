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

    @Column(name = "SHIP_MAST_NADDR", length = 200, nullable = false)
    private String shipMastNaddr; // 납품주소

    @Column(name = "SHIP_MAST_SAWON", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastSawon; // 담당자

    @Column(name = "SHIP_MAST_TDIV", columnDefinition = "char(10)", nullable = false)
    private String shipMastTdiv; // 거래구분

    @Column(name = "SHIP_MAST_PROJECT", columnDefinition = "int(10)", nullable = false)
    private Integer shipMastProject; // 프로젝트

    @Column(name = "SHIP_MAST_CARNO", length = 50, nullable = true)
    private String shipMastCarno; // 차량번호

    @Column(name = "SHIP_MAST_REMARK", length = 200, nullable = false)
    private String shipMastRemark; // 비고

    @Column(name = "SHIP_MAST_FDATE", nullable = false)
    private LocalDateTime shipMastFdate; // 최초등록일

    @Column(name = "SHIP_MAST_FUSER", length = 20, nullable = false)
    private String shipMastFuser; // 최초등록자

    @Column(name = "SHIP_MAST_LDATE", nullable = false)
    private LocalDateTime shipMastLdate; // 최종수정일

    @Column(name = "SHIP_MAST_LUSER", length = 20, nullable = false)
    private String shipMastLuser; // 최종수정자

    @Builder
    public ShipMast(String shipMastDate, Integer shipMastSosok, String shipMastUjcd, Integer shipMastAcno,
                    Integer shipMastCust, String shipMastNaddr, Integer shipMastSawon,
                    String shipMastTdiv, Integer shipMastProject, String shipMastCarno, String shipMastRemark,
                    LocalDateTime shipMastFdate, String shipMastFuser, LocalDateTime shipMastLdate, String shipMastLuser) {
        this.shipMastDate = shipMastDate;
        this.shipMastSosok = shipMastSosok;
        this.shipMastUjcd = shipMastUjcd;
        this.shipMastAcno = shipMastAcno;
        this.shipMastCust = shipMastCust;
        this.shipMastNaddr = shipMastNaddr;
        this.shipMastSawon = shipMastSawon;
        this.shipMastTdiv = shipMastTdiv;
        this.shipMastProject = shipMastProject;
        this.shipMastCarno = shipMastCarno;
        this.shipMastRemark = shipMastRemark;
        this.shipMastFdate = shipMastFdate;
        this.shipMastFuser = shipMastFuser;
        this.shipMastLdate = shipMastLdate;
        this.shipMastLuser = shipMastLuser;
    }

    public String getShipKey() {
        return shipMastDate + "-" + shipMastSosok + "-" + shipMastUjcd + "-" + shipMastAcno;
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
