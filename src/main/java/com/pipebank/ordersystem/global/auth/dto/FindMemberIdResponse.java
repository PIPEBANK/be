package com.pipebank.ordersystem.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindMemberIdResponse {
    
    private String memberId;
    private String memberName;
    
    public static FindMemberIdResponse of(String memberId, String memberName) {
        return FindMemberIdResponse.builder()
                .memberId(memberId)
                .memberName(memberName)
                .build();
    }
} 