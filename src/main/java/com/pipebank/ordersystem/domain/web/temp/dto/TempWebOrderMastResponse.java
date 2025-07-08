package com.pipebank.ordersystem.domain.web.temp.dto;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TempWebOrderMastResponse {
    
    // 복합키 필드들
    private String orderMastDate;
    private Integer orderMastSosok;
    private String orderMastUjcd;
    private Integer orderMastAcno;
    
    // 기본 필드들
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
    
    // 임시저장용 필드들
    private String userId;
    private Boolean send;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 비즈니스 메서드들 (Entity와 동일)
    public String getOrderKey() {
        return orderMastDate + "-" + orderMastSosok + "-" + orderMastUjcd + "-" + orderMastAcno;
    }

    public String getFullAddress() {
        if (orderMastComaddr1 != null && orderMastComaddr2 != null) {
            return orderMastComaddr1 + " " + orderMastComaddr2;
        } else if (orderMastComaddr1 != null) {
            return orderMastComaddr1;
        } else if (orderMastComaddr2 != null) {
            return orderMastComaddr2;
        }
        return "";
    }

    public String getDisplayName() {
        return orderMastComname != null ? orderMastComname : orderMastDcust;
    }

    // 임시저장 상태인지 확인
    public boolean isTempSaved() {
        return !Boolean.TRUE.equals(send);
    }

    // 전송 완료 상태인지 확인
    public boolean isSent() {
        return Boolean.TRUE.equals(send);
    }
    
    public static TempWebOrderMastResponse from(TempWebOrderMast entity) {
        TempWebOrderMastResponse response = new TempWebOrderMastResponse();
        response.orderMastDate = entity.getOrderMastDate();
        response.orderMastSosok = entity.getOrderMastSosok();
        response.orderMastUjcd = entity.getOrderMastUjcd();
        response.orderMastAcno = entity.getOrderMastAcno();
        response.orderMastCust = entity.getOrderMastCust();
        response.orderMastScust = entity.getOrderMastScust();
        response.orderMastSawon = entity.getOrderMastSawon();
        response.orderMastSawonBuse = entity.getOrderMastSawonBuse();
        response.orderMastOdate = entity.getOrderMastOdate();
        response.orderMastProject = entity.getOrderMastProject();
        response.orderMastRemark = entity.getOrderMastRemark();
        response.orderMastFdate = entity.getOrderMastFdate();
        response.orderMastFuser = entity.getOrderMastFuser();
        response.orderMastLdate = entity.getOrderMastLdate();
        response.orderMastLuser = entity.getOrderMastLuser();
        response.orderMastComaddr1 = entity.getOrderMastComaddr1();
        response.orderMastComaddr2 = entity.getOrderMastComaddr2();
        response.orderMastComname = entity.getOrderMastComname();
        response.orderMastComuname = entity.getOrderMastComuname();
        response.orderMastComutel = entity.getOrderMastComutel();
        response.orderMastReason = entity.getOrderMastReason();
        response.orderMastTcomdiv = entity.getOrderMastTcomdiv();
        response.orderMastCurrency = entity.getOrderMastCurrency();
        response.orderMastCurrencyPer = entity.getOrderMastCurrencyPer();
        response.orderMastSdiv = entity.getOrderMastSdiv();
        response.orderMastDcust = entity.getOrderMastDcust();
        response.orderMastIntype = entity.getOrderMastIntype();
        response.orderMastOtime = entity.getOrderMastOtime();
        response.userId = entity.getUserId();
        response.send = entity.getSend();
        response.createdAt = entity.getCreatedAt();
        response.updatedAt = entity.getUpdatedAt();
        return response;
    }
} 