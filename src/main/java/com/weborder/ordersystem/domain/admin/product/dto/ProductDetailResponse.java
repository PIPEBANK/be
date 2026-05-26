package com.weborder.ordersystem.domain.admin.product.dto;

import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProductDetailResponse {
    private Integer itemCode;
    private String itemNum;
    private String itemName;
    private String itemEname;
    private String itemWord;
    private String spec;
    private BigDecimal spec2;
    private String unit;

    private Integer pcust;
    private String pcustName;
    private Integer scust;
    private String scustName;

    private BigDecimal purchaseRate;
    private BigDecimal lastPurchaseRate;
    private String lastPurchaseDate;
    private BigDecimal saleRate;
    private BigDecimal lastSaleRate;
    private String lastSaleDate;
    private BigDecimal avrate;

    private String dcod;
    private String dcodName;
    private String pcod;
    private String pcodName;
    private String scod;
    private String scodName;
    private String ldiv;
    private String ldivName;
    private String dsdiv;
    private String dsdivName;
    private String lproc;
    private String lprocName;

    private Integer use;
    private BigDecimal stock;
    private String remark;
    private String tags;

    private BigDecimal stockQuantity;
    private BigDecimal availableStock;
    private List<ItemImageResponse> images;
    private String thumbnailUrl;

    public static ProductDetailResponse from(ItemCode item, BigDecimal stockQty, BigDecimal availStock,
                                              List<ItemImageResponse> images, String thumbnailUrl) {
        return from(item, stockQty, availStock, images, thumbnailUrl,
                null, null, null, null, null, null, null, null);
    }

    public static ProductDetailResponse from(ItemCode item, BigDecimal stockQty, BigDecimal availStock,
                                              List<ItemImageResponse> images, String thumbnailUrl,
                                              String pcustName, String scustName,
                                              String dcodName, String pcodName, String scodName,
                                              String ldivName, String dsdivName, String lprocName) {
        return ProductDetailResponse.builder()
                .itemCode(item.getItemCodeCode())
                .itemNum(item.getItemCodeNum())
                .itemName(item.getItemCodeHnam())
                .itemEname(item.getItemCodeEnam())
                .itemWord(item.getItemCodeWord())
                .spec(item.getItemCodeSpec())
                .spec2(item.getItemCodeSpec2())
                .unit(item.getItemCodeUnit())
                .pcust(item.getItemCodePcust())
                .pcustName(pcustName)
                .scust(item.getItemCodeScust())
                .scustName(scustName)
                .purchaseRate(item.getItemCodePrate())
                .lastPurchaseRate(item.getItemCodePlrate())
                .lastPurchaseDate(item.getItemCodePlrdate())
                .saleRate(item.getItemCodeSrate())
                .lastSaleRate(item.getItemCodeSlrate())
                .lastSaleDate(item.getItemCodeSlrdate())
                .avrate(item.getItemCodeAvrate())
                .dcod(item.getItemCodeDcod())
                .dcodName(dcodName)
                .pcod(item.getItemCodePcod())
                .pcodName(pcodName)
                .scod(item.getItemCodeScod())
                .scodName(scodName)
                .ldiv(item.getItemCodeLdiv())
                .ldivName(ldivName)
                .dsdiv(item.getItemCodeDsdiv())
                .dsdivName(dsdivName)
                .lproc(item.getItemCodeLproc())
                .lprocName(lprocName)
                .use(item.getItemCodeUse())
                .stock(item.getItemCodeStock())
                .remark(item.getItemCodeRemark())
                .tags(item.getItemCodeTags())
                .stockQuantity(stockQty)
                .availableStock(availStock)
                .images(images)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
