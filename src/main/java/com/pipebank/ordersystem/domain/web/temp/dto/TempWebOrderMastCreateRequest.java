package com.pipebank.ordersystem.domain.web.temp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempWebOrderMastCreateRequest {
    
    // 복합키 필드들
    private String orderMastDate;
    private Integer orderMastSosok;
    private String orderMastUjcd;
    private Integer orderMastAcno;
    
    // 기본 필드들
    private Integer orderMastCust;
    private Integer orderMastScust;
    private Integer orderMastSawon;
    private Integer orderMastSawonBuse;
    private String orderMastOdate;
    private Integer orderMastProject;
    private String orderMastRemark;
    private LocalDateTime orderMastFdate;
    private String orderMastFuser;
    private LocalDateTime orderMastLdate;
    private String orderMastLuser;
    private String orderMastComaddr1;
    private String orderMastComaddr2;
    private String orderMastComname;
    private String orderMastComuname;
    private String orderMastComutel;
    private String orderMastReason;
    private String orderMastTcomdiv;
    private String orderMastCurrency;
    private String orderMastCurrencyPer;
    private String orderMastSdiv;
    private String orderMastDcust;
    private String orderMastIntype;
    private String orderMastOtime;
    
    // 임시저장용 필드들
    private String userId;
    @Builder.Default
    private Boolean send = false; // 기본값: 임시저장
} 