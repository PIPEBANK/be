package com.weborder.ordersystem.domain.web.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sa_order_mast")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(WebOrderMast.WebOrderMastId.class)
public class WebOrderMast {

    @Id
    @Column(name = "ORDER_MAST_DATE", columnDefinition = "char(8)", nullable = false)
    private String orderMastDate; //주문일자

    @Id
    @Column(name = "ORDER_MAST_SOSOK", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastSosok; //소속

    @Id
    @Column(name = "ORDER_MAST_UJCD", columnDefinition = "char(10)", nullable = false)
    private String orderMastUjcd; //업장

    @Id
    @Column(name = "ORDER_MAST_ACNO", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastAcno; //전표번호

    @Column(name = "ORDER_MAST_CUST", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastCust; //거래처

    @Column(name = "ORDER_MAST_SAWON", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastSawon; //담당자

    @Column(name = "ORDER_MAST_ODATE", columnDefinition = "varchar(100)", nullable = false)
    private String orderMastOdate; //납기일자

    @Column(name = "ORDER_MAST_PROJECT", columnDefinition = "int(11)", nullable = false)
    private Integer orderMastProject; //프로젝트

    @Column(name = "ORDER_MAST_REMARK", nullable = false, length = 200)
    private String orderMastRemark; //비고

    @Column(name = "ORDER_MAST_FDATE", nullable = false)
    private LocalDateTime orderMastFdate; //최초등록일

    @Column(name = "ORDER_MAST_FUSER", nullable = false, length = 20)
    private String orderMastFuser; //최초등록자

    @Column(name = "ORDER_MAST_LDATE", nullable = false)
    private LocalDateTime orderMastLdate; //최종수정일

    @Column(name = "ORDER_MAST_LUSER", nullable = false, length = 20)
    private String orderMastLuser; //최종수정자

    // 웹 전용 필드
    @Column(name = "WEB_MEMBER_ID")
    private Long webMemberId; //웹 주문자 (TBL_MEMBER PK)

    @Column(name = "WEB_ORDER_STATUS", length = 20)
    private String webOrderStatus; //웹 주문상태 (ORDERED, CONFIRMED, SHIPPING, DELIVERED)

    @Column(name = "WEB_DRIVER_ID")
    private Long webDriverId; //배송기사 (TBL_MEMBER PK)

    @Column(name = "WEB_CONFIRMED_AT")
    private LocalDateTime webConfirmedAt; //접수완료(기사배정) 시각

    @Column(name = "WEB_DRIVER_SIGN", columnDefinition = "MEDIUMTEXT")
    private String webDriverSign; //배송완료 서명 (Base64)

    @Column(name = "WEB_DRIVER_SIGN_AT")
    private LocalDateTime webDriverSignAt; //배송완료 서명 시각

    @Column(name = "WEB_DRIVER_SIGN_MEMBER_ID")
    private Long webDriverSignMemberId; //배송완료 서명자 회원 ID

    @Column(name = "WEB_CUST_SIGN", columnDefinition = "MEDIUMTEXT")
    private String webCustSign; //거래처 확인 서명 (Base64)

    @Column(name = "WEB_CUST_SIGN_AT")
    private LocalDateTime webCustSignAt; //거래처 확인 서명 시각

    @Column(name = "WEB_CUST_SIGN_MEMBER_ID")
    private Long webCustSignMemberId; //거래처 확인 서명자 회원 ID

    @Builder
    public WebOrderMast(String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno,
                       Integer orderMastCust, Integer orderMastSawon,
                       String orderMastOdate, Integer orderMastProject, String orderMastRemark,
                       LocalDateTime orderMastFdate, String orderMastFuser, LocalDateTime orderMastLdate, String orderMastLuser,
                       Long webMemberId, String webOrderStatus, Long webDriverId, LocalDateTime webConfirmedAt,
                       String webDriverSign, LocalDateTime webDriverSignAt, Long webDriverSignMemberId,
                       String webCustSign, LocalDateTime webCustSignAt, Long webCustSignMemberId) {
        this.orderMastDate = orderMastDate;
        this.orderMastSosok = orderMastSosok;
        this.orderMastUjcd = orderMastUjcd;
        this.orderMastAcno = orderMastAcno;
        this.orderMastCust = orderMastCust;
        this.orderMastSawon = orderMastSawon;
        this.orderMastOdate = orderMastOdate;
        this.orderMastProject = orderMastProject;
        this.orderMastRemark = orderMastRemark;
        this.orderMastFdate = orderMastFdate;
        this.orderMastFuser = orderMastFuser;
        this.orderMastLdate = orderMastLdate;
        this.orderMastLuser = orderMastLuser;
        this.webMemberId = webMemberId;
        this.webOrderStatus = webOrderStatus;
        this.webDriverId = webDriverId;
        this.webConfirmedAt = webConfirmedAt;
        this.webDriverSign = webDriverSign;
        this.webDriverSignAt = webDriverSignAt;
        this.webDriverSignMemberId = webDriverSignMemberId;
        this.webCustSign = webCustSign;
        this.webCustSignAt = webCustSignAt;
        this.webCustSignMemberId = webCustSignMemberId;
    }

    public void assignDriver(Long driverId, String luser) {
        this.webDriverId = driverId;
        this.webOrderStatus = "CONFIRMED";
        this.webConfirmedAt = LocalDateTime.now();
        this.orderMastLdate = LocalDateTime.now();
        this.orderMastLuser = luser;
    }

    public void completeWithSign(String sign, String luser, Long signMemberId) {
        this.webOrderStatus = "DELIVERED";
        this.webDriverSign = sign;
        this.webDriverSignAt = LocalDateTime.now();
        this.webDriverSignMemberId = signMemberId;
        this.orderMastLdate = LocalDateTime.now();
        this.orderMastLuser = luser;
    }

    public void submitCustSign(String sign, Long signMemberId) {
        this.webCustSign = sign;
        this.webCustSignAt = LocalDateTime.now();
        this.webCustSignMemberId = signMemberId;
        this.orderMastLdate = LocalDateTime.now();
    }

    public void updateStatus(String status, String luser) {
        this.webOrderStatus = status;
        this.orderMastLdate = LocalDateTime.now();
        this.orderMastLuser = luser;
    }

    public void updateStatus(String status) {
        this.webOrderStatus = status;
        this.orderMastLdate = LocalDateTime.now();
    }

    public String getOrderKey() {
        return orderMastDate + "-" + orderMastSosok + "-" + orderMastUjcd + "-" + orderMastAcno;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class WebOrderMastId implements Serializable {
        private String orderMastDate;
        private Integer orderMastSosok;
        private String orderMastUjcd;
        private Integer orderMastAcno;

        public WebOrderMastId(String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno) {
            this.orderMastDate = orderMastDate;
            this.orderMastSosok = orderMastSosok;
            this.orderMastUjcd = orderMastUjcd;
            this.orderMastAcno = orderMastAcno;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WebOrderMastId that = (WebOrderMastId) o;
            if (!orderMastDate.equals(that.orderMastDate)) return false;
            if (!orderMastSosok.equals(that.orderMastSosok)) return false;
            if (!orderMastUjcd.equals(that.orderMastUjcd)) return false;
            return orderMastAcno.equals(that.orderMastAcno);
        }

        @Override
        public int hashCode() {
            int result = orderMastDate.hashCode();
            result = 31 * result + orderMastSosok.hashCode();
            result = 31 * result + orderMastUjcd.hashCode();
            result = 31 * result + orderMastAcno.hashCode();
            return result;
        }
    }
}
