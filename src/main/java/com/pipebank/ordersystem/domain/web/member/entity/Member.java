package com.pipebank.ordersystem.domain.web.member.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBL_MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MEMBERID", nullable = false, unique = true, length = 50)
    private String memberId;

    // 🔐 BCrypt 해시 필드: 비밀번호 (이미 해시되므로 추가 암호화 불필요)
    @Column(name = "MEMBERPW", nullable = false, length = 255)
    private String memberPw;

    @Column(name = "MEMBERNAME", nullable = false, length = 100)
    private String memberName;

    @Column(name = "CUSTCODE", nullable = false, length = 50)
    private String custCode;

    @Column(name = "USE_YN", nullable = false)
    private Boolean useYn = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private MemberRole role = MemberRole.USER;

    @Column(name = "TOKEN_VERSION", nullable = false)
    private Integer tokenVersion = 0;

    @CreatedDate
    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "CREATE_BY", length = 50)
    private String createBy;

    @LastModifiedDate
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_BY", length = 50)
    private String updateBy;

    @Builder
    private Member(String memberId, String memberPw, String memberName, String custCode,
                  Boolean useYn, MemberRole role, String createBy, String updateBy) {
        this.memberId = memberId;
        this.memberPw = memberPw;
        this.memberName = memberName;
        this.custCode = custCode;
        this.useYn = useYn != null ? useYn : true;
        this.role = role != null ? role : MemberRole.USER;
        this.createBy = createBy;
        this.updateBy = updateBy;
    }

    // 비즈니스 메소드
    public void updateMemberInfo(String memberName, String custCode, String updateBy) {
        this.memberName = memberName;
        this.custCode = custCode;
        this.updateBy = updateBy;
    }

    public void updatePassword(String newPassword, String updateBy) {
        this.memberPw = newPassword;
        this.updateBy = updateBy;
    }

    public void updateRole(MemberRole role, String updateBy) {
        this.role = role;
        this.updateBy = updateBy;
    }

    public void updateUseYn(Boolean useYn, String updateBy) {
        this.useYn = useYn;
        this.updateBy = updateBy;
    }

    // 계정 상태 확인 메소드
    public boolean isActive() {
        return Boolean.TRUE.equals(this.useYn);
    }

    public boolean isAdmin() {
        return MemberRole.ADMIN.equals(this.role);
    }

    // 토큰 버전 증가(강제 로그아웃 등)
    public void bumpTokenVersion(String updateBy) {
        this.tokenVersion = (this.tokenVersion == null ? 0 : this.tokenVersion) + 1;
        this.updateBy = updateBy;
    }
} 