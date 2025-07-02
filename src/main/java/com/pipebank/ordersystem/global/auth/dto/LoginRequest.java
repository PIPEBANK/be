package com.pipebank.ordersystem.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "회원 ID는 필수입니다")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
} 