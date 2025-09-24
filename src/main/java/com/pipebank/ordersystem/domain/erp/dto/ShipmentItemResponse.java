package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * 현장별 출하조회 응답 DTO
 * ShipTran 단위로 모든 정보 표시 (중복 제거 없음)
 */
@Getter
@Builder
public class ShipmentItemResponse {
    
    private String shipMastComname;         // 현장명 (SHIP_MAST_COMNAME)
    private String shipNumber;              // 출하번호 (SHIP_DATE-SHIP_ACNO)
    private String orderNumber;             // 주문번호 (ORDER_DATE-ORDER_ACNO)
    private String shipTranDeta;            // 제품명 (SHIP_TRAN_DETA)
    private String shipTranSpec;            // 규격 (SHIP_TRAN_SPEC)
    private String shipTranUnit;            // 단위 (SHIP_TRAN_UNIT)
    private String shipTranDate;            // 출고일자 (SHIP_TRAN_DATE)
    private BigDecimal shipTranCnt;         // 수량 (SHIP_TRAN_CNT)
    private BigDecimal shipTranTot;         // 단가 (SHIP_TRAN_TOT)
    private BigDecimal shipTranAmt;        // 판매단가 (SHIP_TRAN_AMT)
    private BigDecimal shipTranNet;        // 공급가액 (SHIP_TRAN_NET)
    
    // 🔥 추가된 운송 관련 정보
    private String shipMastCarno;           // 차량번호 (SHIP_MAST_CARNO)
    private String shipMastTname;           // 운송기사명 (SHIP_MAST_TNAME)
    private String shipMastTtel;            // 운송회사전화 (SHIP_MAST_TTEL)
    private String shipMastCarton;          // 차량톤수 코드 (SHIP_MAST_CARTON)
    private String shipMastCartonDisplayName; // 차량톤수명 (CommonCode3에서 조회)
    
    // 추가 정보 (기존)
    private Integer shipMastCust;           // 거래처코드
    private Integer shipTranSeq;            // ShipTran 순번
} 