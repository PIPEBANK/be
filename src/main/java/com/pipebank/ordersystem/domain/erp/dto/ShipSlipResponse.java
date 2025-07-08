package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.ShipTran;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ShipSlipResponse {
    
    private final String slipNumber;           // 출고전표번호 (SHIP_TRAN_DATE + '-' + SHIP_TRAN_ACNO)
    private final String shipTranDate;         // 출고일
    private final String shipTranDeta;         // 제품명
    private final BigDecimal shipTranCnt;      // 수량
    private final BigDecimal shipTranRate;     // 단가
    private final BigDecimal shipTranTot;      // 출고금액
    private final Integer shipTranSeq;         // 순번 (정렬용)
    
    /**
     * ShipTran에서 출고전표현황 Response 생성
     */
    public static ShipSlipResponse fromShipTran(ShipTran shipTran) {
        String slipNumber = shipTran.getShipTranDate() + "-" + shipTran.getShipTranAcno();
        
        return ShipSlipResponse.builder()
                .slipNumber(slipNumber)
                .shipTranDate(shipTran.getShipTranDate())
                .shipTranDeta(shipTran.getShipTranDeta())
                .shipTranCnt(shipTran.getShipTranCnt())
                .shipTranRate(shipTran.getShipTranRate())
                .shipTranTot(shipTran.getShipTranTot())
                .shipTranSeq(shipTran.getShipTranSeq())
                .build();
    }
} 