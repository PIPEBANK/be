package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 제품군(DIV2) 조회 응답 DTO
 */
@Getter
@Builder
public class ItemDiv2Response {
    
    private String div1;        // 제품종류 코드 (상위)
    private String code;        // 제품군 코드 (1자리)
    private String name;        // 제품군명
    private boolean isActive;   // 사용여부
    private String fullCode;    // 전체 코드 (div1 + code)
    
    public static ItemDiv2Response of(String div1, String code, String name, boolean isActive) {
        return ItemDiv2Response.builder()
                .div1(div1)
                .code(code)
                .name(name)
                .isActive(isActive)
                .fullCode(div1 + code)
                .build();
    }
} 