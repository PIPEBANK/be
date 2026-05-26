package com.weborder.ordersystem.domain.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 부서 코드 엔티티
 * CO_BUSE_CODE 테이블 매핑
 */
@Entity
@Table(name = "CO_BUSE_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuseCode {

    @Id
    @Column(name = "BUSE_CODE_CODE", columnDefinition = "int(11)")
    private Integer buseCodeCode; // 부서코드

    @Column(name = "BUSE_CODE_DCOD", nullable = false, columnDefinition = "char(10)")
    private String buseCodeDcod; // 부서구분-원가구분

    @Column(name = "BUSE_CODE_NAME", length = 50, nullable = false)
    private String buseCodeName; // 부서명

    @Column(name = "BUSE_CODE_NUM", nullable = true, length = 10)
    private String buseCodeNum;

    @Column(name = "BUSE_CODE_WORD", nullable = true, length = 50)
    private String buseCodeWord;

    @Column(name = "BUSE_CODE_TOOO", nullable = false, columnDefinition = "int(11)")
    private Integer buseCodeTooo; // 인원

    @Column(name = "BUSE_CODE_ACC_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer buseCodeAccUse; // 회계사용

    @Column(name = "BUSE_CODE_ACC_SORT", nullable = false, columnDefinition = "int(11)")
    private Integer buseCodeAccSort; // 회계정렬

    @Column(name = "BUSE_CODE_INS_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer buseCodeInsUse; // 인사사용

    @Column(name = "BUSE_CODE_INS_SORT", nullable = false, columnDefinition = "int(11)")
    private Integer buseCodeInsSort; // 인사정렬

    @Column(name = "BUSE_CODE_PUR_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer buseCodePurUse; // 구매사용

    @Column(name = "BUSE_CODE_PUR_SORT", nullable = false, columnDefinition = "int(11)")
    private Integer buseCodePurSort; // 구매정렬

    @Column(name = "BUSE_CODE_POS_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer buseCodePosUse; // 업장사용

    @Column(name = "BUSE_CODE_POS_SORT", nullable = false, columnDefinition = "int(11)")
    private Integer buseCodePosSort; // 업장정렬

    @Column(name = "BUSE_CODE_WORK", nullable = true, columnDefinition = "int(11)")
    private Integer buseCodeWork;

    @Column(name = "BUSE_CODE_REMARK", nullable = false, length = 50)
    private String buseCodeRemark; // 비고

    @Column(name = "BUSE_CODE_FDATE", nullable = false)
    private LocalDateTime buseCodeFdate; // 최초등록일

    @Column(name = "BUSE_CODE_FUSER", nullable = false, length = 50)
    private String buseCodeFuser; // 최초등록자

    @Column(name = "BUSE_CODE_LDATE", nullable = false)
    private LocalDateTime buseCodeLdate; // 최종수정일

    @Column(name = "BUSE_CODE_LUSER", nullable = false, length = 50)
    private String buseCodeLuser; // 최종수정자

    // 표시명 반환 (부서명)
    public String getDisplayName() {
        return this.buseCodeName;
    }

    @Override
    public String toString() {
        return String.format("BuseCode{code=%d, name='%s'}", buseCodeCode, buseCodeName);
    }
}
