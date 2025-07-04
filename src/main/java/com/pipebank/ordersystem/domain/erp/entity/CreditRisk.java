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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 신용 리스크 엔티티
 * ac_credit_risk 테이블 매핑
 */
@Entity
@Table(name = "ac_credit_risk")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(CreditRisk.CreditRiskId.class)
public class CreditRisk {

    @Id
    @Column(name = "CREDIT_RISK_SOSOK", columnDefinition = "int(11)", nullable = false)
    private Integer creditRiskSosok;

    @Id
    @Column(name = "CREDIT_RISK_CUST", columnDefinition = "int(11)", nullable = false)
    private Integer creditRiskCust;

    @Id
    @Column(name = "CREDIT_RISK_SEQ", columnDefinition = "int(11)", nullable = false)
    private Integer creditRiskSeq;

    @Column(name = "CREDIT_RISK_STAU", nullable = false, columnDefinition = "char(10)")
    private String creditRiskStau; //최권관리단계코드 (코드관리테이블)

    @Column(name = "CREDIT_RISK_SALE_DATE", nullable = false, columnDefinition = "char(8)")
    private String creditRiskSaleDate;

    @Column(name = "CREDIT_RISK_SDATE", nullable = false, columnDefinition = "char(8)")
    private String creditRiskSdate;

    @Column(name = "CREDIT_RISK_EDATE", nullable = false, columnDefinition = "char(8)")
    private String creditRiskEdate;

    @Column(name = "CREDIT_RISK_LIMIT_LIMIT", nullable = false, precision = 18, scale = 0, columnDefinition = "decimal(18,0) default '0'")
    private BigDecimal creditRiskLimitLimit;  //한도액

    @Column(name = "CREDIT_RISK_LIMIT_BOND", nullable = false, precision = 18, scale = 0, columnDefinition = "decimal(18,0) default '0'")
    private BigDecimal creditRiskLimitBond;  //채권잔액

    @Column(name = "CREDIT_RISK_UNRECV_BOND", nullable = false, precision = 18, scale = 0, columnDefinition = "decimal(18,0) default '0'")
    private BigDecimal creditRiskUnrecvBond;  //채권발생액(당일)

    @Column(name = "CREDIT_RISK_UNRECV_RECV", nullable = false, precision = 18, scale = 0, columnDefinition = "decimal(18,0) default '0'")
    private BigDecimal creditRiskUnrecvRecv;  //채권총수금액

    @Column(name = "CREDIT_RISK_UNRECV_BALA", nullable = false, precision = 18, scale = 0, columnDefinition = "decimal(18,0) default '0'")
    private BigDecimal creditRiskUnrecvBala; // 잔액

    @Column(name = "CREDIT_RISK_FDATE", nullable = false)
    private LocalDateTime creditRiskFdate;

    @Column(name = "CREDIT_RISK_FUSER", nullable = false, length = 50)
    private String creditRiskFuser;

    @Column(name = "CREDIT_RISK_LDATE", nullable = false)
    private LocalDateTime creditRiskLdate;

    @Column(name = "CREDIT_RISK_LUSER", nullable = false, length = 50)
    private String creditRiskLuser;

    // 생성자
    public CreditRisk(Integer creditRiskSosok, Integer creditRiskCust, Integer creditRiskSeq,
                     String creditRiskStau, String creditRiskSaleDate, String creditRiskSdate,
                     String creditRiskEdate, BigDecimal creditRiskLimitLimit, BigDecimal creditRiskLimitBond,
                     BigDecimal creditRiskUnrecvBond, BigDecimal creditRiskUnrecvRecv, BigDecimal creditRiskUnrecvBala,
                     LocalDateTime creditRiskFdate, String creditRiskFuser,
                     LocalDateTime creditRiskLdate, String creditRiskLuser) {
        this.creditRiskSosok = creditRiskSosok;
        this.creditRiskCust = creditRiskCust;
        this.creditRiskSeq = creditRiskSeq;
        this.creditRiskStau = creditRiskStau;
        this.creditRiskSaleDate = creditRiskSaleDate;
        this.creditRiskSdate = creditRiskSdate;
        this.creditRiskEdate = creditRiskEdate;
        this.creditRiskLimitLimit = creditRiskLimitLimit;
        this.creditRiskLimitBond = creditRiskLimitBond;
        this.creditRiskUnrecvBond = creditRiskUnrecvBond;
        this.creditRiskUnrecvRecv = creditRiskUnrecvRecv;
        this.creditRiskUnrecvBala = creditRiskUnrecvBala;
        this.creditRiskFdate = creditRiskFdate;
        this.creditRiskFuser = creditRiskFuser;
        this.creditRiskLdate = creditRiskLdate;
        this.creditRiskLuser = creditRiskLuser;
    }

    // 비즈니스 메서드
    public String getCreditRiskKey() {
        return creditRiskSosok + "-" + creditRiskCust + "-" + creditRiskSeq;
    }

    public String getDisplayName() {
        return creditRiskStau;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class CreditRiskId implements Serializable {
        private Integer creditRiskSosok;
        private Integer creditRiskCust;
        private Integer creditRiskSeq;

        public CreditRiskId(Integer creditRiskSosok, Integer creditRiskCust, Integer creditRiskSeq) {
            this.creditRiskSosok = creditRiskSosok;
            this.creditRiskCust = creditRiskCust;
            this.creditRiskSeq = creditRiskSeq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CreditRiskId that = (CreditRiskId) o;

            if (!creditRiskSosok.equals(that.creditRiskSosok)) return false;
            if (!creditRiskCust.equals(that.creditRiskCust)) return false;
            return creditRiskSeq.equals(that.creditRiskSeq);
        }

        @Override
        public int hashCode() {
            int result = creditRiskSosok.hashCode();
            result = 31 * result + creditRiskCust.hashCode();
            result = 31 * result + creditRiskSeq.hashCode();
            return result;
        }
    }
} 