package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditMastSearchRequest {
    
    private Integer sosok;              // 소속코드
    private Integer cust;               // 거래처코드
    private String creditRank;          // 신용등급
    private String creditScore;         // 신용점수
    private String bondDcod;            // 채권코드
    private String keyword;             // 키워드 통합 검색
    
    // 페이징 관련
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "creditMastSosok";
    private String sortDir = "asc";
} 