package com.weborder.ordersystem.domain.web.customitem.entity;

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

@Entity
@Table(name = "co_custom_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOM_ITEM_CODE")
    private Long customItemCode;

    @Column(name = "CUSTOM_ITEM_CUST", nullable = false, columnDefinition = "int(11)")
    private Integer customItemCust;

    @Column(name = "CUSTOM_ITEM_HNAM", nullable = false, length = 100)
    private String customItemHnam;

    @Column(name = "CUSTOM_ITEM_DESC", length = 500)
    private String customItemDesc;

    @Column(name = "CUSTOM_ITEM_SPEC", length = 200)
    private String customItemSpec;

    @Column(name = "CUSTOM_ITEM_UNIT", length = 50)
    private String customItemUnit;

    @Column(name = "CUSTOM_ITEM_VDIV", nullable = false, columnDefinition = "tinyint(3)")
    private Integer customItemVdiv;

    @Column(name = "CUSTOM_ITEM_SRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal customItemSrate;

    @Column(name = "CUSTOM_ITEM_SLRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal customItemSlrate;

    @Column(name = "CUSTOM_ITEM_SLRDATE", nullable = false, columnDefinition = "char(8)")
    private String customItemSlrdate;

    @Column(name = "CUSTOM_ITEM_REMARK", length = 200)
    private String customItemRemark;

    @Column(name = "CUSTOM_ITEM_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer customItemUse;

    @Column(name = "CUSTOM_ITEM_FDATE")
    private LocalDateTime customItemFdate;

    @Column(name = "CUSTOM_ITEM_FUSER", length = 20)
    private String customItemFuser;

    @Column(name = "CUSTOM_ITEM_LDATE")
    private LocalDateTime customItemLdate;

    @Column(name = "CUSTOM_ITEM_LUSER", length = 20)
    private String customItemLuser;

    @Builder
    public CustomItem(Integer customItemCust, String customItemHnam, String customItemDesc,
                      String customItemSpec, String customItemUnit, Integer customItemVdiv,
                      BigDecimal customItemSrate, BigDecimal customItemSlrate, String customItemSlrdate,
                      String customItemRemark, Integer customItemUse,
                      LocalDateTime customItemFdate, String customItemFuser,
                      LocalDateTime customItemLdate, String customItemLuser) {
        this.customItemCust = customItemCust;
        this.customItemHnam = customItemHnam;
        this.customItemDesc = customItemDesc;
        this.customItemSpec = customItemSpec;
        this.customItemUnit = customItemUnit;
        this.customItemVdiv = customItemVdiv != null ? customItemVdiv : 1;
        this.customItemSrate = customItemSrate != null ? customItemSrate : BigDecimal.ZERO;
        this.customItemSlrate = customItemSlrate != null ? customItemSlrate : BigDecimal.ZERO;
        this.customItemSlrdate = customItemSlrdate != null ? customItemSlrdate : "";
        this.customItemRemark = customItemRemark;
        this.customItemUse = customItemUse != null ? customItemUse : 1;
        this.customItemFdate = customItemFdate != null ? customItemFdate : LocalDateTime.now();
        this.customItemFuser = customItemFuser;
        this.customItemLdate = customItemLdate != null ? customItemLdate : LocalDateTime.now();
        this.customItemLuser = customItemLuser;
    }

    public boolean isActive() {
        return customItemUse == 1;
    }

    public boolean hasPrice() {
        return customItemSrate != null && customItemSrate.compareTo(BigDecimal.ZERO) > 0;
    }

    public void updatePrice(BigDecimal srate, String spec, String unit, Integer vdiv, String remark, String luser) {
        if (srate != null) {
            this.customItemSlrate = this.customItemSrate;
            this.customItemSrate = srate;
        }
        if (spec != null) this.customItemSpec = spec;
        if (unit != null) this.customItemUnit = unit;
        if (vdiv != null) this.customItemVdiv = vdiv;
        if (remark != null) this.customItemRemark = remark;
        this.customItemLdate = LocalDateTime.now();
        this.customItemLuser = luser;
    }

    public void updateLastOrderDate(String date) {
        this.customItemSlrdate = date;
    }

    public void deactivate(String luser) {
        this.customItemUse = 0;
        this.customItemLdate = LocalDateTime.now();
        this.customItemLuser = luser;
    }
}
