package com.pipebank.ordersystem.domain.web.order.dto;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderMast;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WebOrderMastResponse {

    private String orderMastDate;
    private Integer orderMastSosok;
    private String orderMastUjcd;
    private Integer orderMastAcno;
    private Integer orderMastCust;
    private Integer orderMastScust;
    private Integer orderMastSawon;
    private Integer orderMastSawonBuse;
    private String orderMastOdate;
    private Integer orderMastProject;
    private String orderMastRemark;
    private LocalDateTime orderMastFdate;
    private String orderMastFuser;
    private LocalDateTime orderMastLdate;
    private String orderMastLuser;
    private String orderMastComaddr1;
    private String orderMastComaddr2;
    private String orderMastComname;
    private String orderMastComuname;
    private String orderMastComutel;
    private String orderMastReason;
    private String orderMastTcomdiv;
    private String orderMastCurrency;
    private String orderMastCurrencyPer;
    private String orderMastSdiv;
    private String orderMastDcust;
    private String orderMastIntype;
    private String orderMastOtime;

    // 비즈니스 메서드 결과
    private String orderKey;
    private String fullAddress;
    private String displayName;

    public WebOrderMastResponse(String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno,
                              Integer orderMastCust, Integer orderMastScust, Integer orderMastSawon, Integer orderMastSawonBuse,
                              String orderMastOdate, Integer orderMastProject, String orderMastRemark,
                              LocalDateTime orderMastFdate, String orderMastFuser, LocalDateTime orderMastLdate, String orderMastLuser,
                              String orderMastComaddr1, String orderMastComaddr2, String orderMastComname, String orderMastComuname,
                              String orderMastComutel, String orderMastReason, String orderMastTcomdiv, String orderMastCurrency,
                              String orderMastCurrencyPer, String orderMastSdiv, String orderMastDcust, String orderMastIntype,
                              String orderMastOtime, String orderKey, String fullAddress, String displayName) {
        this.orderMastDate = orderMastDate;
        this.orderMastSosok = orderMastSosok;
        this.orderMastUjcd = orderMastUjcd;
        this.orderMastAcno = orderMastAcno;
        this.orderMastCust = orderMastCust;
        this.orderMastScust = orderMastScust;
        this.orderMastSawon = orderMastSawon;
        this.orderMastSawonBuse = orderMastSawonBuse;
        this.orderMastOdate = orderMastOdate;
        this.orderMastProject = orderMastProject;
        this.orderMastRemark = orderMastRemark;
        this.orderMastFdate = orderMastFdate;
        this.orderMastFuser = orderMastFuser;
        this.orderMastLdate = orderMastLdate;
        this.orderMastLuser = orderMastLuser;
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
        this.orderMastOtime = orderMastOtime;
        this.orderKey = orderKey;
        this.fullAddress = fullAddress;
        this.displayName = displayName;
    }

    public static WebOrderMastResponse from(WebOrderMast entity) {
        return new WebOrderMastResponse(
                entity.getOrderMastDate(),
                entity.getOrderMastSosok(),
                entity.getOrderMastUjcd(),
                entity.getOrderMastAcno(),
                entity.getOrderMastCust(),
                entity.getOrderMastScust(),
                entity.getOrderMastSawon(),
                entity.getOrderMastSawonBuse(),
                entity.getOrderMastOdate(),
                entity.getOrderMastProject(),
                entity.getOrderMastRemark(),
                entity.getOrderMastFdate(),
                entity.getOrderMastFuser(),
                entity.getOrderMastLdate(),
                entity.getOrderMastLuser(),
                entity.getOrderMastComaddr1(),
                entity.getOrderMastComaddr2(),
                entity.getOrderMastComname(),
                entity.getOrderMastComuname(),
                entity.getOrderMastComutel(),
                entity.getOrderMastReason(),
                entity.getOrderMastTcomdiv(),
                entity.getOrderMastCurrency(),
                entity.getOrderMastCurrencyPer(),
                entity.getOrderMastSdiv(),
                entity.getOrderMastDcust(),
                entity.getOrderMastIntype(),
                entity.getOrderMastOtime(),
                entity.getOrderKey(),
                entity.getFullAddress(),
                entity.getDisplayName()
        );
    }
} 