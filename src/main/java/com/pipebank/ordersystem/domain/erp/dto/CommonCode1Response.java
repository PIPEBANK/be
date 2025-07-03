package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.CommonCode1;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonCode1Response {

    // 원본 필드들
    private String commCod1Code;
    private String commCod1Num;
    private String commCod1Word;
    private String commCod1Name;
    private Integer commCod1Sort;
    private Integer commCod1View;
    private Integer commCod1Use;
    private Integer commCod1Lock;
    private Integer commCod1Free;
    private String commCod1Remark;
    private LocalDateTime commCod1Fdate;
    private String commCod1Fuser;
    private LocalDateTime commCod1Ldate;
    private String commCod1Luser;

    // 표시용 메서드들
    private Boolean active;
    private Boolean visible;
    private Boolean locked;
    private Boolean free;
    private String displayName;

    public static CommonCode1Response from(CommonCode1 entity) {
        return CommonCode1Response.builder()
                .commCod1Code(entity.getCommCod1Code())
                .commCod1Num(entity.getCommCod1Num())
                .commCod1Word(entity.getCommCod1Word())
                .commCod1Name(entity.getCommCod1Name())
                .commCod1Sort(entity.getCommCod1Sort())
                .commCod1View(entity.getCommCod1View())
                .commCod1Use(entity.getCommCod1Use())
                .commCod1Lock(entity.getCommCod1Lock())
                .commCod1Free(entity.getCommCod1Free())
                .commCod1Remark(entity.getCommCod1Remark())
                .commCod1Fdate(entity.getCommCod1Fdate())
                .commCod1Fuser(entity.getCommCod1Fuser())
                .commCod1Ldate(entity.getCommCod1Ldate())
                .commCod1Luser(entity.getCommCod1Luser())
                .active(entity.isActive())
                .visible(entity.isVisible())
                .locked(entity.isLocked())
                .free(entity.isFree())
                .displayName(entity.getDisplayName())
                .build();
    }
} 