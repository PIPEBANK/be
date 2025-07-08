package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 출하진행현황 조회 응답 DTO
 * OrderMast의 출하 관련 필수 필드만 포함
 */
@Getter
@Builder
public class OrderShipmentResponse {
    
    private String orderNumber;                     // 주문번호 (DATE-ACNO)
    private String shipNumber;                      // 출하번호 (SHIP_ORDER_DATE-SHIP_ORDER_ACNO)
    private String orderMastDate;                   // 주문일
    private String orderMastSdiv;                   // 출고형태 코드
    private String orderMastSdivDisplayName;        // 출고형태명
    private String orderMastComname;                // 현장명
    private String orderMastOdate;                  // 납품일
    private String orderMastStatus;                 // 주문상태 코드 (계산된 값)
    private String orderMastStatusDisplayName;      // 주문상태명 (계산된 값)
} 