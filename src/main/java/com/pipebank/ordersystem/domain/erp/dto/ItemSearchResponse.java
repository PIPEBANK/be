package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * 품목 검색 응답 DTO (검색 결과용 간단한 정보)
 */
@Getter
@Builder
public class ItemSearchResponse {
    
    private Integer itemCode;       // 품목 코드
    private String itemNum;         // 품목번호
    private String itemName;        // 품목명
    private String spec;            // 규격
    private String unit;            // 단위
    private BigDecimal saleRate;    // 판매단가
    private String brand;           // 브랜드
    private BigDecimal stockQuantity; // 재고수량
    
    public static ItemSearchResponse of(Integer itemCode, String itemNum, String itemName,
                                       String spec, String unit, BigDecimal saleRate, String brand,
                                       BigDecimal stockQuantity) {
        return ItemSearchResponse.builder()
                .itemCode(itemCode)
                .itemNum(itemNum)
                .itemName(itemName)
                .spec(spec)
                .unit(unit)
                .saleRate(saleRate)
                .brand(brand)
                .stockQuantity(stockQuantity)
                .build();
    }
} 