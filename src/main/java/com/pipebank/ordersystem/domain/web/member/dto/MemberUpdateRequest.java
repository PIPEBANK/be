package com.pipebank.ordersystem.domain.web.member.dto;

import com.pipebank.ordersystem.domain.web.member.entity.MemberRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberUpdateRequest {

    @NotBlank(message = "회원 이름은 필수입니다")
    @Size(max = 100, message = "회원 이름은 100자를 초과할 수 없습니다")
    private String memberName;

    @NotBlank(message = "거래처 코드는 필수입니다")
    @Size(max = 50, message = "거래처 코드는 50자를 초과할 수 없습니다")
    private String custCode;

    private Boolean useYn;

    private MemberRole role;

    @Size(max = 50, message = "수정자는 50자를 초과할 수 없습니다")
    private String updateBy;
} 