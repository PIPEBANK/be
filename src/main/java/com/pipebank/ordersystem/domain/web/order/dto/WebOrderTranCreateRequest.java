package com.pipebank.ordersystem.domain.web.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class WebOrderTranCreateRequest {

    @NotBlank(message = "주문일자는 필수입니다")
    @Size(min = 8, max = 8, message = "주문일자는 8자리여야 합니다")
    private String orderTranDate;

    @NotNull(message = "소속은 필수입니다")
    private Integer orderTranSosok;

    @NotBlank(message = "업장은 필수입니다")
    @Size(max = 10, message = "업장은 10자 이하여야 합니다")
    private String orderTranUjcd;

    @NotNull(message = "주문번호는 필수입니다")
    private Integer orderTranAcno;

    @NotBlank(message = "품목버전은 필수입니다")
    @Size(max = 10, message = "품목버전은 10자 이하여야 합니다")
    private String orderTranItemVer;

    @NotNull(message = "품목코드는 필수입니다")
    private Integer orderTranItem;

    @NotBlank(message = "품목명은 필수입니다")
    @Size(max = 50, message = "품목명은 50자 이하여야 합니다")
    private String orderTranDeta;

    @NotBlank(message = "규격은 필수입니다")
    @Size(max = 100, message = "규격은 100자 이하여야 합니다")
    private String orderTranSpec;

    @NotBlank(message = "단위는 필수입니다")
    @Size(max = 50, message = "단위는 50자 이하여야 합니다")
    private String orderTranUnit;

    @NotNull(message = "정산 여부는 필수입니다")
    private Integer orderTranCalc;

    @NotNull(message = "부가세 여부는 필수입니다")
    private Integer orderTranVdiv;

    @NotNull(message = "예수금 여부는 필수입니다")
    private Integer orderTranAdiv;

    @NotNull(message = "기본단가는 필수입니다")
    @DecimalMin(value = "0", message = "기본단가는 0 이상이어야 합니다")
    private BigDecimal orderTranRate;

    @NotNull(message = "수량은 필수입니다")
    @DecimalMin(value = "0", message = "수량은 0 이상이어야 합니다")
    private BigDecimal orderTranCnt;

    @NotNull(message = "환산중량은 필수입니다")
    @DecimalMin(value = "0", message = "환산중량은 0 이상이어야 합니다")
    private BigDecimal orderTranConvertWeight;

    @NotNull(message = "할인율은 필수입니다")
    @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
    private BigDecimal orderTranDcPer;

    @NotNull(message = "할인금액은 필수입니다")
    @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
    private BigDecimal orderTranDcAmt;

    @NotNull(message = "외화단가는 필수입니다")
    @DecimalMin(value = "0", message = "외화단가는 0 이상이어야 합니다")
    private BigDecimal orderTranForiAmt;

    @NotNull(message = "판매단가는 필수입니다")
    @DecimalMin(value = "0", message = "판매단가는 0 이상이어야 합니다")
    private BigDecimal orderTranAmt;

    @NotNull(message = "공급가는 필수입니다")
    @DecimalMin(value = "0", message = "공급가는 0 이상이어야 합니다")
    private BigDecimal orderTranNet;

    @NotNull(message = "부가세는 필수입니다")
    @DecimalMin(value = "0", message = "부가세는 0 이상이어야 합니다")
    private BigDecimal orderTranVat;

    @NotNull(message = "예수금은 필수입니다")
    @DecimalMin(value = "0", message = "예수금은 0 이상이어야 합니다")
    private BigDecimal orderTranAdv;

    @NotNull(message = "합산금액은 필수입니다")
    @DecimalMin(value = "0", message = "합산금액은 0 이상이어야 합니다")
    private BigDecimal orderTranTot;

    @NotNull(message = "이전판매단가는 필수입니다")
    @DecimalMin(value = "0", message = "이전판매단가는 0 이상이어야 합니다")
    private BigDecimal orderTranLrate;

    @NotNull(message = "매입가는 필수입니다")
    @DecimalMin(value = "0", message = "매입가는 0 이상이어야 합니다")
    private BigDecimal orderTranPrice;

    @NotNull(message = "매입가증감은 필수입니다")
    @DecimalMin(value = "0", message = "매입가증감은 0 이상이어야 합니다")
    private BigDecimal orderTranPrice2;

    @NotNull(message = "판매가미반영 여부는 필수입니다")
    private Integer orderTranLdiv;

    @NotBlank(message = "비고는 필수입니다")
    @Size(max = 200, message = "비고는 200자 이하여야 합니다")
    private String orderTranRemark;

    @NotBlank(message = "상태코드는 필수입니다")
    @Size(max = 10, message = "상태코드는 10자 이하여야 합니다")
    private String orderTranStau;

    @NotNull(message = "중량단가는 필수입니다")
    @DecimalMin(value = "0", message = "중량단가는 0 이상이어야 합니다")
    private BigDecimal orderTranWamt;

    public WebOrderTranCreateRequest(String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno,
                                   String orderTranItemVer, Integer orderTranItem, String orderTranDeta, String orderTranSpec,
                                   String orderTranUnit, Integer orderTranCalc, Integer orderTranVdiv, Integer orderTranAdiv,
                                   BigDecimal orderTranRate, BigDecimal orderTranCnt, BigDecimal orderTranConvertWeight,
                                   BigDecimal orderTranDcPer, BigDecimal orderTranDcAmt, BigDecimal orderTranForiAmt,
                                   BigDecimal orderTranAmt, BigDecimal orderTranNet, BigDecimal orderTranVat,
                                   BigDecimal orderTranAdv, BigDecimal orderTranTot, BigDecimal orderTranLrate,
                                   BigDecimal orderTranPrice, BigDecimal orderTranPrice2, Integer orderTranLdiv,
                                   String orderTranRemark, String orderTranStau, BigDecimal orderTranWamt) {
        this.orderTranDate = orderTranDate;
        this.orderTranSosok = orderTranSosok;
        this.orderTranUjcd = orderTranUjcd;
        this.orderTranAcno = orderTranAcno;
        this.orderTranItemVer = orderTranItemVer;
        this.orderTranItem = orderTranItem;
        this.orderTranDeta = orderTranDeta;
        this.orderTranSpec = orderTranSpec;
        this.orderTranUnit = orderTranUnit;
        this.orderTranCalc = orderTranCalc;
        this.orderTranVdiv = orderTranVdiv;
        this.orderTranAdiv = orderTranAdiv;
        this.orderTranRate = orderTranRate;
        this.orderTranCnt = orderTranCnt;
        this.orderTranConvertWeight = orderTranConvertWeight;
        this.orderTranDcPer = orderTranDcPer;
        this.orderTranDcAmt = orderTranDcAmt;
        this.orderTranForiAmt = orderTranForiAmt;
        this.orderTranAmt = orderTranAmt;
        this.orderTranNet = orderTranNet;
        this.orderTranVat = orderTranVat;
        this.orderTranAdv = orderTranAdv;
        this.orderTranTot = orderTranTot;
        this.orderTranLrate = orderTranLrate;
        this.orderTranPrice = orderTranPrice;
        this.orderTranPrice2 = orderTranPrice2;
        this.orderTranLdiv = orderTranLdiv;
        this.orderTranRemark = orderTranRemark;
        this.orderTranStau = orderTranStau;
        this.orderTranWamt = orderTranWamt;
    }
} 