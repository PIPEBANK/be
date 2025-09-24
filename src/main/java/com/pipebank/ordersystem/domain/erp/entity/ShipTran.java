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
@Table(name = "sa_ship_tran")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ShipTran.ShipTranId.class)
public class ShipTran {

    @Id
    @Column(name = "SHIP_TRAN_DATE", columnDefinition = "char(8)", nullable = false)
    private String shipTranDate; // 출하일자

    @Id
    @Column(name = "SHIP_TRAN_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer shipTranSosok; // 소속

    @Id
    @Column(name = "SHIP_TRAN_UJCD", columnDefinition = "char(10)", nullable = false)
    private String shipTranUjcd; // 업장

    @Id
    @Column(name = "SHIP_TRAN_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer shipTranAcno; // 출하번호

    @Id
    @Column(name = "SHIP_TRAN_SEQ", columnDefinition = "int(10)", nullable = false)
    private Integer shipTranSeq; // 순번

    @Column(name = "SHIP_TRAN_DCOD", columnDefinition = "char(10)", nullable = false)
    private String shipTranDcod; // 부서코드

    @Column(name = "SHIP_TRAN_PURPOSE", columnDefinition = "char(10)", nullable = false)
    private String shipTranPurpose; // 용도

    @Column(name = "SHIP_TRAN_ITEM", columnDefinition = "int(10)", nullable = false)
    private Integer shipTranItem; // 품목

    @Column(name = "SHIP_TRAN_DETA", length = 50, nullable = false)
    private String shipTranDeta; // 품목상세

    @Column(name = "SHIP_TRAN_ITEM_VER", length = 10, nullable = false)
    private String shipTranItemVer; // 품목버전

    @Column(name = "SHIP_TRAN_CARIDNUM", length = 50, nullable = false)
    private String shipTranCaridnum; // 카드번호

    @Column(name = "SHIP_TRAN_SPEC", length = 100, nullable = false)
    private String shipTranSpec; // 규격

    @Column(name = "SHIP_TRAN_UNIT", length = 50, nullable = false)
    private String shipTranUnit; // 단위

    @Column(name = "SHIP_TRAN_CALC", columnDefinition = "tinyint(3)", nullable = false)
    private Integer shipTranCalc; // 계산방식

    @Column(name = "SHIP_TRAN_VDIV", columnDefinition = "tinyint(3)", nullable = false)
    private Integer shipTranVdiv; // VAT구분

    @Column(name = "SHIP_TRAN_ADIV", columnDefinition = "tinyint(3)", nullable = false)
    private Integer shipTranAdiv; // 계산구분

    @Column(name = "SHIP_TRAN_RATE", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranRate; // 기본단가

    @Column(name = "SHIP_TRAN_DC_PER", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranDcPer; // 할인율

    @Column(name = "SHIP_TRAN_DC_AMT", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranDcAmt; // 할인금액

    @Column(name = "SHIP_TRAN_AMT", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranAmt; // 판매단가

    @Column(name = "SHIP_TRAN_CNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal shipTranCnt; // 수량

    @Column(name = "SHIP_TRAN_CONVERT_WEIGHT", precision = 18, scale = 3, nullable = false)
    private BigDecimal shipTranConvertWeight; // 환산중량

    @Column(name = "SHIP_TRAN_NET", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranNet; // 공급가액

    @Column(name = "SHIP_TRAN_VAT", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranVat; // 부가세

    @Column(name = "SHIP_TRAN_ADV", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranAdv; // 선급금

    @Column(name = "SHIP_TRAN_TOT", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranTot; // 합계

    @Column(name = "SHIP_TRAN_CHECK", precision = 18, scale = 2, nullable = false)
    private BigDecimal shipTranCheck; // 검수수량

    @Column(name = "SHIP_TRAN_OCNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal shipTranOcnt; // 주문수량

    @Column(name = "SHIP_TRAN_LRATE", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranLrate; // 마지막단가

    @Column(name = "SHIP_TRAN_PRICE", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranPrice; // 가격

    @Column(name = "SHIP_TRAN_PRICE2", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranPrice2; // 가격2

    @Column(name = "SHIP_TRAN_LDIV", columnDefinition = "tinyint(3)", nullable = false)
    private Integer shipTranLdiv; // L구분

    @Column(name = "SHIP_TRAN_STAU", columnDefinition = "char(10)", nullable = false)
    private String shipTranStau; // 상태

    @Column(name = "SHIP_TRAN_REMARK", length = 200, nullable = false)
    private String shipTranRemark; // 비고

    @Column(name = "SHIP_TRAN_FDATE", nullable = false)
    private LocalDateTime shipTranFdate; // 최초등록일

    @Column(name = "SHIP_TRAN_FUSER", length = 50, nullable = false)
    private String shipTranFuser; // 최초등록자

    @Column(name = "SHIP_TRAN_LDATE", nullable = false)
    private LocalDateTime shipTranLdate; // 최종수정일

    @Column(name = "SHIP_TRAN_LUSER", length = 50, nullable = false)
    private String shipTranLuser; // 최종수정자

    @Column(name = "SHIP_TRAN_WAMT", precision = 18, scale = 0, nullable = false)
    private BigDecimal shipTranWamt; // W금액

    @Builder
    public ShipTran(String shipTranDate, Integer shipTranSosok, String shipTranUjcd, Integer shipTranAcno,
                    Integer shipTranSeq, String shipTranDcod, String shipTranPurpose, Integer shipTranItem,
                    String shipTranDeta, String shipTranItemVer, String shipTranCaridnum, String shipTranSpec,
                    String shipTranUnit, Integer shipTranCalc, Integer shipTranVdiv, Integer shipTranAdiv,
                    BigDecimal shipTranRate, BigDecimal shipTranDcPer, BigDecimal shipTranDcAmt, BigDecimal shipTranAmt,
                    BigDecimal shipTranCnt, BigDecimal shipTranConvertWeight, BigDecimal shipTranNet, BigDecimal shipTranVat,
                    BigDecimal shipTranAdv, BigDecimal shipTranTot, BigDecimal shipTranCheck, BigDecimal shipTranOcnt,
                    BigDecimal shipTranLrate, BigDecimal shipTranPrice, BigDecimal shipTranPrice2, Integer shipTranLdiv,
                    String shipTranStau, String shipTranRemark, LocalDateTime shipTranFdate, String shipTranFuser,
                    LocalDateTime shipTranLdate, String shipTranLuser, BigDecimal shipTranWamt) {
        this.shipTranDate = shipTranDate;
        this.shipTranSosok = shipTranSosok;
        this.shipTranUjcd = shipTranUjcd;
        this.shipTranAcno = shipTranAcno;
        this.shipTranSeq = shipTranSeq;
        this.shipTranDcod = shipTranDcod;
        this.shipTranPurpose = shipTranPurpose;
        this.shipTranItem = shipTranItem;
        this.shipTranDeta = shipTranDeta;
        this.shipTranItemVer = shipTranItemVer;
        this.shipTranCaridnum = shipTranCaridnum;
        this.shipTranSpec = shipTranSpec;
        this.shipTranUnit = shipTranUnit;
        this.shipTranCalc = shipTranCalc;
        this.shipTranVdiv = shipTranVdiv;
        this.shipTranAdiv = shipTranAdiv;
        this.shipTranRate = shipTranRate;
        this.shipTranDcPer = shipTranDcPer;
        this.shipTranDcAmt = shipTranDcAmt;
        this.shipTranAmt = shipTranAmt;
        this.shipTranCnt = shipTranCnt;
        this.shipTranConvertWeight = shipTranConvertWeight;
        this.shipTranNet = shipTranNet;
        this.shipTranVat = shipTranVat;
        this.shipTranAdv = shipTranAdv;
        this.shipTranTot = shipTranTot;
        this.shipTranCheck = shipTranCheck;
        this.shipTranOcnt = shipTranOcnt;
        this.shipTranLrate = shipTranLrate;
        this.shipTranPrice = shipTranPrice;
        this.shipTranPrice2 = shipTranPrice2;
        this.shipTranLdiv = shipTranLdiv;
        this.shipTranStau = shipTranStau;
        this.shipTranRemark = shipTranRemark;
        this.shipTranFdate = shipTranFdate;
        this.shipTranFuser = shipTranFuser;
        this.shipTranLdate = shipTranLdate;
        this.shipTranLuser = shipTranLuser;
        this.shipTranWamt = shipTranWamt;
    }

    // 비즈니스 메서드
    public String getShipTranKey() {
        return shipTranDate + "-" + shipTranSosok + "-" + shipTranUjcd + "-" + shipTranAcno + "-" + shipTranSeq;
    }

    public String getShipMastKey() {
        return shipTranDate + "-" + shipTranSosok + "-" + shipTranUjcd + "-" + shipTranAcno;
    }

    public BigDecimal getTotalAmount() {
        return shipTranNet.add(shipTranVat);
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ShipTranId implements Serializable {
        private String shipTranDate;
        private Integer shipTranSosok;
        private String shipTranUjcd;
        private Integer shipTranAcno;
        private Integer shipTranSeq;

        public ShipTranId(String shipTranDate, Integer shipTranSosok, String shipTranUjcd, Integer shipTranAcno, Integer shipTranSeq) {
            this.shipTranDate = shipTranDate;
            this.shipTranSosok = shipTranSosok;
            this.shipTranUjcd = shipTranUjcd;
            this.shipTranAcno = shipTranAcno;
            this.shipTranSeq = shipTranSeq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ShipTranId that = (ShipTranId) o;

            if (!shipTranDate.equals(that.shipTranDate)) return false;
            if (!shipTranSosok.equals(that.shipTranSosok)) return false;
            if (!shipTranUjcd.equals(that.shipTranUjcd)) return false;
            if (!shipTranAcno.equals(that.shipTranAcno)) return false;
            return shipTranSeq.equals(that.shipTranSeq);
        }

        @Override
        public int hashCode() {
            int result = shipTranDate.hashCode();
            result = 31 * result + shipTranSosok.hashCode();
            result = 31 * result + shipTranUjcd.hashCode();
            result = 31 * result + shipTranAcno.hashCode();
            result = 31 * result + shipTranSeq.hashCode();
            return result;
        }
    }
} 