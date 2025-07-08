package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 제품기능(DIV4) 조회 응답 DTO
 */
@Getter
@Builder
public class ItemDiv4Response {
    
    private String div1;        // 제품종류 코드 (상위)
    private String div2;        // 제품군 코드 (상위)
    private String div3;        // 제품용도 코드 (상위)
    private String code;        // 제품기능 코드 (2자리)
    private String name;        // 제품기능명
    private boolean isActive;   // 사용여부
    private boolean isOrderable; // 오더센터 사용여부
    private String fullCode;    // 전체 코드 (div1 + div2 + div3 + code)
    
    public static ItemDiv4Response of(String div1, String div2, String div3, String code, 
                                     String name, boolean isActive, boolean isOrderable) {
        return ItemDiv4Response.builder()
                .div1(div1)
                .div2(div2)
                .div3(div3)
                .code(code)
                .name(name)
                .isActive(isActive)
                .isOrderable(isOrderable)
                .fullCode(div1 + div2 + div3 + code)
                .build();
    }
} 