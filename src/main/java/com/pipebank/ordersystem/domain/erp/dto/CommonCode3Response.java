package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.CommonCode3;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonCode3Response {

    // 원본 필드들
    private String commCod3Cod1;
    private String commCod3Cod2;
    private String commCod3Cod3;
    private String commCod3Code;
    private String commCod3Num;
    private String commCod3Word;
    private String commCod3Hnam;
    private String commCod3Enam;
    private String commCod3Hsub;
    private String commCod3Esub;
    private Integer commCod3Sort;
    private Integer commCod3View;
    private Integer commCod3Use;
    private String commCod3Remark;
    private LocalDateTime commCod3Fdate;
    private String commCod3Fuser;
    private LocalDateTime commCod3Ldate;
    private String commCod3Luser;

    // 표시용 메서드들
    private Boolean active;
    private Boolean visible;
    private String fullCode;
    private String displayName;
    private String hangulName;
    private String englishName;
    private String hangulSub;
    private String englishSub;

    public static CommonCode3Response from(CommonCode3 entity) {
        return CommonCode3Response.builder()
                .commCod3Cod1(entity.getCommCod3Cod1())
                .commCod3Cod2(entity.getCommCod3Cod2())
                .commCod3Cod3(entity.getCommCod3Cod3())
                .commCod3Code(entity.getCommCod3Code())
                .commCod3Num(entity.getCommCod3Num())
                .commCod3Word(entity.getCommCod3Word())
                .commCod3Hnam(entity.getCommCod3Hnam())
                .commCod3Enam(entity.getCommCod3Enam())
                .commCod3Hsub(entity.getCommCod3Hsub())
                .commCod3Esub(entity.getCommCod3Esub())
                .commCod3Sort(entity.getCommCod3Sort())
                .commCod3View(entity.getCommCod3View())
                .commCod3Use(entity.getCommCod3Use())
                .commCod3Remark(entity.getCommCod3Remark())
                .commCod3Fdate(entity.getCommCod3Fdate())
                .commCod3Fuser(entity.getCommCod3Fuser())
                .commCod3Ldate(entity.getCommCod3Ldate())
                .commCod3Luser(entity.getCommCod3Luser())
                .active(entity.isActive())
                .visible(entity.isVisible())
                .fullCode(entity.getFullCode())
                .displayName(entity.getDisplayName())
                .hangulName(entity.getHangulName())
                .englishName(entity.getEnglishName())
                .hangulSub(entity.getHangulSub())
                .englishSub(entity.getEnglishSub())
                .build();
    }
} 