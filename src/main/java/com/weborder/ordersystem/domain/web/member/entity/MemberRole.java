package com.weborder.ordersystem.domain.web.member.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("관리자", "ROLE_ADMIN"),
    USER("사용자", "ROLE_USER"),
    DRIVER("배송기사", "ROLE_DRIVER");

    private final String description;
    private final String authority;

    MemberRole(String description, String authority) {
        this.description = description;
        this.authority = authority;
    }
} 