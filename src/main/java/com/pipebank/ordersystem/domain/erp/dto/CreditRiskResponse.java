package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.CreditRisk;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CreditRiskResponse {
    
    // 복합키 필드들
    private Integer creditRiskSosok;
    private Integer creditRiskCust;
    private Integer creditRiskSeq;
    
    // 기본 정보 필드들
    private String creditRiskStau;
    private String creditRiskSaleDate;
    private String creditRiskSdate;
    private String creditRiskEdate;
    private BigDecimal creditRiskLimitLimit;
    private BigDecimal creditRiskLimitBond;
    private BigDecimal creditRiskUnrecvBond;
    private BigDecimal creditRiskUnrecvRecv;
    private BigDecimal creditRiskUnrecvBala;
    private LocalDateTime creditRiskFdate;
    private String creditRiskFuser;
    private LocalDateTime creditRiskLdate;
    private String creditRiskLuser;
    
    // 추가 정보 필드들
    private String creditRiskKey;
    private String displayName;
    
    // 코드 필드들의 표시명
    private String creditRiskSosokName;     // 소속명
    private String creditRiskCustName;      // 거래처명
    private String creditRiskStauName;      // 상태코드 표시명
    
    // 계산된 필드들
    private BigDecimal totalUnrecv;         // 총 미수금액
    private BigDecimal availableLimit;      // 가용한도 (한도 - 미수채권)
    private String riskLevel;               // 위험도 레벨
    
    public static CreditRiskResponse from(CreditRisk creditRisk) {
        return CreditRiskResponse.builder()
                .creditRiskSosok(creditRisk.getCreditRiskSosok())
                .creditRiskCust(creditRisk.getCreditRiskCust())
                .creditRiskSeq(creditRisk.getCreditRiskSeq())
                .creditRiskStau(creditRisk.getCreditRiskStau())
                .creditRiskSaleDate(creditRisk.getCreditRiskSaleDate())
                .creditRiskSdate(creditRisk.getCreditRiskSdate())
                .creditRiskEdate(creditRisk.getCreditRiskEdate())
                .creditRiskLimitLimit(creditRisk.getCreditRiskLimitLimit())
                .creditRiskLimitBond(creditRisk.getCreditRiskLimitBond())
                .creditRiskUnrecvBond(creditRisk.getCreditRiskUnrecvBond())
                .creditRiskUnrecvRecv(creditRisk.getCreditRiskUnrecvRecv())
                .creditRiskUnrecvBala(creditRisk.getCreditRiskUnrecvBala())
                .creditRiskFdate(creditRisk.getCreditRiskFdate())
                .creditRiskFuser(creditRisk.getCreditRiskFuser())
                .creditRiskLdate(creditRisk.getCreditRiskLdate())
                .creditRiskLuser(creditRisk.getCreditRiskLuser())
                .creditRiskKey(creditRisk.getCreditRiskKey())
                .displayName(creditRisk.getDisplayName())
                .build();
    }
} 