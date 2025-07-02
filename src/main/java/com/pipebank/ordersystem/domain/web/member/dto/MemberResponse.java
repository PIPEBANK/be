package com.pipebank.ordersystem.domain.web.member.dto;

import java.time.LocalDateTime;

import com.pipebank.ordersystem.domain.web.member.entity.Member;
import com.pipebank.ordersystem.domain.web.member.entity.MemberRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String memberId;
    private String memberName;
    private String custCode;
    private Boolean useYn;
    private MemberRole role;
    private String roleDescription;
    private LocalDateTime createDate;
    private String createBy;
    private LocalDateTime updateDate;
    private String updateBy;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .custCode(member.getCustCode())
                .useYn(member.getUseYn())
                .role(member.getRole())
                .roleDescription(member.getRole().getDescription())
                .createDate(member.getCreateDate())
                .createBy(member.getCreateBy())
                .updateDate(member.getUpdateDate())
                .updateBy(member.getUpdateBy())
                .build();
    }
} 