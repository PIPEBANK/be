package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

/**
 * 최종 품목 선택 응답 DTO
 */
@Getter
@Builder
public class ItemSelectionResponse {
    
    private Integer itemCode;       // 품목 코드
    private String itemNum;         // 품목번호
    private String itemName;        // 품목명
    private String spec;            // 규격
    private BigDecimal spec2;       // 규격2 (표준중량)
    private String unit;            // 단위
    private BigDecimal saleRate;    // 판매단가
    private String brand;           // 브랜드
    private boolean isActive;       // 사용여부
    private boolean isOrderable;    // 주문가능여부
    
    // 분류 정보
    private String div1;            // 제품종류 코드
    private String div2;            // 제품군 코드
    private String div3;            // 제품용도 코드
    private String div4;            // 제품기능 코드
    private String div1Name;        // 제품종류명
    private String div2Name;        // 제품군명
    private String div3Name;        // 제품용도명
    private String div4Name;        // 제품기능명
    
    public static ItemSelectionResponse of(Integer itemCode, String itemNum, String itemName,
                                          String spec, BigDecimal spec2, String unit, BigDecimal saleRate,
                                          String brand, boolean isActive, boolean isOrderable,
                                          String div1, String div2, String div3, String div4,
                                          String div1Name, String div2Name, String div3Name, String div4Name) {
        return ItemSelectionResponse.builder()
                .itemCode(itemCode)
                .itemNum(itemNum)
                .itemName(itemName)
                .spec(spec)
                .spec2(spec2)
                .unit(unit)
                .saleRate(saleRate)
                .brand(brand)
                .isActive(isActive)
                .isOrderable(isOrderable)
                .div1(div1)
                .div2(div2)
                .div3(div3)
                .div4(div4)
                .div1Name(div1Name)
                .div2Name(div2Name)
                .div3Name(div3Name)
                .div4Name(div4Name)
                .build();
    }
} 