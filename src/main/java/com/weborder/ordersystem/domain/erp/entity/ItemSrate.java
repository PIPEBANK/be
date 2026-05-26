package com.weborder.ordersystem.domain.erp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "co_item_srate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSrate {

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ItemSrateId implements Serializable {
        @Column(name = "ITEM_SRATE_ITEM", columnDefinition = "int(10)")
        private Integer itemSrateItem;

        @Column(name = "ITEM_SRATE_CUST", columnDefinition = "int(10)")
        private Integer itemSrateCust;
    }

    @EmbeddedId
    private ItemSrateId id;

    @Column(name = "ITEM_SRATE_RATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemSrateRate;

    @Column(name = "ITEM_SRATE_PRATE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemSratePrate;

    @Column(name = "ITEM_SRATE_PRICE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemSratePrice;

    @Column(name = "ITEM_SRATE_PPRICE", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemSratePprice;

    @Column(name = "ITEM_SRATE_DATE", nullable = false, columnDefinition = "char(8)")
    private String itemSrateDate;

    @Column(name = "ITEM_SRATE_CNT", nullable = false, precision = 18, scale = 0)
    private BigDecimal itemSrateCnt;

    @Column(name = "ITEM_SRATE_PCNT", nullable = false, precision = 18, scale = 0)
    private BigDecimal itemSratePcnt;

    @Column(name = "ITEM_SRATE_REMARK", nullable = false, length = 200)
    private String itemSrateRemark;

    @Column(name = "ITEM_SRATE_FDATE", nullable = false)
    private LocalDateTime itemSrateFdate;

    @Column(name = "ITEM_SRATE_FUSER", nullable = false, length = 50)
    private String itemSrateFuser;

    @Column(name = "ITEM_SRATE_LDATE", nullable = false)
    private LocalDateTime itemSrateLdate;

    @Column(name = "ITEM_SRATE_LUSER", nullable = false, length = 50)
    private String itemSrateLuser;

    @Builder
    public ItemSrate(Integer itemSrateItem, Integer itemSrateCust,
                     BigDecimal itemSrateRate, BigDecimal itemSratePrate,
                     BigDecimal itemSratePrice, BigDecimal itemSratePprice,
                     String itemSrateDate, BigDecimal itemSrateCnt, BigDecimal itemSratePcnt,
                     String itemSrateRemark,
                     LocalDateTime itemSrateFdate, String itemSrateFuser,
                     LocalDateTime itemSrateLdate, String itemSrateLuser) {
        this.id = new ItemSrateId(itemSrateItem, itemSrateCust);
        this.itemSrateRate = itemSrateRate;
        this.itemSratePrate = itemSratePrate;
        this.itemSratePrice = itemSratePrice;
        this.itemSratePprice = itemSratePprice;
        this.itemSrateDate = itemSrateDate;
        this.itemSrateCnt = itemSrateCnt;
        this.itemSratePcnt = itemSratePcnt;
        this.itemSrateRemark = itemSrateRemark;
        this.itemSrateFdate = itemSrateFdate;
        this.itemSrateFuser = itemSrateFuser;
        this.itemSrateLdate = itemSrateLdate;
        this.itemSrateLuser = itemSrateLuser;
    }

    public Integer getItemCode() {
        return id.getItemSrateItem();
    }

    public Integer getCustCode() {
        return id.getItemSrateCust();
    }

    public void updateRate(BigDecimal rate, String remark, String user) {
        this.itemSrateRate = rate;
        if (remark != null) this.itemSrateRemark = remark;
        this.itemSrateLdate = LocalDateTime.now();
        this.itemSrateLuser = user;
    }
}
