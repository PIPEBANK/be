package com.weborder.ordersystem.domain.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 품목 코드 엔티티
 * co_item_code 테이블 매핑
 */
@Entity
@Table(name = "co_item_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCode {

    @Id
    @Column(name = "ITEM_CODE_CODE", columnDefinition = "int(11)")
    private Integer itemCodeCode;

    @Column(name = "ITEM_CODE_NUM", nullable = false, length = 40)
    private String itemCodeNum;  // 품목번호 (회사에서 직접관리하는번호)

    @Column(name = "ITEM_CODE_DCOD", nullable = false, columnDefinition = "char(10)")
    private String itemCodeDcod; // 품목분류코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_PCOD", nullable = false, columnDefinition = "char(10)")
    private String itemCodePcod; // 매입품목구분코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_SCOD", nullable = false, columnDefinition = "char(10)")
    private String itemCodeScod; // 매출품목구분코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_HNAM", nullable = false, length = 100)
    private String itemCodeHnam; // 품목명(한글)

    @Column(name = "ITEM_CODE_ENAM", nullable = false, length = 100)
    private String itemCodeEnam; // 품목명(영문)

    @Column(name = "ITEM_CODE_WORD", nullable = false, length = 100)
    private String itemCodeWord; // 품목명(이니셜)

    @Column(name = "ITEM_CODE_SPEC", nullable = false, length = 200)
    private String itemCodeSpec; // 품목규격

    @Column(name = "ITEM_CODE_SPEC2", nullable = false, precision = 18, scale = 0)
    private BigDecimal itemCodeSpec2; // 표준중량(m당)

    @Column(name = "ITEM_CODE_UNIT", nullable = false, length = 50)
    private String itemCodeUnit; // 단위

    @Column(name = "ITEM_CODE_PCUST", nullable = false, columnDefinition = "int(11)")
    private Integer itemCodePcust; // 매입처 (Customer테이블)

    @Column(name = "ITEM_CODE_PCUST2", nullable = false, columnDefinition = "int(11)")
    private Integer itemCodePcust2; // 매입처[이전] (Customer테이블)

    @Column(name = "ITEM_CODE_SCUST", nullable = false, columnDefinition = "int(11)")
    private Integer itemCodeScust; // 매출처 (Customer테이블)

    @Column(name = "ITEM_CODE_CALC", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeCalc; // 정산 checkbox

    @Column(name = "ITEM_CODE_SDIV", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeSdiv; // S checkbox

    @Column(name = "ITEM_CODE_VDIV", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeVdiv; // 부가세여부

    @Column(name = "ITEM_CODE_ADIV", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeAdiv; // 예수금여부

    @Column(name = "ITEM_CODE_PRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemCodePrate; // 매입단가

    @Column(name = "ITEM_CODE_PLRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemCodePlrate; // 최종매입단가

    @Column(name = "ITEM_CODE_PLRDATE", nullable = false, columnDefinition = "char(8)")
    private String itemCodePlrdate; // 최종매입일

    @Column(name = "ITEM_CODE_SRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemCodeSrate; // 판매단가

    @Column(name = "ITEM_CODE_SLRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemCodeSlrate; // 최종판매단가

    @Column(name = "ITEM_CODE_SLRDATE", nullable = false, columnDefinition = "char(8)")
    private String itemCodeSlrdate; // 최종판매일

    @Column(name = "ITEM_CODE_LDIV", nullable = false, columnDefinition = "char(10)")
    private String itemCodeLdiv; // 최종거래가적용구분코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeUse; // 사용여부

    @Column(name = "ITEM_CODE_AVRATE", nullable = false, precision = 18, scale = 0)
    private BigDecimal itemCodeAvrate; // 평균매입가

    @Column(name = "ITEM_CODE_DSDIV", nullable = false, columnDefinition = "char(10)")
    private String itemCodeDsdiv; // D/S구분코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_BRAND", nullable = false, length = 200)
    private String itemCodeBrand; // 브랜드

    @Column(name = "ITEM_CODE_PLACE", nullable = false, length = 50)
    private String itemCodePlace; // 보관장소

    @Column(name = "ITEM_CODE_NATIVE", nullable = false, length = 100)
    private String itemCodeNative; // 제조사

    @Column(name = "ITEM_CODE_STOCK", nullable = false, precision = 18, scale = 0)
    private BigDecimal itemCodeStock; // 적정재고

    @Column(name = "ITEM_CODE_BITEM", nullable = false, columnDefinition = "int(11)")
    private Integer itemCodeBitem; // 기초재고품목코드

    @Column(name = "ITEM_CODE_PITEM", nullable = false, columnDefinition = "int(11)")
    private Integer itemCodePitem; // 매입품목코드

    @Column(name = "ITEM_CODE_CHNG", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeChng; // 매입처 자동변경 여부

    @Column(name = "ITEM_CODE_AUTO", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeAuto; // 자동매입 여부

    @Column(name = "ITEM_CODE_MARKET", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeMarket; // 시장사입 여부

    @Column(name = "ITEM_CODE_KITCHEN", nullable = false, columnDefinition = "char(10)")
    private String itemCodeKitchen; // 주방구분 코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_PRINT", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodePrint; // 주문서 출력 여부

    @Column(name = "ITEM_CODE_DCLOCK", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeDclock; // D/C 잠금 여부

    @Column(name = "ITEM_CODE_LPROC", nullable = false, columnDefinition = "char(10)")
    private String itemCodeLproc; // 최종공정코드 (코드관리테이블)

    @Column(name = "ITEM_CODE_URATE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeUrate; // 업장별 단가 사용 여부

    @Column(name = "ITEM_CODE_OPTION", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeOption; // 주문옵션사용 여부

    @Column(name = "ITEM_CODE_NSTOCK", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeNstock; // 재고관리 여부

    @Column(name = "ITEM_CODE_SERIAL", nullable = false, columnDefinition = "tinyint(3)")
    private Integer itemCodeSerial; // 일련번호관리 여부

    @Column(name = "ITEM_CODE_DIV1", nullable = false, columnDefinition = "char(10)")
    private String itemCodeDiv1; // 제품종류 코드 (제품구분코드테이블)

    @Column(name = "ITEM_CODE_DIV2", nullable = false, columnDefinition = "char(10)")
    private String itemCodeDiv2; // 제품군 코드 (제품구분코드테이블)

    @Column(name = "ITEM_CODE_DIV3", nullable = false, columnDefinition = "char(10)")
    private String itemCodeDiv3; // 제품용도 코드 (제품구분코드테이블)

    @Column(name = "ITEM_CODE_REMARK", nullable = false, length = 200)
    private String itemCodeRemark; // 비고

    @Column(name = "ITEM_CODE_TAGS", length = 500)
    private String itemCodeTags; // 검색태그 (예: #물 #워터 #생수)

    @Column(name = "ITEM_CODE_FDATE", nullable = false)
    private LocalDateTime itemCodeFdate; // 최초등록일

    @Column(name = "ITEM_CODE_FUSER", nullable = false, length = 20)
    private String itemCodeFuser; // 최초등록자

    @Column(name = "ITEM_CODE_LDATE", nullable = false)
    private LocalDateTime itemCodeLdate; // 최종수정일

    @Column(name = "ITEM_CODE_LUSER", nullable = false, length = 20)
    private String itemCodeLuser; // 최종수정자

    @Column(name = "ITEM_CODE_MOQ", nullable = false, columnDefinition = "int(11)")
    private Integer itemCodeMoq; // 최소주문수량

    @Builder
    public ItemCode(Integer itemCodeCode, String itemCodeNum, String itemCodeDcod, String itemCodePcod,
                   String itemCodeScod, String itemCodeHnam, String itemCodeEnam, String itemCodeWord,
                   String itemCodeSpec, BigDecimal itemCodeSpec2, String itemCodeUnit, Integer itemCodePcust,
                   Integer itemCodePcust2, Integer itemCodeScust, Integer itemCodeCalc, Integer itemCodeSdiv,
                   Integer itemCodeVdiv, Integer itemCodeAdiv, BigDecimal itemCodePrate, BigDecimal itemCodePlrate,
                   String itemCodePlrdate, BigDecimal itemCodeSrate, BigDecimal itemCodeSlrate, String itemCodeSlrdate,
                   String itemCodeLdiv, Integer itemCodeUse, BigDecimal itemCodeAvrate, String itemCodeDsdiv,
                   String itemCodeBrand, String itemCodePlace, String itemCodeNative, BigDecimal itemCodeStock,
                   Integer itemCodeBitem, Integer itemCodePitem, Integer itemCodeChng, Integer itemCodeAuto,
                   Integer itemCodeMarket, String itemCodeKitchen, Integer itemCodePrint, Integer itemCodeDclock,
                   String itemCodeLproc, Integer itemCodeUrate, Integer itemCodeOption, Integer itemCodeNstock,
                   Integer itemCodeSerial, String itemCodeDiv1, String itemCodeDiv2, String itemCodeDiv3,
                   String itemCodeRemark, String itemCodeTags, LocalDateTime itemCodeFdate, String itemCodeFuser,
                   LocalDateTime itemCodeLdate, String itemCodeLuser, Integer itemCodeMoq) {
        this.itemCodeCode = itemCodeCode;
        this.itemCodeNum = itemCodeNum;
        this.itemCodeDcod = itemCodeDcod;
        this.itemCodePcod = itemCodePcod;
        this.itemCodeScod = itemCodeScod;
        this.itemCodeHnam = itemCodeHnam;
        this.itemCodeEnam = itemCodeEnam;
        this.itemCodeWord = itemCodeWord;
        this.itemCodeSpec = itemCodeSpec;
        this.itemCodeSpec2 = itemCodeSpec2;
        this.itemCodeUnit = itemCodeUnit;
        this.itemCodePcust = itemCodePcust;
        this.itemCodePcust2 = itemCodePcust2;
        this.itemCodeScust = itemCodeScust;
        this.itemCodeCalc = itemCodeCalc;
        this.itemCodeSdiv = itemCodeSdiv;
        this.itemCodeVdiv = itemCodeVdiv;
        this.itemCodeAdiv = itemCodeAdiv;
        this.itemCodePrate = itemCodePrate;
        this.itemCodePlrate = itemCodePlrate;
        this.itemCodePlrdate = itemCodePlrdate;
        this.itemCodeSrate = itemCodeSrate;
        this.itemCodeSlrate = itemCodeSlrate;
        this.itemCodeSlrdate = itemCodeSlrdate;
        this.itemCodeLdiv = itemCodeLdiv;
        this.itemCodeUse = itemCodeUse;
        this.itemCodeAvrate = itemCodeAvrate;
        this.itemCodeDsdiv = itemCodeDsdiv;
        this.itemCodeBrand = itemCodeBrand;
        this.itemCodePlace = itemCodePlace;
        this.itemCodeNative = itemCodeNative;
        this.itemCodeStock = itemCodeStock;
        this.itemCodeBitem = itemCodeBitem;
        this.itemCodePitem = itemCodePitem;
        this.itemCodeChng = itemCodeChng;
        this.itemCodeAuto = itemCodeAuto;
        this.itemCodeMarket = itemCodeMarket;
        this.itemCodeKitchen = itemCodeKitchen;
        this.itemCodePrint = itemCodePrint;
        this.itemCodeDclock = itemCodeDclock;
        this.itemCodeLproc = itemCodeLproc;
        this.itemCodeUrate = itemCodeUrate;
        this.itemCodeOption = itemCodeOption;
        this.itemCodeNstock = itemCodeNstock;
        this.itemCodeSerial = itemCodeSerial;
        this.itemCodeDiv1 = itemCodeDiv1;
        this.itemCodeDiv2 = itemCodeDiv2;
        this.itemCodeDiv3 = itemCodeDiv3;
        this.itemCodeRemark = itemCodeRemark;
        this.itemCodeTags = itemCodeTags;
        this.itemCodeFdate = itemCodeFdate;
        this.itemCodeFuser = itemCodeFuser;
        this.itemCodeLdate = itemCodeLdate;
        this.itemCodeLuser = itemCodeLuser;
        this.itemCodeMoq = itemCodeMoq;
    }

    // 제품 정보 수정 (관리자용 — ProductCreateRequest 기반)
    public void updateProductInfo(
            com.weborder.ordersystem.domain.admin.product.dto.ProductCreateRequest req,
            String luser) {
        if (req.getItemNum() != null) this.itemCodeNum = req.getItemNum();
        if (req.getItemName() != null) this.itemCodeHnam = req.getItemName();
        if (req.getItemEname() != null) this.itemCodeEnam = req.getItemEname();
        if (req.getItemWord() != null) this.itemCodeWord = req.getItemWord();
        if (req.getSpec() != null) this.itemCodeSpec = req.getSpec();
        if (req.getSpec2() != null) this.itemCodeSpec2 = req.getSpec2();
        if (req.getUnit() != null) this.itemCodeUnit = req.getUnit();
        if (req.getPcust() != null && !req.getPcust().equals(this.itemCodePcust)) {
            this.itemCodePcust2 = this.itemCodePcust;
            this.itemCodePcust = req.getPcust();
        }
        if (req.getScust() != null) this.itemCodeScust = req.getScust();
        if (req.getPurchaseRate() != null) this.itemCodePrate = req.getPurchaseRate();
        if (req.getLastPurchaseRate() != null) this.itemCodePlrate = req.getLastPurchaseRate();
        if (req.getLastPurchaseDate() != null) this.itemCodePlrdate = req.getLastPurchaseDate();
        if (req.getSaleRate() != null) this.itemCodeSrate = req.getSaleRate();
        if (req.getLastSaleRate() != null) this.itemCodeSlrate = req.getLastSaleRate();
        if (req.getLastSaleDate() != null) this.itemCodeSlrdate = req.getLastSaleDate();
        if (req.getAvrate() != null) this.itemCodeAvrate = req.getAvrate();
        if (req.getDcod() != null) this.itemCodeDcod = req.getDcod();
        if (req.getPcod() != null) this.itemCodePcod = req.getPcod();
        if (req.getScod() != null) this.itemCodeScod = req.getScod();
        if (req.getLdiv() != null) this.itemCodeLdiv = req.getLdiv();
        if (req.getDsdiv() != null) this.itemCodeDsdiv = req.getDsdiv();
        if (req.getLproc() != null) this.itemCodeLproc = req.getLproc();
        if (req.getUse() != null) this.itemCodeUse = req.getUse();
        if (req.getStock() != null) this.itemCodeStock = req.getStock();
        if (req.getRemark() != null) this.itemCodeRemark = req.getRemark();
        if (req.getTags() != null) this.itemCodeTags = req.getTags();
        this.itemCodeLdate = LocalDateTime.now();
        this.itemCodeLuser = luser;
    }

    public void updateUseStatus(Integer use, String luser) {
        this.itemCodeUse = use;
        this.itemCodeLdate = LocalDateTime.now();
        this.itemCodeLuser = luser;
    }

    // 비즈니스 메서드
    public boolean isActive() {
        return itemCodeUse == 1;
    }

    public boolean hasVat() {
        return itemCodeVdiv == 1;
    }

    public String getDisplayName() {
        return itemCodeHnam != null ? itemCodeHnam : itemCodeEnam;
    }

    public String getShortName() {
        return itemCodeWord;
    }

    public String getFullSpec() {
        if (itemCodeSpec != null && itemCodeSpec2 != null) {
            return itemCodeSpec + " (" + itemCodeSpec2 + ")";
        } else if (itemCodeSpec != null) {
            return itemCodeSpec;
        }
        return "";
    }

    public String getBrandInfo() {
        return itemCodeBrand;
    }

    public String getOrigin() {
        return itemCodeNative;
    }

    @Override
    public String toString() {
        return String.format("ItemCode{code=%d, name='%s', spec='%s'}",
            itemCodeCode, itemCodeHnam, itemCodeSpec);
    }
}
