package com.pipebank.ordersystem.domain.web.member.dto;

import java.time.LocalDateTime;

import com.pipebank.ordersystem.domain.web.member.entity.Member;
import com.pipebank.ordersystem.domain.web.member.entity.MemberRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberResponse {

    private Long id;
    private String memberId;
    private String memberName;
    private String custCode;
    private String custCodeName;
    private String custCodeSano;     // 사업자번호
    private String custCodeUname1;   // 담당자명
    private String custCodeUtel1;    // 담당자 전화번호
    private String custCodeAddr;     // 주소
    private String custCodeEmail;    // 이메일
    private Integer custCodeSawon;   // 담당 사원번호
    private Integer custCodeBuse;    // 담당 부서번호
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
                // custCodeName은 Service에서 별도로 설정
                .useYn(member.getUseYn())
                .role(member.getRole())
                .roleDescription(member.getRole().getDescription())
                .createDate(member.getCreateDate())
                .createBy(member.getCreateBy())
                .updateDate(member.getUpdateDate())
                .updateBy(member.getUpdateBy())
                .build();
    }

    // 거래처명과 함께 생성하는 정적 메서드 추가
    public static MemberResponse fromWithCustName(Member member, String custCodeName) {
        return MemberResponse.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .custCode(member.getCustCode())
                .custCodeName(custCodeName)
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