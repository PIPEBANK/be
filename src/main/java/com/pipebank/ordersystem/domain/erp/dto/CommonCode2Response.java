package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.CommonCode2;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonCode2Response {

    // 원본 필드들
    private String commCod2Cod1;
    private String commCod2Cod2;
    private String commCod2Code;
    private String commCod2Num;
    private String commCod2Word;
    private String commCod2Name;
    private Integer commCod2Sort;
    private Integer commCod2View;
    private Integer commCod2Use;
    private String commCod2Remark;
    private LocalDateTime commCod2Fdate;
    private String commCod2Fuser;
    private LocalDateTime commCod2Ldate;
    private String commCod2Luser;

    // 표시용 메서드들
    private Boolean active;
    private Boolean visible;
    private String fullCode;
    private String displayName;

    public static CommonCode2Response from(CommonCode2 entity) {
        return CommonCode2Response.builder()
                .commCod2Cod1(entity.getCommCod2Cod1())
                .commCod2Cod2(entity.getCommCod2Cod2())
                .commCod2Code(entity.getCommCod2Code())
                .commCod2Num(entity.getCommCod2Num())
                .commCod2Word(entity.getCommCod2Word())
                .commCod2Name(entity.getCommCod2Name())
                .commCod2Sort(entity.getCommCod2Sort())
                .commCod2View(entity.getCommCod2View())
                .commCod2Use(entity.getCommCod2Use())
                .commCod2Remark(entity.getCommCod2Remark())
                .commCod2Fdate(entity.getCommCod2Fdate())
                .commCod2Fuser(entity.getCommCod2Fuser())
                .commCod2Ldate(entity.getCommCod2Ldate())
                .commCod2Luser(entity.getCommCod2Luser())
                .active(entity.isActive())
                .visible(entity.isVisible())
                .fullCode(entity.getFullCode())
                .displayName(entity.getDisplayName())
                .build();
    }
} 