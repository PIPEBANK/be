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
 * 신용 마스터 엔티티
 * ac_credit_mast 테이블 매핑
 */
@Entity
@Table(name = "ac_credit_mast")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(CreditMast.CreditMastId.class)
public class CreditMast {

    @Id
    @Column(name = "CREDIT_MAST_SOSOK", columnDefinition = "int(11)", nullable = false)
    private Integer creditMastSosok;  //소속코드 (SosokCode테이블 )ode테이블 )

    @Id
    @Column(name = "CREDIT_MAST_CUST", columnDefinition = "int(11)", nullable = false)
    private Integer creditMastCust;  // 거래처코드 (Customer테이블)

    @Column(name = "CREDIT_MAST_CREDIT_RANK", nullable = false, length = 50)
    private String creditMastCreditRank;

    @Column(name = "CREDIT_MAST_CREDIT_SCORE", nullable = false, length = 50)
    private String creditMastCreditScore;

    @Column(name = "CREDIT_MAST_BOND_DCOD", nullable = false, columnDefinition = "char(10)")
    private String creditMastBondDcod;  //채권관리단계코드 (코드관리테이블)

    @Column(name = "CREDIT_MAST_SDATE", nullable = false, columnDefinition = "char(8)")
    private String creditMastSdate;

    @Column(name = "CREDIT_MAST_FDATE", nullable = false)
    private LocalDateTime creditMastFdate;

    @Column(name = "CREDIT_MAST_FUSER", nullable = false, length = 50)
    private String creditMastFuser;

    @Column(name = "CREDIT_MAST_LDATE", nullable = false)
    private LocalDateTime creditMastLdate;

    @Column(name = "CREDIT_MAST_LUSER", nullable = false, length = 50)
    private String creditMastLuser;

    // 생성자
    public CreditMast(Integer creditMastSosok, Integer creditMastCust, String creditMastCreditRank,
                     String creditMastCreditScore, String creditMastBondDcod, String creditMastSdate,
                     LocalDateTime creditMastFdate, String creditMastFuser,
                     LocalDateTime creditMastLdate, String creditMastLuser) {
        this.creditMastSosok = creditMastSosok;
        this.creditMastCust = creditMastCust;
        this.creditMastCreditRank = creditMastCreditRank;
        this.creditMastCreditScore = creditMastCreditScore;
        this.creditMastBondDcod = creditMastBondDcod;
        this.creditMastSdate = creditMastSdate;
        this.creditMastFdate = creditMastFdate;
        this.creditMastFuser = creditMastFuser;
        this.creditMastLdate = creditMastLdate;
        this.creditMastLuser = creditMastLuser;
    }

    // 비즈니스 메서드
    public String getCreditKey() {
        return creditMastSosok + "-" + creditMastCust;
    }

    public String getDisplayName() {
        return creditMastCreditRank;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class CreditMastId implements Serializable {
        private Integer creditMastSosok;
        private Integer creditMastCust;

        public CreditMastId(Integer creditMastSosok, Integer creditMastCust) {
            this.creditMastSosok = creditMastSosok;
            this.creditMastCust = creditMastCust;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CreditMastId that = (CreditMastId) o;

            if (!creditMastSosok.equals(that.creditMastSosok)) return false;
            return creditMastCust.equals(that.creditMastCust);
        }

        @Override
        public int hashCode() {
            int result = creditMastSosok.hashCode();
            result = 31 * result + creditMastCust.hashCode();
            return result;
        }
    }
} 