package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 현장별 출하조회 응답 DTO
 * ShipTran 단위로 모든 정보 표시 (중복 제거 없음)
 */
@Getter
@Builder
public class ShipmentItemResponse {
    
    private String shipMastComname;         // 현장명 (SHIP_MAST_COMNAME)
    private String shipNumber;              // 출하번호 (SHIP_DATE-SHIP_ACNO)
    private String shipTranDeta;            // 제품명 (SHIP_TRAN_DETA)
    private String shipTranSpec;            // 규격 (SHIP_TRAN_SPEC)
    private String shipTranUnit;            // 단위 (SHIP_TRAN_UNIT)
    private String shipTranDate;            // 출고일자 (SHIP_TRAN_DATE)
    private BigDecimal shipTranCnt;         // 수량 (SHIP_TRAN_CNT)
    private BigDecimal shipTranTot;         // 단가 (SHIP_TRAN_TOT)
    
    // 추가 정보 (필요시)
    private Integer shipMastCust;           // 거래처코드
    private Integer shipTranSeq;            // ShipTran 순번
} 