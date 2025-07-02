package com.pipebank.ordersystem.domain.web.member.dto;

import com.pipebank.ordersystem.domain.web.member.entity.MemberRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberCreateRequest {

    @NotBlank(message = "회원 ID는 필수입니다")
    @Size(min = 3, max = 50, message = "회원 ID는 3-50자 사이여야 합니다")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8-100자 사이여야 합니다")
    private String memberPw;

    @NotBlank(message = "회원 이름은 필수입니다")
    @Size(max = 100, message = "회원 이름은 100자를 초과할 수 없습니다")
    private String memberName;

    @NotBlank(message = "거래처 코드는 필수입니다")
    @Size(max = 50, message = "거래처 코드는 50자를 초과할 수 없습니다")
    private String custCode;

    private Boolean useYn = true;

    @NotNull(message = "권한은 필수입니다")
    private MemberRole role = MemberRole.USER;

    @Size(max = 50, message = "생성자는 50자를 초과할 수 없습니다")
    private String createBy;
} 