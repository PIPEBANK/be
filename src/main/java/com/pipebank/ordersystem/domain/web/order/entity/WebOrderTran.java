package com.pipebank.ordersystem.domain.web.order.entity;

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
@Table(name = "sa_order_tran")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(WebOrderTran.WebOrderTranId.class)
public class WebOrderTran {

    @Id
    @Column(name = "ORDER_TRAN_DATE", columnDefinition = "char(8)", nullable = false)
    private String orderTranDate;

    @Id
    @Column(name = "ORDER_TRAN_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer orderTranSosok;

    @Id
    @Column(name = "ORDER_TRAN_UJCD", columnDefinition = "char(10)", nullable = false)
    private String orderTranUjcd; 
    
    @Id
    @Column(name = "ORDER_TRAN_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer orderTranAcno;

    @Id
    @Column(name = "ORDER_TRAN_SEQ", columnDefinition = "int(10)", nullable = false)
    private Integer orderTranSeq; 

    @Column(name = "ORDER_TRAN_ITEM_VER", length = 10, nullable = false)
    private String orderTranItemVer; //품목버전

    @Column(name = "ORDER_TRAN_ITEM", columnDefinition = "int(10)", nullable = false)
    private Integer orderTranItem; //품목코드

    @Column(name = "ORDER_TRAN_DETA", length = 50, nullable = false)
    private String orderTranDeta; //품목명

    @Column(name = "ORDER_TRAN_SPEC", length = 100, nullable = false)
    private String orderTranSpec; //규격

    @Column(name = "ORDER_TRAN_UNIT", length = 50, nullable = false)
    private String orderTranUnit; //단위

    @Column(name = "ORDER_TRAN_CALC", columnDefinition = "tinyint(3)", nullable = false)
    private Integer orderTranCalc; // 정산 CheckBox

    @Column(name = "ORDER_TRAN_VDIV", columnDefinition = "tinyint(3)", nullable = false)
    private Integer orderTranVdiv; // V CheckBox  부가세여부

    @Column(name = "ORDER_TRAN_ADIV", columnDefinition = "tinyint(3)", nullable = false)
    private Integer orderTranAdiv;// A CheckBox 예수금여부

    @Column(name = "ORDER_TRAN_RATE", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranRate; // 기본단가

    @Column(name = "ORDER_TRAN_CNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal orderTranCnt; // 수량

    @Column(name = "ORDER_TRAN_CONVERT_WEIGHT", precision = 18, scale = 3, nullable = false, columnDefinition = "decimal(18,3) default '0.000'")
    private BigDecimal orderTranConvertWeight; // 환산중량

    @Column(name = "ORDER_TRAN_DC_PER", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranDcPer; // 할인율

    @Column(name = "ORDER_TRAN_DC_AMT", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranDcAmt; // 할인금액

    @Column(name = "ORDER_TRAN_FORI_AMT", precision = 18, scale = 3, nullable = false, columnDefinition = "decimal(18,3) default '0.000'")
    private BigDecimal orderTranForiAmt; // 외화단가

    @Column(name = "ORDER_TRAN_AMT", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranAmt; // 판매단가

    @Column(name = "ORDER_TRAN_NET", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranNet; // 공급가

    @Column(name = "ORDER_TRAN_VAT", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranVat; // 부가세

    @Column(name = "ORDER_TRAN_ADV", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranAdv; // 예수금

    @Column(name = "ORDER_TRAN_TOT", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranTot; // 합산금액

    @Column(name = "ORDER_TRAN_LRATE", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranLrate; // 이전판매단가

    @Column(name = "ORDER_TRAN_PRICE", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranPrice; // 매입가

    @Column(name = "ORDER_TRAN_PRICE2", precision = 18, scale = 0, nullable = false)
    private BigDecimal orderTranPrice2; // 매입가증감

    @Column(name = "ORDER_TRAN_LDIV", columnDefinition = "tinyint(3)", nullable = false)
    private Integer orderTranLdiv; // 판매가미반영 CheckBox

    @Column(name = "ORDER_TRAN_REMARK", length = 200, nullable = false)
    private String orderTranRemark; // 비고

    @Column(name = "ORDER_TRAN_STAU", columnDefinition = "char(10)", nullable = false)
    private String orderTranStau; // 상태 코드

    @Column(name = "ORDER_TRAN_FDATE", nullable = false)
    private LocalDateTime orderTranFdate; // 최초등록일

    @Column(name = "ORDER_TRAN_FUSER", length = 20, nullable = false)
    private String orderTranFuser; // 최초등록자

    @Column(name = "ORDER_TRAN_LDATE", nullable = false)
    private LocalDateTime orderTranLdate; // 최종수정일

    @Column(name = "ORDER_TRAN_LUSER", length = 20, nullable = false)
    private String orderTranLuser; // 최종수정자

    @Column(name = "ORDER_TRAN_WAMT", precision = 18, scale = 0, nullable = false, columnDefinition = "decimal(18,0) default '0'")
    private BigDecimal orderTranWamt;  //중량단가

    @Builder
    public WebOrderTran(String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno,
                    Integer orderTranSeq, String orderTranItemVer, Integer orderTranItem, String orderTranDeta,
                    String orderTranSpec, String orderTranUnit, Integer orderTranCalc, Integer orderTranVdiv,
                    Integer orderTranAdiv, BigDecimal orderTranRate, BigDecimal orderTranCnt, BigDecimal orderTranConvertWeight,
                    BigDecimal orderTranDcPer, BigDecimal orderTranDcAmt, BigDecimal orderTranForiAmt, BigDecimal orderTranAmt,
                    BigDecimal orderTranNet, BigDecimal orderTranVat, BigDecimal orderTranAdv, BigDecimal orderTranTot,
                    BigDecimal orderTranLrate, BigDecimal orderTranPrice, BigDecimal orderTranPrice2, Integer orderTranLdiv,
                    String orderTranRemark, String orderTranStau, LocalDateTime orderTranFdate, String orderTranFuser,
                    LocalDateTime orderTranLdate, String orderTranLuser, BigDecimal orderTranWamt) {
        this.orderTranDate = orderTranDate;
        this.orderTranSosok = orderTranSosok;
        this.orderTranUjcd = orderTranUjcd;
        this.orderTranAcno = orderTranAcno;
        this.orderTranSeq = orderTranSeq;
        this.orderTranItemVer = orderTranItemVer;
        this.orderTranItem = orderTranItem;
        this.orderTranDeta = orderTranDeta;
        this.orderTranSpec = orderTranSpec;
        this.orderTranUnit = orderTranUnit;
        this.orderTranCalc = orderTranCalc;
        this.orderTranVdiv = orderTranVdiv;
        this.orderTranAdiv = orderTranAdiv;
        this.orderTranRate = orderTranRate;
        this.orderTranCnt = orderTranCnt;
        this.orderTranConvertWeight = orderTranConvertWeight;
        this.orderTranDcPer = orderTranDcPer;
        this.orderTranDcAmt = orderTranDcAmt;
        this.orderTranForiAmt = orderTranForiAmt;
        this.orderTranAmt = orderTranAmt;
        this.orderTranNet = orderTranNet;
        this.orderTranVat = orderTranVat;
        this.orderTranAdv = orderTranAdv;
        this.orderTranTot = orderTranTot;
        this.orderTranLrate = orderTranLrate;
        this.orderTranPrice = orderTranPrice;
        this.orderTranPrice2 = orderTranPrice2;
        this.orderTranLdiv = orderTranLdiv;
        this.orderTranRemark = orderTranRemark;
        this.orderTranStau = orderTranStau;
        this.orderTranFdate = orderTranFdate;
        this.orderTranFuser = orderTranFuser;
        this.orderTranLdate = orderTranLdate;
        this.orderTranLuser = orderTranLuser;
        this.orderTranWamt = orderTranWamt;
    }

    // 비즈니스 메서드
    public String getOrderTranKey() {
        return orderTranDate + "-" + orderTranSosok + "-" + orderTranUjcd + "-" + orderTranAcno + "-" + orderTranSeq;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class WebOrderTranId implements Serializable {
        private String orderTranDate;
        private Integer orderTranSosok;
        private String orderTranUjcd;
        private Integer orderTranAcno;
        private Integer orderTranSeq;

        public WebOrderTranId(String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno, Integer orderTranSeq) {
            this.orderTranDate = orderTranDate;
            this.orderTranSosok = orderTranSosok;
            this.orderTranUjcd = orderTranUjcd;
            this.orderTranAcno = orderTranAcno;
            this.orderTranSeq = orderTranSeq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WebOrderTranId that = (WebOrderTranId) o;

            if (!orderTranDate.equals(that.orderTranDate)) return false;
            if (!orderTranSosok.equals(that.orderTranSosok)) return false;
            if (!orderTranUjcd.equals(that.orderTranUjcd)) return false;
            if (!orderTranAcno.equals(that.orderTranAcno)) return false;
            return orderTranSeq.equals(that.orderTranSeq);
        }

        @Override
        public int hashCode() {
            int result = orderTranDate.hashCode();
            result = 31 * result + orderTranSosok.hashCode();
            result = 31 * result + orderTranUjcd.hashCode();
            result = 31 * result + orderTranAcno.hashCode();
            result = 31 * result + orderTranSeq.hashCode();
            return result;
        }
    }
} 