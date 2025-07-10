package com.pipebank.ordersystem.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindMemberIdRequest {
    
    @NotBlank(message = "회원명은 필수입니다.")
    private String memberName;
    
    @NotBlank(message = "사업자번호는 필수입니다.")
    private String custCodeSano;
} 