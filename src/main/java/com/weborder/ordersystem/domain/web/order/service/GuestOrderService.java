package com.weborder.ordersystem.domain.web.order.service;

import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.OrderMast;
import com.weborder.ordersystem.domain.erp.entity.OrderTran;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.OrderMastRepository;
import com.weborder.ordersystem.domain.erp.repository.OrderTranRepository;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.entity.MemberRole;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.order.dto.*;
import com.weborder.ordersystem.domain.web.order.entity.GuestOrder;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import com.weborder.ordersystem.domain.web.order.repository.GuestOrderRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import com.weborder.ordersystem.domain.web.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GuestOrderService {

    private final WebOrderMastRepository webOrderMastRepository;
    private final WebOrderTranRepository webOrderTranRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final OrderMastRepository erpOrderMastRepository;
    private final OrderTranRepository erpOrderTranRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final WebOrderService webOrderService;

    private final TransactionTemplate webTxTemplate;
    private final TransactionTemplate erpTxTemplate;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Integer DEFAULT_SOSOK = 1;
    private static final String DEFAULT_UJCD = "0013001000";
    private static final String STAU_ORDER_REGISTERED = "4010010001";
    private static final String GUEST_USER = "GUEST";
    private static final int MAX_ACNO_RETRY = 3;

    public GuestOrderService(WebOrderMastRepository webOrderMastRepository,
                              WebOrderTranRepository webOrderTranRepository,
                              GuestOrderRepository guestOrderRepository,
                              ItemCodeRepository itemCodeRepository,
                              OrderMastRepository erpOrderMastRepository,
                              OrderTranRepository erpOrderTranRepository,
                              MemberRepository memberRepository,
                              NotificationService notificationService,
                              WebOrderService webOrderService,
                              @Qualifier("webTransactionManager") PlatformTransactionManager webTxManager,
                              @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager) {
        this.webOrderMastRepository = webOrderMastRepository;
        this.webOrderTranRepository = webOrderTranRepository;
        this.guestOrderRepository = guestOrderRepository;
        this.itemCodeRepository = itemCodeRepository;
        this.erpOrderMastRepository = erpOrderMastRepository;
        this.erpOrderTranRepository = erpOrderTranRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
        this.webOrderService = webOrderService;
        this.webTxTemplate = new TransactionTemplate(webTxManager);
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
    }

    /**
     * 비회원 주문 생성
     */
    public OrderMastResponse createGuestOrder(GuestOrderCreateRequest request) {
        log.info("===== 비회원 주문 생성 시작 - 업체: {}, 담당자: {} =====",
                request.getCompanyName(), request.getManagerName());

        if (request.getCompanyName() == null || request.getCompanyName().isBlank())
            throw new IllegalArgumentException("업체명은 필수입니다.");
        if (request.getManagerName() == null || request.getManagerName().isBlank())
            throw new IllegalArgumentException("담당자명은 필수입니다.");
        if (request.getContact() == null || request.getContact().isBlank())
            throw new IllegalArgumentException("연락처는 필수입니다.");
        if (request.getItems() == null || request.getItems().isEmpty())
            throw new IllegalArgumentException("주문 항목이 없습니다.");

        String today = LocalDate.now().format(DATE_FMT);
        LocalDateTime now = LocalDateTime.now();
        Integer sosok = DEFAULT_SOSOK;
        String ujcd = DEFAULT_UJCD;

        String odate = request.getOdate() != null ? request.getOdate() : today;
        String remark = request.getRemark() != null ? request.getRemark() : "";

        // 주문 항목 준비
        List<WebOrderService.OrderItemData> itemDataList = new ArrayList<>();
        for (OrderItemRequest reqItem : request.getItems()) {
            ItemCode item = erpTxTemplate.execute(status ->
                    itemCodeRepository.findById(reqItem.getItemCode())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + reqItem.getItemCode())));
            BigDecimal rate = reqItem.getRate() != null ? reqItem.getRate() : item.getItemCodeSrate();
            String itemRemark = reqItem.getRemark() != null ? reqItem.getRemark() : "";
            itemDataList.add(new WebOrderService.OrderItemData(item, reqItem.getQuantity(), rate, itemRemark));
        }

        final String finalOdate = odate;
        final String finalRemark = remark;

        // ACNO 생성 + Web DB 저장 (충돌 시 재시도)
        WebOrderMast webMast = null;
        int finalAcno = 0;
        for (int attempt = 1; attempt <= MAX_ACNO_RETRY; attempt++) {
            Integer webMaxAcno = webTxTemplate.execute(status ->
                    webOrderMastRepository.findMaxAcno(today, sosok, ujcd));
            Integer erpMaxAcno = erpTxTemplate.execute(status ->
                    erpOrderMastRepository.findMaxAcnoByDateAndSosokAndUjcd(today, sosok, ujcd));
            finalAcno = Math.max(
                    webMaxAcno != null ? webMaxAcno : 0,
                    erpMaxAcno != null ? erpMaxAcno : 0
            ) + 1;
            log.info("ACNO 생성 - webMax: {}, erpMax: {}, newAcno: {} (시도 {}/{})",
                    webMaxAcno, erpMaxAcno, finalAcno, attempt, MAX_ACNO_RETRY);

            final int acnoForTx = finalAcno;
            try {
                webMast = webTxTemplate.execute(status -> {
                    WebOrderMast mast = WebOrderMast.builder()
                            .orderMastDate(today)
                            .orderMastSosok(sosok)
                            .orderMastUjcd(ujcd)
                            .orderMastAcno(acnoForTx)
                            .orderMastCust(0)
                            .orderMastSawon(0)
                            .orderMastOdate(finalOdate)
                            .orderMastProject(0)
                            .orderMastRemark(finalRemark)
                            .orderMastFdate(now)
                            .orderMastFuser(GUEST_USER)
                            .orderMastLdate(now)
                            .orderMastLuser(GUEST_USER)
                            .webMemberId(null)
                            .webOrderStatus("ORDERED")
                            .build();

                    webOrderMastRepository.save(mast);
                    log.info("[Web DB] 비회원 주문 마스터 저장 - key: {}", mast.getOrderKey());

                    List<WebOrderTran> webTrans = new ArrayList<>();
                    int seq = 1;
                    for (WebOrderService.OrderItemData data : itemDataList) {
                        webTrans.add(webOrderService.buildWebOrderTran(mast, seq++, data, GUEST_USER, now));
                    }
                    webOrderTranRepository.saveAll(webTrans);
                    log.info("[Web DB] 비회원 주문 상세 {} 건 저장", webTrans.size());

                    GuestOrder guestOrder = GuestOrder.builder()
                            .orderKey(mast.getOrderKey())
                            .companyName(request.getCompanyName().trim())
                            .managerName(request.getManagerName().trim())
                            .contact(request.getContact().trim())
                            .address(request.getAddress() != null ? request.getAddress().trim() : null)
                            .createdAt(now)
                            .build();
                    guestOrderRepository.save(guestOrder);
                    log.info("[Web DB] 비회원 정보 저장 - orderKey: {}", mast.getOrderKey());

                    return mast;
                });
                break;
            } catch (DataIntegrityViolationException e) {
                if (attempt == MAX_ACNO_RETRY) {
                    log.error("ACNO 충돌 {}회 재시도 후에도 실패", MAX_ACNO_RETRY);
                    throw e;
                }
                log.warn("ACNO 충돌 감지 (acno={}), 재시도 {}/{}", acnoForTx, attempt, MAX_ACNO_RETRY);
            }
        }

        // ERP DB 저장
        final int acnoForErp = finalAcno;
        final WebOrderMast webMastForRollback = webMast;
        try {
            erpTxTemplate.executeWithoutResult(status -> {
                OrderMast erpMast = OrderMast.builder()
                        .orderMastDate(today)
                        .orderMastSosok(sosok)
                        .orderMastUjcd(ujcd)
                        .orderMastAcno(acnoForErp)
                        .orderMastCust(0)
                        .orderMastSawon(0)
                        .orderMastOdate(finalOdate)
                        .orderMastProject(0)
                        .orderMastRemark(finalRemark)
                        .orderMastFdate(now)
                        .orderMastFuser(GUEST_USER)
                        .orderMastLdate(now)
                        .orderMastLuser(GUEST_USER)
                        .build();

                erpOrderMastRepository.save(erpMast);
                log.info("[ERP DB] 비회원 주문 마스터 저장");

                List<OrderTran> erpTrans = new ArrayList<>();
                int seq = 1;
                for (WebOrderService.OrderItemData data : itemDataList) {
                    erpTrans.add(webOrderService.buildErpOrderTran(today, sosok, ujcd, acnoForErp,
                            seq++, data, GUEST_USER, now));
                }
                erpOrderTranRepository.saveAll(erpTrans);
                log.info("[ERP DB] 비회원 주문 상세 {} 건 저장", erpTrans.size());
            });
        } catch (Exception e) {
            log.error("[ERP DB] 비회원 주문 저장 실패! Web DB 롤백 시작", e);
            try {
                webTxTemplate.executeWithoutResult(status -> {
                    List<WebOrderTran> webTrans = webOrderTranRepository
                            .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                    today, sosok, ujcd, acnoForErp);
                    webOrderTranRepository.deleteAll(webTrans);
                    guestOrderRepository.findByOrderKey(webMastForRollback.getOrderKey())
                            .ifPresent(guestOrderRepository::delete);
                    WebOrderMast.WebOrderMastId mastId = new WebOrderMast.WebOrderMastId(today, sosok, ujcd, acnoForErp);
                    webOrderMastRepository.deleteById(mastId);
                    log.info("[Web DB] 비회원 주문 보상 롤백 완료");
                });
            } catch (Exception rollbackEx) {
                log.error("[Web DB] 보상 롤백 실패! ERP_SYNC_FAILED 마킹 시도", rollbackEx);
                try {
                    webTxTemplate.executeWithoutResult(s -> {
                        WebOrderMast.WebOrderMastId mid = new WebOrderMast.WebOrderMastId(
                                today, sosok, ujcd, acnoForErp);
                        webOrderMastRepository.findById(mid).ifPresent(m -> {
                            m.updateStatus("ERP_SYNC_FAILED");
                            webOrderMastRepository.save(m);
                        });
                    });
                    log.warn("[Web DB] ERP_SYNC_FAILED 마킹 완료 - acno: {}", acnoForErp);
                } catch (Exception markEx) {
                    log.error("[Web DB] ERP_SYNC_FAILED 마킹도 실패! 수동 확인 필수 - acno: {}",
                            acnoForErp, markEx);
                }
            }
            throw new RuntimeException("ERP DB 저장에 실패했습니다. 주문이 취소되었습니다.", e);
        }

        log.info("===== 비회원 주문 생성 완료 - key: {} =====", webMast.getOrderKey());

        // 관리자 알림
        try {
            String orderKey = webMast.getOrderKey();
            int itemCount = itemDataList.size();
            String notiMessage = "[비회원] " + request.getCompanyName() + " 발주 " + itemCount + "건이 접수되었습니다.";
            List<Member> admins = memberRepository.findByRoleAndUseYnTrue(MemberRole.ADMIN);
            for (Member admin : admins) {
                notificationService.send(admin.getId(), "ORDER_CREATED", "비회원 발주", notiMessage, orderKey);
            }
        } catch (Exception e) {
            log.warn("비회원 주문 알림 전송 실패 (주문은 정상)", e);
        }

        // 응답 빌드
        List<WebOrderTran> savedTrans = webOrderTranRepository
                .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                        today, sosok, ujcd, acnoForErp);
        List<OrderTranResponse> tranResponses = savedTrans.stream()
                .map(OrderTranResponse::from)
                .collect(Collectors.toList());

        return OrderMastResponse.builder()
                .orderDate(webMast.getOrderMastDate())
                .sosok(webMast.getOrderMastSosok())
                .ujcd(webMast.getOrderMastUjcd())
                .acno(webMast.getOrderMastAcno())
                .cust(webMast.getOrderMastCust())
                .odate(webMast.getOrderMastOdate())
                .remark(webMast.getOrderMastRemark())
                .fdate(webMast.getOrderMastFdate() != null
                        ? webMast.getOrderMastFdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
                .webOrderStatus(webMast.getWebOrderStatus())
                .orderKey(webMast.getOrderKey())
                .items(tranResponses)
                .itemCount(tranResponses.size())
                .totalAmount(tranResponses.stream()
                        .map(OrderTranResponse::getTot)
                        .filter(t -> t != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .memberName(request.getManagerName())
                .custCodeName(request.getCompanyName())
                .guestCompanyName(request.getCompanyName())
                .guestManagerName(request.getManagerName())
                .guestContact(request.getContact())
                .guestAddress(request.getAddress())
                .build();
    }

    /**
     * 비회원 주문 조회 - 주문번호
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public OrderMastResponse getGuestOrderByKey(String orderKey) {
        String[] parts = orderKey.split("-");
        if (parts.length < 4) throw new IllegalArgumentException("잘못된 주문번호 형식입니다.");

        String date = parts[0];
        int sosok = Integer.parseInt(parts[1]);
        String ujcd = parts[2];
        int acno = Integer.parseInt(parts[3]);

        WebOrderMast mast = webOrderMastRepository
                .findById(new WebOrderMast.WebOrderMastId(date, sosok, ujcd, acno))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (mast.getOrderMastCust() != null && mast.getOrderMastCust() > 0) {
            throw new IllegalArgumentException("비회원 주문이 아닙니다.");
        }

        return buildGuestOrderResponse(mast);
    }

    /**
     * 비회원 주문 조회 - 업체명 + 담당자 + 연락처
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderMastResponse> lookupGuestOrders(String companyName, String managerName, String contact) {
        List<GuestOrder> guestOrders = guestOrderRepository
                .findByCompanyInfo(companyName, managerName, contact);

        return guestOrders.stream()
                .map(g -> {
                    try {
                        return getGuestOrderByKey(g.getOrderKey());
                    } catch (Exception e) {
                        log.warn("비회원 주문 조회 실패 - orderKey: {}", g.getOrderKey(), e);
                        return null;
                    }
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    /**
     * 비회원 확인서명
     */
    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse submitGuestCustSign(String orderKey, String signData) {
        if (signData == null || signData.isBlank()) {
            throw new IllegalArgumentException("서명 데이터가 필요합니다");
        }

        String[] parts = orderKey.split("-");
        if (parts.length < 4) throw new IllegalArgumentException("잘못된 주문번호 형식입니다.");

        String date = parts[0];
        int sosok = Integer.parseInt(parts[1]);
        String ujcd = parts[2];
        int acno = Integer.parseInt(parts[3]);

        WebOrderMast mast = webOrderMastRepository
                .findById(new WebOrderMast.WebOrderMastId(date, sosok, ujcd, acno))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (!"DELIVERED".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("배송완료 상태에서만 확인서명이 가능합니다");
        }
        if (mast.getOrderMastCust() != null && mast.getOrderMastCust() > 0) {
            throw new IllegalArgumentException("비회원 주문이 아닙니다");
        }
        if (mast.getWebCustSign() != null && !mast.getWebCustSign().isBlank()) {
            throw new IllegalStateException("이미 확인서명이 완료되었습니다");
        }

        mast.submitCustSign(signData, null);
        webOrderMastRepository.save(mast);
        log.info("비회원 확인서명 완료 - orderKey: {}", orderKey);

        return buildGuestOrderResponse(mast);
    }

    private OrderMastResponse buildGuestOrderResponse(WebOrderMast mast) {
        List<WebOrderTran> trans = webOrderTranRepository
                .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                        mast.getOrderMastDate(), mast.getOrderMastSosok(),
                        mast.getOrderMastUjcd(), mast.getOrderMastAcno());

        List<OrderTranResponse> tranResponses = trans.stream()
                .map(OrderTranResponse::from)
                .collect(Collectors.toList());

        GuestOrder guest = guestOrderRepository.findByOrderKey(mast.getOrderKey()).orElse(null);

        String driverName = null;
        String driverMemberId = null;
        if (mast.getWebDriverId() != null) {
            Member driver = memberRepository.findById(mast.getWebDriverId()).orElse(null);
            if (driver != null) {
                driverName = driver.getMemberName();
                driverMemberId = driver.getMemberId();
            }
        }

        return OrderMastResponse.builder()
                .orderDate(mast.getOrderMastDate())
                .sosok(mast.getOrderMastSosok())
                .ujcd(mast.getOrderMastUjcd())
                .acno(mast.getOrderMastAcno())
                .cust(mast.getOrderMastCust())
                .odate(mast.getOrderMastOdate())
                .remark(mast.getOrderMastRemark())
                .fdate(mast.getOrderMastFdate() != null
                        ? mast.getOrderMastFdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
                .webOrderStatus(mast.getWebOrderStatus())
                .orderKey(mast.getOrderKey())
                .items(tranResponses)
                .itemCount(tranResponses.size())
                .totalAmount(tranResponses.stream()
                        .map(OrderTranResponse::getTot)
                        .filter(t -> t != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .webDriverId(mast.getWebDriverId())
                .driverName(driverName)
                .driverMemberId(driverMemberId)
                .confirmedAt(mast.getWebConfirmedAt() != null
                        ? mast.getWebConfirmedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .driverSign(mast.getWebDriverSign())
                .driverSignAt(mast.getWebDriverSignAt() != null
                        ? mast.getWebDriverSignAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .driverSignName(mast.getWebDriverSignMemberId() != null
                        ? memberRepository.findById(mast.getWebDriverSignMemberId())
                                .map(Member::getMemberName).orElse(null) : null)
                .custSign(mast.getWebCustSign())
                .custSignAt(mast.getWebCustSignAt() != null
                        ? mast.getWebCustSignAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .custSignName(mast.getWebCustSignMemberId() != null
                        ? memberRepository.findById(mast.getWebCustSignMemberId())
                                .map(Member::getMemberName).orElse(null) : null)
                .memberName(guest != null ? guest.getCompanyName() : null)
                .custCodeName(guest != null ? guest.getCompanyName() : null)
                .guestCompanyName(guest != null ? guest.getCompanyName() : null)
                .guestManagerName(guest != null ? guest.getManagerName() : null)
                .guestContact(guest != null ? guest.getContact() : null)
                .guestAddress(guest != null ? guest.getAddress() : null)
                .build();
    }
}
