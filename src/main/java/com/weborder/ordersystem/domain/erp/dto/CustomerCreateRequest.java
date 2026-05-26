package com.weborder.ordersystem.domain.erp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerCreateRequest {
    private String custCodeNum;
    private String custCodeName;
    private String custCodeSano;
    private String custCodePart1;
    private String custCodePart2;
    private String custCodeUname1;
    private String custCodeUname2;
    private String custCodeUtel1;
    private String custCodeUtel2;
    private String custCodeFax;
    private String custCodeEmail;
    private String custCodePost;
    private String custCodeAddr1;
    private String custCodeAddr2;
    private String custCodeAddr;
    private String custCodeRemark;
    private Integer custCodeUseAcc;
    private Integer custCodeUsePur;
    private Integer custCodeUsePos;
    private Integer custCodeRooms;
}
