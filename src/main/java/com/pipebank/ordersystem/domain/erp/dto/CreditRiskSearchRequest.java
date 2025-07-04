package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditRiskSearchRequest {
    
    private Integer sosok;              // 소속코드
    private Integer cust;               // 거래처코드
    private String stau;                // 상태코드
    private String saleDate;            // 판매일자
    private String startDate;           // 시작일자
    private String endDate;             // 종료일자
    private BigDecimal minLimit;        // 최소 신용한도
    private BigDecimal maxLimit;        // 최대 신용한도
    private BigDecimal minBond;         // 최소 미수채권
    private String keyword;             // 키워드 통합 검색
    
    // 페이징 관련
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "creditRiskSosok";
    private String sortDir = "asc";
} 