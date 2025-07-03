package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.OrderMast;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderMastResponse {
    
    // 복합키 필드들
    private String orderMastDate;
    private Integer orderMastSosok;
    private String orderMastUjcd;
    private Integer orderMastAcno;
    
    // 기본 정보 필드들
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
    
    // 추가 정보 필드들
    private String orderKey;
    private String orderNumber;    // 사용자 친화적 주문번호 (DATE-ACNO)
    private String fullAddress;
    private String displayName;
    
    // 코드 필드들의 표시명
    private String orderMastUjcdDisplayName;     // 업장코드 표시명
    private String orderMastReasonDisplayName;   // 사유코드 표시명
    private String orderMastTcomdivDisplayName;  // 거래구분 표시명
    private String orderMastCurrencyDisplayName; // 통화코드 표시명
    private String orderMastSdivDisplayName;     // 구분코드 표시명
    private String orderMastIntypeDisplayName;   // 입력타입 표시명
    
    // 관련 엔티티 정보
    private String orderMastSosokName;  // orderMastSosok FK -> SosokCode.sosokCodeName
    private String orderMastSawonName;  // orderMastSawon FK -> InsaMast.insaMastKnam
    private String orderMastSawonBuseName;  // orderMastSawonBuse FK -> BuseCode.buseCodeName
    private String orderMastCustName;   // orderMastCust FK -> Customer.custCodeName
    private String orderMastScustName;  // orderMastScust FK -> Customer.custCodeName
    
    public static OrderMastResponse from(OrderMast orderMast) {
        return OrderMastResponse.builder()
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastSosok(orderMast.getOrderMastSosok())
                .orderMastUjcd(orderMast.getOrderMastUjcd())
                .orderMastAcno(orderMast.getOrderMastAcno())
                .orderMastCust(orderMast.getOrderMastCust())
                .orderMastScust(orderMast.getOrderMastScust())
                .orderMastSawon(orderMast.getOrderMastSawon())
                .orderMastSawonBuse(orderMast.getOrderMastSawonBuse())
                .orderMastOdate(orderMast.getOrderMastOdate())
                .orderMastProject(orderMast.getOrderMastProject())
                .orderMastRemark(orderMast.getOrderMastRemark())
                .orderMastFdate(orderMast.getOrderMastFdate())
                .orderMastFuser(orderMast.getOrderMastFuser())
                .orderMastLdate(orderMast.getOrderMastLdate())
                .orderMastLuser(orderMast.getOrderMastLuser())
                .orderMastComaddr1(orderMast.getOrderMastComaddr1())
                .orderMastComaddr2(orderMast.getOrderMastComaddr2())
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastComuname(orderMast.getOrderMastComuname())
                .orderMastComutel(orderMast.getOrderMastComutel())
                .orderMastReason(orderMast.getOrderMastReason())
                .orderMastTcomdiv(orderMast.getOrderMastTcomdiv())
                .orderMastCurrency(orderMast.getOrderMastCurrency())
                .orderMastCurrencyPer(orderMast.getOrderMastCurrencyPer())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastDcust(orderMast.getOrderMastDcust())
                .orderMastIntype(orderMast.getOrderMastIntype())
                .orderMastOtime(orderMast.getOrderMastOtime())
                // 추가 정보
                .orderKey(orderMast.getOrderKey())
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .fullAddress(orderMast.getFullAddress())
                .displayName(orderMast.getDisplayName())
                .build();
    }
    
    // 코드 표시명을 포함한 팩토리 메서드
    public static OrderMastResponse from(OrderMast orderMast, 
                                        String ujcdDisplayName,
                                        String reasonDisplayName,
                                        String tcomdivDisplayName,
                                        String currencyDisplayName,
                                        String sdivDisplayName,
                                        String intypeDisplayName,
                                        String sosokName,
                                        String sawonName,
                                        String buseName,
                                        String orderMastCustName,
                                        String orderMastScustName) {
        return OrderMastResponse.builder()
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastSosok(orderMast.getOrderMastSosok())
                .orderMastUjcd(orderMast.getOrderMastUjcd())
                .orderMastAcno(orderMast.getOrderMastAcno())
                .orderMastCust(orderMast.getOrderMastCust())
                .orderMastScust(orderMast.getOrderMastScust())
                .orderMastSawon(orderMast.getOrderMastSawon())
                .orderMastSawonBuse(orderMast.getOrderMastSawonBuse())
                .orderMastOdate(orderMast.getOrderMastOdate())
                .orderMastProject(orderMast.getOrderMastProject())
                .orderMastRemark(orderMast.getOrderMastRemark())
                .orderMastFdate(orderMast.getOrderMastFdate())
                .orderMastFuser(orderMast.getOrderMastFuser())
                .orderMastLdate(orderMast.getOrderMastLdate())
                .orderMastLuser(orderMast.getOrderMastLuser())
                .orderMastComaddr1(orderMast.getOrderMastComaddr1())
                .orderMastComaddr2(orderMast.getOrderMastComaddr2())
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastComuname(orderMast.getOrderMastComuname())
                .orderMastComutel(orderMast.getOrderMastComutel())
                .orderMastReason(orderMast.getOrderMastReason())
                .orderMastTcomdiv(orderMast.getOrderMastTcomdiv())
                .orderMastCurrency(orderMast.getOrderMastCurrency())
                .orderMastCurrencyPer(orderMast.getOrderMastCurrencyPer())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastDcust(orderMast.getOrderMastDcust())
                .orderMastIntype(orderMast.getOrderMastIntype())
                .orderMastOtime(orderMast.getOrderMastOtime())
                // 추가 정보
                .orderKey(orderMast.getOrderKey())
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .fullAddress(orderMast.getFullAddress())
                .displayName(orderMast.getDisplayName())
                // 코드 표시명들
                .orderMastUjcdDisplayName(ujcdDisplayName)
                .orderMastReasonDisplayName(reasonDisplayName)
                .orderMastTcomdivDisplayName(tcomdivDisplayName)
                .orderMastCurrencyDisplayName(currencyDisplayName)
                .orderMastSdivDisplayName(sdivDisplayName)
                .orderMastIntypeDisplayName(intypeDisplayName)
                // 관련 엔티티 정보
                .orderMastSosokName(sosokName)
                .orderMastSawonName(sawonName)
                .orderMastSawonBuseName(buseName)
                .orderMastCustName(orderMastCustName)
                .orderMastScustName(orderMastScustName)
                .build();
    }
} 