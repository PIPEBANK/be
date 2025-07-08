package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 제품용도(DIV3) 조회 응답 DTO
 */
@Getter
@Builder
public class ItemDiv3Response {
    
    private String div1;        // 제품종류 코드 (상위)
    private String div2;        // 제품군 코드 (상위)
    private String code;        // 제품용도 코드 (2자리)
    private String name;        // 제품용도명
    private boolean isActive;   // 사용여부
    private String fullCode;    // 전체 코드 (div1 + div2 + code)
    
    public static ItemDiv3Response of(String div1, String div2, String code, String name, boolean isActive) {
        return ItemDiv3Response.builder()
                .div1(div1)
                .div2(div2)
                .code(code)
                .name(name)
                .isActive(isActive)
                .fullCode(div1 + div2 + code)
                .build();
    }
} 