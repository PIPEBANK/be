package com.pipebank.ordersystem.domain.web.temp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    // orderMastAcno는 자동생성되므로 제거
    
    // 기본 필드들
    private Integer orderMastCust;
    private Integer orderMastScust;
    private Integer orderMastSawon;
    private Integer orderMastSawonBuse;
    private String orderMastOdate;
    private Integer orderMastProject;
    private String orderMastRemark;
    // orderMastFdate, orderMastFuser, orderMastLdate, orderMastLuser는 자동생성
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
    
    // Mast와 Tran을 한 번에 처리하기 위한 필드
    private List<TempWebOrderTranCreateRequest> orderTrans;
} 