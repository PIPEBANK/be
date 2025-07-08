package com.pipebank.ordersystem.domain.web.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebOrderMastCreateRequest {

    @NotBlank(message = "주문일자는 필수입니다")
    @Size(min = 8, max = 8, message = "주문일자는 8자리여야 합니다")
    private String orderMastDate;

    @NotNull(message = "소속은 필수입니다")
    private Integer orderMastSosok;

    @NotBlank(message = "업장은 필수입니다")
    @Size(max = 10, message = "업장은 10자 이하여야 합니다")
    private String orderMastUjcd;

    @NotNull(message = "거래처는 필수입니다")
    private Integer orderMastCust;

    @NotNull(message = "매출거래처는 필수입니다")
    private Integer orderMastScust;

    @NotNull(message = "담당자는 필수입니다")
    private Integer orderMastSawon;

    @NotNull(message = "담당자부서는 필수입니다")
    private Integer orderMastSawonBuse;

    @NotBlank(message = "납기일자는 필수입니다")
    @Size(min = 8, max = 8, message = "납기일자는 8자리여야 합니다")
    private String orderMastOdate;

    @NotNull(message = "프로젝트는 필수입니다")
    private Integer orderMastProject;

    @NotBlank(message = "비고는 필수입니다")
    @Size(max = 2000, message = "비고는 2000자 이하여야 합니다")
    private String orderMastRemark;

    @NotBlank(message = "납기시간은 필수입니다")
    @Size(min = 2, max = 2, message = "납기시간은 2자리여야 합니다")
    private String orderMastOtime;

    // 선택적 필드들
    @Size(max = 100, message = "납품현장 기본주소는 100자 이하여야 합니다")
    private String orderMastComaddr1;

    @Size(max = 100, message = "납품현장 상세주소는 100자 이하여야 합니다")
    private String orderMastComaddr2;

    @Size(max = 100, message = "현장명은 100자 이하여야 합니다")
    private String orderMastComname;

    @Size(max = 50, message = "인수자는 50자 이하여야 합니다")
    private String orderMastComuname;

    @Size(max = 30, message = "인수자연락처는 30자 이하여야 합니다")
    private String orderMastComutel;

    @Size(max = 10, message = "용도구분은 10자 이하여야 합니다")
    private String orderMastReason;

    @Size(max = 10, message = "운송구분은 10자 이하여야 합니다")
    private String orderMastTcomdiv;

    @Size(max = 10, message = "화폐코드는 10자 이하여야 합니다")
    private String orderMastCurrency;

    @Size(max = 20, message = "환율은 20자 이하여야 합니다")
    private String orderMastCurrencyPer;

    @Size(max = 10, message = "출고형태코드는 10자 이하여야 합니다")
    private String orderMastSdiv;

    @Size(max = 200, message = "수요처는 200자 이하여야 합니다")
    private String orderMastDcust;

    @Size(max = 10, message = "등록구분은 10자 이하여야 합니다")
    private String orderMastIntype;

    public WebOrderMastCreateRequest(String orderMastDate, Integer orderMastSosok, String orderMastUjcd,
                                   Integer orderMastCust, Integer orderMastScust, Integer orderMastSawon,
                                   Integer orderMastSawonBuse, String orderMastOdate, Integer orderMastProject,
                                   String orderMastRemark, String orderMastOtime, String orderMastComaddr1,
                                   String orderMastComaddr2, String orderMastComname, String orderMastComuname,
                                   String orderMastComutel, String orderMastReason, String orderMastTcomdiv,
                                   String orderMastCurrency, String orderMastCurrencyPer, String orderMastSdiv,
                                   String orderMastDcust, String orderMastIntype) {
        this.orderMastDate = orderMastDate;
        this.orderMastSosok = orderMastSosok;
        this.orderMastUjcd = orderMastUjcd;
        this.orderMastCust = orderMastCust;
        this.orderMastScust = orderMastScust;
        this.orderMastSawon = orderMastSawon;
        this.orderMastSawonBuse = orderMastSawonBuse;
        this.orderMastOdate = orderMastOdate;
        this.orderMastProject = orderMastProject;
        this.orderMastRemark = orderMastRemark;
        this.orderMastOtime = orderMastOtime;
        this.orderMastComaddr1 = orderMastComaddr1;
        this.orderMastComaddr2 = orderMastComaddr2;
        this.orderMastComname = orderMastComname;
        this.orderMastComuname = orderMastComuname;
        this.orderMastComutel = orderMastComutel;
        this.orderMastReason = orderMastReason;
        this.orderMastTcomdiv = orderMastTcomdiv;
        this.orderMastCurrency = orderMastCurrency;
        this.orderMastCurrencyPer = orderMastCurrencyPer;
        this.orderMastSdiv = orderMastSdiv;
        this.orderMastDcust = orderMastDcust;
        this.orderMastIntype = orderMastIntype;
    }
} 