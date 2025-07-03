package com.pipebank.ordersystem.domain.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "co_cust_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUST_CODE_CODE", columnDefinition = "int(10)")
    private Integer custCodeCode;

    @Column(name = "CUST_CODE_NUM", nullable = false, length = 100)
    private String custCodeNum;

    @Column(name = "CUST_CODE_DCOD", nullable = false, columnDefinition = "char(10)")
    private String custCodeDcod;

    @Column(name = "CUST_CODE_NAME", nullable = false, length = 200)
    private String custCodeName;

    @Column(name = "CUST_CODE_WORD", nullable = false, length = 50)
    private String custCodeWord;

    @Column(name = "CUST_CODE_ANAM", nullable = false, length = 200)
    private String custCodeAnam;

    @Column(name = "CUST_CODE_SANO", nullable = false, length = 20)
    private String custCodeSano;

    @Column(name = "CUST_CODE_SANO_SEQ", nullable = false, length = 4)
    private String custCodeSanoSeq;

    @Column(name = "CUST_CODE_PART1", nullable = false, length = 100)
    private String custCodePart1;

    @Column(name = "CUST_CODE_PART2", nullable = false, length = 100)
    private String custCodePart2;

    @Column(name = "CUST_CODE_UNAME1", nullable = false, length = 50)
    private String custCodeUname1;

    @Column(name = "CUST_CODE_UTEL1", nullable = false, length = 20)
    private String custCodeUtel1;

    @Column(name = "CUST_CODE_UNAME2", nullable = false, length = 50)
    private String custCodeUname2;

    @Column(name = "CUST_CODE_UTEL2", nullable = false, length = 20)
    private String custCodeUtel2;

    @Column(name = "CUST_CODE_UNAME3", nullable = true, length = 50)
    private String custCodeUname3;

    @Column(name = "CUST_CODE_UTEL3", nullable = true, length = 50)
    private String custCodeUtel3;

    @Column(name = "CUST_CODE_FAX", nullable = false, length = 20)
    private String custCodeFax;

    @Column(name = "CUST_CODE_POST", nullable = false, columnDefinition = "char(7)")
    private String custCodePost;

    @Column(name = "CUST_CODE_ADDR1", nullable = false, length = 200)
    private String custCodeAddr1;

    @Column(name = "CUST_CODE_ADDR2", nullable = false, length = 200)
    private String custCodeAddr2;

    @Column(name = "CUST_CODE_ADDR", nullable = false, length = 400)
    private String custCodeAddr;

    @Column(name = "CUST_CODE_EMAIL", nullable = false, length = 40)
    private String custCodeEmail;

    @Column(name = "CUST_CODE_HTTP", nullable = false, length = 60)
    private String custCodeHttp;

    @Column(name = "CUST_CODE_SAWON", nullable = false, columnDefinition = "int(10)")
    private Integer custCodeSawon;

    @Column(name = "CUST_CODE_BUSE", nullable = false, columnDefinition = "int(10)")
    private Integer custCodeBuse;

    @Column(name = "CUST_CODE_BANK", nullable = false, columnDefinition = "char(10)")
    private String custCodeBank;

    @Column(name = "CUST_CODE_BKNAME", nullable = false, length = 30)
    private String custCodeBkname;

    @Column(name = "CUST_CODE_BKNO", nullable = false, length = 30)
    private String custCodeBkno;

    @Column(name = "CUST_CODE_BKUNAME", nullable = false, length = 30)
    private String custCodeBkuname;

    @Column(name = "CUST_CODE_COUNTRY", nullable = false, columnDefinition = "char(10)")
    private String custCodeCountry;

    @Column(name = "CUST_CODE_LOCAL", nullable = false, columnDefinition = "char(10)")
    private String custCodeLocal;

    @Column(name = "CUST_CODE_USE_ACC", nullable = false, columnDefinition = "tinyint(3)")
    private Integer custCodeUseAcc;

    @Column(name = "CUST_CODE_USE_PUR", nullable = false, columnDefinition = "tinyint(3)")
    private Integer custCodeUsePur;

    @Column(name = "CUST_CODE_USE_POS", nullable = false, columnDefinition = "tinyint(3)")
    private Integer custCodeUsePos;

    @Column(name = "CUST_CODE_BUBIN", nullable = false, length = 14)
    private String custCodeBubin;

    @Column(name = "CUST_CODE_OCUST", nullable = false, columnDefinition = "int(10)")
    private Integer custCodeOcust;

    @Column(name = "CUST_CODE_TDIV", nullable = false, columnDefinition = "char(10)")
    private String custCodeTdiv;

    @Column(name = "CUST_CODE_LIMIT", nullable = false, precision = 18, scale = 0)
    private BigDecimal custCodeLimit;

    @Column(name = "CUST_CODE_SDATE", nullable = false, columnDefinition = "char(8)")
    private String custCodeSdate;

    @Column(name = "CUST_CODE_EDATE", nullable = false, columnDefinition = "char(8)")
    private String custCodeEdate;

    @Column(name = "CUST_CODE_PDATE", nullable = false, columnDefinition = "char(8)")
    private String custCodePdate;

    @Column(name = "CUST_CODE_MCHARGE", nullable = false, precision = 18, scale = 0)
    private BigDecimal custCodeMcharge;

    @Column(name = "CUST_CODE_PTYPE", nullable = true, columnDefinition = "char(10)")
    private String custCodePtype;

    @Column(name = "CUST_CODE_WELD_AGENT", nullable = true, columnDefinition = "int(11)")
    private Integer custCodeWeldAgent;

    @Column(name = "CUST_CODE_REMARK", nullable = false, length = 1000)
    private String custCodeRemark;

    @Column(name = "CUST_CODE_FDATE", nullable = false)
    private LocalDateTime custCodeFdate;

    @Column(name = "CUST_CODE_FUSER", nullable = false, length = 20)
    private String custCodeFuser;

    @Column(name = "CUST_CODE_LDATE", nullable = false)
    private LocalDateTime custCodeLdate;

    @Column(name = "CUST_CODE_LUSER", nullable = false, length = 20)
    private String custCodeLuser;

    @Builder
    public Customer(String custCodeNum, String custCodeDcod, String custCodeName, String custCodeWord,
                   String custCodeAnam, String custCodeSano, String custCodeSanoSeq, String custCodePart1,
                   String custCodePart2, String custCodeUname1, String custCodeUtel1, String custCodeUname2,
                   String custCodeUtel2, String custCodeUname3, String custCodeUtel3, String custCodeFax,
                   String custCodePost, String custCodeAddr1, String custCodeAddr2, String custCodeAddr,
                   String custCodeEmail, String custCodeHttp, Integer custCodeSawon, Integer custCodeBuse,
                   String custCodeBank, String custCodeBkname, String custCodeBkno, String custCodeBkuname,
                   String custCodeCountry, String custCodeLocal, Integer custCodeUseAcc, Integer custCodeUsePur,
                   Integer custCodeUsePos, String custCodeBubin, Integer custCodeOcust, String custCodeTdiv,
                   BigDecimal custCodeLimit, String custCodeSdate, String custCodeEdate, String custCodePdate,
                   BigDecimal custCodeMcharge, String custCodePtype, Integer custCodeWeldAgent,
                   String custCodeRemark, LocalDateTime custCodeFdate, String custCodeFuser,
                   LocalDateTime custCodeLdate, String custCodeLuser) {
        this.custCodeNum = custCodeNum;
        this.custCodeDcod = custCodeDcod;
        this.custCodeName = custCodeName;
        this.custCodeWord = custCodeWord;
        this.custCodeAnam = custCodeAnam;
        this.custCodeSano = custCodeSano;
        this.custCodeSanoSeq = custCodeSanoSeq;
        this.custCodePart1 = custCodePart1;
        this.custCodePart2 = custCodePart2;
        this.custCodeUname1 = custCodeUname1;
        this.custCodeUtel1 = custCodeUtel1;
        this.custCodeUname2 = custCodeUname2;
        this.custCodeUtel2 = custCodeUtel2;
        this.custCodeUname3 = custCodeUname3;
        this.custCodeUtel3 = custCodeUtel3;
        this.custCodeFax = custCodeFax;
        this.custCodePost = custCodePost;
        this.custCodeAddr1 = custCodeAddr1;
        this.custCodeAddr2 = custCodeAddr2;
        this.custCodeAddr = custCodeAddr;
        this.custCodeEmail = custCodeEmail;
        this.custCodeHttp = custCodeHttp;
        this.custCodeSawon = custCodeSawon;
        this.custCodeBuse = custCodeBuse;
        this.custCodeBank = custCodeBank;
        this.custCodeBkname = custCodeBkname;
        this.custCodeBkno = custCodeBkno;
        this.custCodeBkuname = custCodeBkuname;
        this.custCodeCountry = custCodeCountry;
        this.custCodeLocal = custCodeLocal;
        this.custCodeUseAcc = custCodeUseAcc;
        this.custCodeUsePur = custCodeUsePur;
        this.custCodeUsePos = custCodeUsePos;
        this.custCodeBubin = custCodeBubin;
        this.custCodeOcust = custCodeOcust;
        this.custCodeTdiv = custCodeTdiv;
        this.custCodeLimit = custCodeLimit;
        this.custCodeSdate = custCodeSdate;
        this.custCodeEdate = custCodeEdate;
        this.custCodePdate = custCodePdate;
        this.custCodeMcharge = custCodeMcharge;
        this.custCodePtype = custCodePtype;
        this.custCodeWeldAgent = custCodeWeldAgent;
        this.custCodeRemark = custCodeRemark;
        this.custCodeFdate = custCodeFdate;
        this.custCodeFuser = custCodeFuser;
        this.custCodeLdate = custCodeLdate;
        this.custCodeLuser = custCodeLuser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return custCodeUseAcc == 1;
    }

    public boolean canPurchase() {
        return custCodeUsePur == 1;
    }

    public boolean canPos() {
        return custCodeUsePos == 1;
    }

    public String getDisplayName() {
        return custCodeName != null ? custCodeName : custCodeAnam;
    }

    public String getFullAddress() {
        return custCodeAddr;
    }
} 