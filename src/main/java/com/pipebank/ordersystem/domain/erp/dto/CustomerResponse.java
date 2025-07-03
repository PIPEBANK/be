package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pipebank.ordersystem.domain.erp.entity.Customer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerResponse {
    
    private Integer custCodeCode;
    private String custCodeNum;
    private String custCodeDcod;
    private String custCodeName;
    private String custCodeWord;
    private String custCodeAnam;
    private String custCodeSano;
    private String custCodeSanoSeq;
    private String custCodePart1;
    private String custCodePart2;
    private String custCodeUname1;
    private String custCodeUtel1;
    private String custCodeUname2;
    private String custCodeUtel2;
    private String custCodeUname3;
    private String custCodeUtel3;
    private String custCodeFax;
    private String custCodePost;
    private String custCodeAddr1;
    private String custCodeAddr2;
    private String custCodeAddr;
    private String custCodeEmail;
    private String custCodeHttp;
    private Integer custCodeSawon;
    private Integer custCodeBuse;
    private String custCodeBank;
    private String custCodeBkname;
    private String custCodeBkno;
    private String custCodeBkuname;
    private String custCodeCountry;
    private String custCodeLocal;
    private Integer custCodeUseAcc;
    private Integer custCodeUsePur;
    private Integer custCodeUsePos;
    private String custCodeBubin;
    private Integer custCodeOcust;
    private String custCodeTdiv;
    private BigDecimal custCodeLimit;
    private String custCodeSdate;
    private String custCodeEdate;
    private String custCodePdate;
    private BigDecimal custCodeMcharge;
    private String custCodePtype;
    private Integer custCodeWeldAgent;
    private String custCodeRemark;
    private LocalDateTime custCodeFdate;
    private String custCodeFuser;
    private LocalDateTime custCodeLdate;
    private String custCodeLuser;
    
    // 추가 정보 필드
    private String displayName;
    private String fullAddress;
    private boolean isActive;
    private boolean canPurchase;
    private boolean canPos;
    private String custCodeDcodDisplayName; // DCOD 코드의 실제 의미

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .custCodeCode(customer.getCustCodeCode())
                .custCodeNum(customer.getCustCodeNum())
                .custCodeDcod(customer.getCustCodeDcod())
                .custCodeName(customer.getCustCodeName())
                .custCodeWord(customer.getCustCodeWord())
                .custCodeAnam(customer.getCustCodeAnam())
                .custCodeSano(customer.getCustCodeSano())
                .custCodeSanoSeq(customer.getCustCodeSanoSeq())
                .custCodePart1(customer.getCustCodePart1())
                .custCodePart2(customer.getCustCodePart2())
                .custCodeUname1(customer.getCustCodeUname1())
                .custCodeUtel1(customer.getCustCodeUtel1())
                .custCodeUname2(customer.getCustCodeUname2())
                .custCodeUtel2(customer.getCustCodeUtel2())
                .custCodeUname3(customer.getCustCodeUname3())
                .custCodeUtel3(customer.getCustCodeUtel3())
                .custCodeFax(customer.getCustCodeFax())
                .custCodePost(customer.getCustCodePost())
                .custCodeAddr1(customer.getCustCodeAddr1())
                .custCodeAddr2(customer.getCustCodeAddr2())
                .custCodeAddr(customer.getCustCodeAddr())
                .custCodeEmail(customer.getCustCodeEmail())
                .custCodeHttp(customer.getCustCodeHttp())
                .custCodeSawon(customer.getCustCodeSawon())
                .custCodeBuse(customer.getCustCodeBuse())
                .custCodeBank(customer.getCustCodeBank())
                .custCodeBkname(customer.getCustCodeBkname())
                .custCodeBkno(customer.getCustCodeBkno())
                .custCodeBkuname(customer.getCustCodeBkuname())
                .custCodeCountry(customer.getCustCodeCountry())
                .custCodeLocal(customer.getCustCodeLocal())
                .custCodeUseAcc(customer.getCustCodeUseAcc())
                .custCodeUsePur(customer.getCustCodeUsePur())
                .custCodeUsePos(customer.getCustCodeUsePos())
                .custCodeBubin(customer.getCustCodeBubin())
                .custCodeOcust(customer.getCustCodeOcust())
                .custCodeTdiv(customer.getCustCodeTdiv())
                .custCodeLimit(customer.getCustCodeLimit())
                .custCodeSdate(customer.getCustCodeSdate())
                .custCodeEdate(customer.getCustCodeEdate())
                .custCodePdate(customer.getCustCodePdate())
                .custCodeMcharge(customer.getCustCodeMcharge())
                .custCodePtype(customer.getCustCodePtype())
                .custCodeWeldAgent(customer.getCustCodeWeldAgent())
                .custCodeRemark(customer.getCustCodeRemark())
                .custCodeFdate(customer.getCustCodeFdate())
                .custCodeFuser(customer.getCustCodeFuser())
                .custCodeLdate(customer.getCustCodeLdate())
                .custCodeLuser(customer.getCustCodeLuser())
                // 추가 정보
                .displayName(customer.getDisplayName())
                .fullAddress(customer.getFullAddress())
                .isActive(customer.isActive())
                .canPurchase(customer.canPurchase())
                .canPos(customer.canPos())
                .build();
    }

    // 코드 표시명을 포함한 팩토리 메서드
    public static CustomerResponse from(Customer customer, String dcodDisplayName) {
        return CustomerResponse.builder()
                .custCodeCode(customer.getCustCodeCode())
                .custCodeNum(customer.getCustCodeNum())
                .custCodeDcod(customer.getCustCodeDcod())
                .custCodeName(customer.getCustCodeName())
                .custCodeWord(customer.getCustCodeWord())
                .custCodeAnam(customer.getCustCodeAnam())
                .custCodeSano(customer.getCustCodeSano())
                .custCodeSanoSeq(customer.getCustCodeSanoSeq())
                .custCodePart1(customer.getCustCodePart1())
                .custCodePart2(customer.getCustCodePart2())
                .custCodeUname1(customer.getCustCodeUname1())
                .custCodeUtel1(customer.getCustCodeUtel1())
                .custCodeUname2(customer.getCustCodeUname2())
                .custCodeUtel2(customer.getCustCodeUtel2())
                .custCodeUname3(customer.getCustCodeUname3())
                .custCodeUtel3(customer.getCustCodeUtel3())
                .custCodeFax(customer.getCustCodeFax())
                .custCodePost(customer.getCustCodePost())
                .custCodeAddr1(customer.getCustCodeAddr1())
                .custCodeAddr2(customer.getCustCodeAddr2())
                .custCodeAddr(customer.getCustCodeAddr())
                .custCodeEmail(customer.getCustCodeEmail())
                .custCodeHttp(customer.getCustCodeHttp())
                .custCodeSawon(customer.getCustCodeSawon())
                .custCodeBuse(customer.getCustCodeBuse())
                .custCodeBank(customer.getCustCodeBank())
                .custCodeBkname(customer.getCustCodeBkname())
                .custCodeBkno(customer.getCustCodeBkno())
                .custCodeBkuname(customer.getCustCodeBkuname())
                .custCodeCountry(customer.getCustCodeCountry())
                .custCodeLocal(customer.getCustCodeLocal())
                .custCodeUseAcc(customer.getCustCodeUseAcc())
                .custCodeUsePur(customer.getCustCodeUsePur())
                .custCodeUsePos(customer.getCustCodeUsePos())
                .custCodeBubin(customer.getCustCodeBubin())
                .custCodeOcust(customer.getCustCodeOcust())
                .custCodeTdiv(customer.getCustCodeTdiv())
                .custCodeLimit(customer.getCustCodeLimit())
                .custCodeSdate(customer.getCustCodeSdate())
                .custCodeEdate(customer.getCustCodeEdate())
                .custCodePdate(customer.getCustCodePdate())
                .custCodeMcharge(customer.getCustCodeMcharge())
                .custCodePtype(customer.getCustCodePtype())
                .custCodeWeldAgent(customer.getCustCodeWeldAgent())
                .custCodeRemark(customer.getCustCodeRemark())
                .custCodeFdate(customer.getCustCodeFdate())
                .custCodeFuser(customer.getCustCodeFuser())
                .custCodeLdate(customer.getCustCodeLdate())
                .custCodeLuser(customer.getCustCodeLuser())
                // 추가 정보
                .displayName(customer.getDisplayName())
                .fullAddress(customer.getFullAddress())
                .isActive(customer.isActive())
                .canPurchase(customer.canPurchase())
                .canPos(customer.canPos())
                .custCodeDcodDisplayName(dcodDisplayName) // DCOD 코드 표시명
                .build();
    }
} 