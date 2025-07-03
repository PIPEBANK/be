package com.pipebank.ordersystem.domain.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "co_comm_cod1")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CommonCode1 {

    @Id
    @Column(name = "COMM_COD1_CODE", columnDefinition = "char(3)")
    private String commCod1Code;

    @Column(name = "COMM_COD1_NUM", nullable = false, length = 10)
    private String commCod1Num;

    @Column(name = "COMM_COD1_WORD", nullable = false, length = 50)
    private String commCod1Word;

    @Column(name = "COMM_COD1_NAME", nullable = false, length = 50)
    private String commCod1Name;

    @Column(name = "COMM_COD1_SORT", nullable = false)
    private Integer commCod1Sort;

    @Column(name = "COMM_COD1_VIEW", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod1View;

    @Column(name = "COMM_COD1_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod1Use;

    @Column(name = "COMM_COD1_LOCK", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod1Lock;

    @Column(name = "COMM_COD1_FREE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod1Free;

    @Column(name = "COMM_COD1_REMARK", nullable = false, length = 50)
    private String commCod1Remark;

    @Column(name = "COMM_COD1_FDATE", nullable = false)
    private LocalDateTime commCod1Fdate;

    @Column(name = "COMM_COD1_FUSER", nullable = false, length = 20)
    private String commCod1Fuser;

    @Column(name = "COMM_COD1_LDATE", nullable = false)
    private LocalDateTime commCod1Ldate;

    @Column(name = "COMM_COD1_LUSER", nullable = false, length = 20)
    private String commCod1Luser;

    @Builder
    public CommonCode1(String commCod1Code, String commCod1Num, String commCod1Word, String commCod1Name,
                      Integer commCod1Sort, Integer commCod1View, Integer commCod1Use, Integer commCod1Lock,
                      Integer commCod1Free, String commCod1Remark, LocalDateTime commCod1Fdate,
                      String commCod1Fuser, LocalDateTime commCod1Ldate, String commCod1Luser) {
        this.commCod1Code = commCod1Code;
        this.commCod1Num = commCod1Num;
        this.commCod1Word = commCod1Word;
        this.commCod1Name = commCod1Name;
        this.commCod1Sort = commCod1Sort;
        this.commCod1View = commCod1View;
        this.commCod1Use = commCod1Use;
        this.commCod1Lock = commCod1Lock;
        this.commCod1Free = commCod1Free;
        this.commCod1Remark = commCod1Remark;
        this.commCod1Fdate = commCod1Fdate;
        this.commCod1Fuser = commCod1Fuser;
        this.commCod1Ldate = commCod1Ldate;
        this.commCod1Luser = commCod1Luser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return commCod1Use == 1;
    }

    public boolean isVisible() {
        return commCod1View == 1;
    }

    public boolean isLocked() {
        return commCod1Lock == 1;
    }

    public boolean isFree() {
        return commCod1Free == 1;
    }

    public String getDisplayName() {
        return commCod1Name;
    }

    public String getShortName() {
        return commCod1Word;
    }
} 