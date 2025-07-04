package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.CreditMast;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreditMastResponse {
    
    // 복합키 필드들
    private Integer creditMastSosok;
    private Integer creditMastCust;
    
    // 기본 정보 필드들
    private String creditMastCreditRank;
    private String creditMastCreditScore;
    private String creditMastBondDcod;
    private String creditMastSdate;
    private LocalDateTime creditMastFdate;
    private String creditMastFuser;
    private LocalDateTime creditMastLdate;
    private String creditMastLuser;
    
    // 추가 정보 필드들
    private String creditKey;
    private String displayName;
    
    // 코드 필드들의 표시명
    private String creditMastSosokName;     // 소속명
    private String creditMastCustName;      // 거래처명
    private String creditMastBondDcodName;  // 채권코드 표시명
    
    public static CreditMastResponse from(CreditMast creditMast) {
        return CreditMastResponse.builder()
                .creditMastSosok(creditMast.getCreditMastSosok())
                .creditMastCust(creditMast.getCreditMastCust())
                .creditMastCreditRank(creditMast.getCreditMastCreditRank())
                .creditMastCreditScore(creditMast.getCreditMastCreditScore())
                .creditMastBondDcod(creditMast.getCreditMastBondDcod())
                .creditMastSdate(creditMast.getCreditMastSdate())
                .creditMastFdate(creditMast.getCreditMastFdate())
                .creditMastFuser(creditMast.getCreditMastFuser())
                .creditMastLdate(creditMast.getCreditMastLdate())
                .creditMastLuser(creditMast.getCreditMastLuser())
                .creditKey(creditMast.getCreditKey())
                .displayName(creditMast.getDisplayName())
                .build();
    }
} 