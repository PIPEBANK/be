package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ShipSlipSummaryResponse {
    
    private final String slipNumber;                    // 출고전표번호
    private final List<ShipSlipResponse> details;       // 상세 내역들
    private final BigDecimal totalQuantity;             // 수량 합계
    private final BigDecimal totalRate;                 // 단가 합계
    private final BigDecimal totalAmount;               // 출고금액 합계
    
    /**
     * ShipSlipResponse 리스트에서 합계 정보를 포함한 Response 생성
     */
    public static ShipSlipSummaryResponse fromDetails(String slipNumber, List<ShipSlipResponse> details) {
        BigDecimal totalQuantity = details.stream()
                .map(ShipSlipResponse::getShipTranCnt)
                .filter(cnt -> cnt != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRate = details.stream()
                .map(ShipSlipResponse::getShipTranRate)
                .filter(rate -> rate != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalAmount = details.stream()
                .map(ShipSlipResponse::getShipTranTot)
                .filter(tot -> tot != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return ShipSlipSummaryResponse.builder()
                .slipNumber(slipNumber)
                .details(details)
                .totalQuantity(totalQuantity)
                .totalRate(totalRate)
                .totalAmount(totalAmount)
                .build();
    }
} 