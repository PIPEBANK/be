package com.weborder.ordersystem.domain.web.order.service;

import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import com.weborder.ordersystem.domain.erp.entity.OrderMast;
import com.weborder.ordersystem.domain.erp.entity.OrderTran;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemSrateRepository;
import com.weborder.ordersystem.domain.erp.repository.OrderMastRepository;
import com.weborder.ordersystem.domain.erp.repository.OrderTranRepository;
import com.weborder.ordersystem.domain.web.cart.entity.CartItem;
import com.weborder.ordersystem.domain.web.cart.repository.CartItemRepository;
import com.weborder.ordersystem.domain.web.customitem.entity.CustomItem;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemRepository;
import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import com.weborder.ordersystem.domain.web.image.service.ItemImageService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.member.entity.MemberRole;
import com.weborder.ordersystem.domain.web.notification.service.NotificationService;
import com.weborder.ordersystem.domain.web.order.dto.*;
import com.weborder.ordersystem.domain.web.order.entity.GuestOrder;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import com.weborder.ordersystem.domain.web.order.repository.GuestOrderRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebOrderService {

    private final WebOrderMastRepository webOrderMastRepository;
    private final WebOrderTranRepository webOrderTranRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final ItemSrateRepository itemSrateRepository;
    private final OrderMastRepository erpOrderMastRepository;
    private final OrderTranRepository erpOrderTranRepository;
    private final com.weborder.ordersystem.domain.erp.repository.CommonCode3Repository commonCode3Repository;
    private final com.weborder.ordersystem.domain.erp.repository.CustomerRepository customerRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final NotificationService notificationService;
    private final CustomItemRepository customItemRepository;
    private final ItemImageService itemImageService;

    private final TransactionTemplate webTxTemplate;
    private final TransactionTemplate erpTxTemplate;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // ERP 기본값 상수
    private static final Integer DEFAULT_SOSOK = 1;          // 회계단위 (스테이큐)
    private static final String DEFAULT_UJCD = "0013001000"; // 업장코드 (구매)

    // 수주 상태코드 (sa_order_tran.STAU) - 401 그룹
    private static final String STAU_ORDER_REGISTERED = "4010010001"; // 수주등록
    private static final String STAU_ORDER_PROGRESS   = "4010020001"; // 수주진행
    private static final String STAU_ORDER_SHIPPED    = "4010030001"; // 출하완료
    private static final String STAU_ORDER_CLOSED     = "4010030002"; // 강제종료

    // 출하 상태코드 (sa_ship_tran 등) - 538 그룹
    private static final String STAU_SHIP_REGISTERED  = "5380010001"; // 출하등록
    private static final String STAU_SHIP_COMPLETED   = "5380010002"; // 출하완료
    private static final String STAU_SHIP_PROGRESS    = "5380020001"; // 출하진행
    private static final String STAU_SHIP_CONFIRMED   = "5380030001"; // 매출확정
    private static final String STAU_SHIP_CLOSED      = "5380030002"; // 종결

    private static final int MAX_ACNO_RETRY = 3;

    public WebOrderService(
            WebOrderMastRepository webOrderMastRepository,
            WebOrderTranRepository webOrderTranRepository,
            CartItemRepository cartItemRepository,
            MemberRepository memberRepository,
            ItemCodeRepository itemCodeRepository,
            ItemSrateRepository itemSrateRepository,
            OrderMastRepository erpOrderMastRepository,
            OrderTranRepository erpOrderTranRepository,
            com.weborder.ordersystem.domain.erp.repository.CommonCode3Repository commonCode3Repository,
            com.weborder.ordersystem.domain.erp.repository.CustomerRepository customerRepository,
            GuestOrderRepository guestOrderRepository,
            NotificationService notificationService,
            CustomItemRepository customItemRepository,
            ItemImageService itemImageService,
            @Qualifier("webTransactionManager") PlatformTransactionManager webTxManager,
            @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager) {
        this.webOrderMastRepository = webOrderMastRepository;
        this.webOrderTranRepository = webOrderTranRepository;
        this.cartItemRepository = cartItemRepository;
        this.memberRepository = memberRepository;
        this.itemCodeRepository = itemCodeRepository;
        this.itemSrateRepository = itemSrateRepository;
        this.erpOrderMastRepository = erpOrderMastRepository;
        this.erpOrderTranRepository = erpOrderTranRepository;
        this.commonCode3Repository = commonCode3Repository;
        this.customerRepository = customerRepository;
        this.guestOrderRepository = guestOrderRepository;
        this.notificationService = notificationService;
        this.customItemRepository = customItemRepository;
        this.itemImageService = itemImageService;
        this.webTxTemplate = new TransactionTemplate(webTxManager);
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
    }

    /**
     * 주문 생성 (장바구니 or 바로주문) - Web DB + ERP DB 동시저장
     *
     * 트랜잭션 전략:
     * 1. Web DB 저장 (webTxTemplate)
     * 2. ERP DB 저장 (erpTxTemplate)
     * 3. 둘 중 하나라도 실패하면 전체 롤백
     */
    public OrderMastResponse createOrder(String loginId, OrderCreateRequest request) {
        log.info("===== 주문 생성 시작 =====");
        log.info("loginId: {}, fromCart: {}", loginId, request.isFromCart());

        // ---- 1단계: 공통 데이터 준비 (트랜잭션 외부) ----
        Member member = webTxTemplate.execute(status -> {
            return memberRepository.findByMemberId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + loginId));
        });

        String today = LocalDate.now().format(DATE_FMT);
        LocalDateTime now = LocalDateTime.now();

        Integer sosok = request.getSosok() != null ? request.getSosok() : DEFAULT_SOSOK;
        String ujcd = request.getUjcd() != null ? request.getUjcd() : DEFAULT_UJCD;

        Integer custCode;
        try {
            custCode = Integer.parseInt(member.getCustCode());
        } catch (NumberFormatException e) {
            custCode = 0;
        }
        Integer cust = request.getCust() != null ? request.getCust() : custCode;
        String odate = request.getOdate() != null ? request.getOdate() : today;
        String remark = request.getRemark() != null ? request.getRemark() : "";

        // ---- 2단계: 주문 항목 준비 (ERP 품목정보 조회) ----
        final Integer finalSosok = sosok;
        final String finalUjcd = ujcd;
        final Integer finalCust = cust;
        final String finalOdate = odate;
        final String finalRemark = remark;

        List<OrderItemData> itemDataList = prepareOrderItems(request, member, loginId, now);

        // ---- 3단계: ACNO 생성 + Web DB 저장 (충돌 시 재시도) ----
        WebOrderMast webMast = null;
        int finalAcno = 0;
        for (int attempt = 1; attempt <= MAX_ACNO_RETRY; attempt++) {
            Integer webMaxAcno = webTxTemplate.execute(status ->
                    webOrderMastRepository.findMaxAcno(today, finalSosok, finalUjcd));
            Integer erpMaxAcno = erpTxTemplate.execute(status ->
                    erpOrderMastRepository.findMaxAcnoByDateAndSosokAndUjcd(today, finalSosok, finalUjcd));
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
                            .orderMastSosok(finalSosok)
                            .orderMastUjcd(finalUjcd)
                            .orderMastAcno(acnoForTx)
                            .orderMastCust(finalCust)
                            .orderMastSawon(0)
                            .orderMastOdate(finalOdate)
                            .orderMastProject(0)
                            .orderMastRemark(finalRemark)
                            .orderMastFdate(now)
                            .orderMastFuser(loginId)
                            .orderMastLdate(now)
                            .orderMastLuser(loginId)
                            .webMemberId(member.getId())
                            .webOrderStatus("ORDERED")
                            .build();

                    webOrderMastRepository.save(mast);
                    log.info("[Web DB] 주문 마스터 저장 - key: {}", mast.getOrderKey());

                    List<WebOrderTran> webTrans = new ArrayList<>();
                    int seq = 1;
                    for (OrderItemData data : itemDataList) {
                        webTrans.add(buildWebOrderTran(mast, seq++, data, loginId, now));
                    }
                    webOrderTranRepository.saveAll(webTrans);
                    log.info("[Web DB] 주문 상세 {} 건 저장", webTrans.size());

                    if (request.isFromCart() && request.getCartItemIds() != null) {
                        for (Long cartItemId : request.getCartItemIds()) {
                            cartItemRepository.findById(cartItemId).ifPresent(cartItemRepository::delete);
                        }
                        log.info("[Web DB] 장바구니 {} 건 삭제", request.getCartItemIds().size());
                    }

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

        // ---- 4단계: ERP DB 저장 ----
        final int acnoForErp = finalAcno;
        try {
            erpTxTemplate.executeWithoutResult(status -> {
                OrderMast erpMast = OrderMast.builder()
                        .orderMastDate(today)
                        .orderMastSosok(finalSosok)
                        .orderMastUjcd(finalUjcd)
                        .orderMastAcno(acnoForErp)
                        .orderMastCust(finalCust)
                        .orderMastSawon(0)
                        .orderMastOdate(finalOdate)
                        .orderMastProject(0)
                        .orderMastRemark(finalRemark)
                        .orderMastFdate(now)
                        .orderMastFuser(loginId)
                        .orderMastLdate(now)
                        .orderMastLuser(loginId)
                        .build();

                erpOrderMastRepository.save(erpMast);
                log.info("[ERP DB] 주문 마스터 저장 - key: {}", erpMast.getOrderKey());

                List<OrderTran> erpTrans = new ArrayList<>();
                int seq = 1;
                for (OrderItemData data : itemDataList) {
                    erpTrans.add(buildErpOrderTran(today, finalSosok, finalUjcd, acnoForErp,
                            seq++, data, loginId, now));
                }
                erpOrderTranRepository.saveAll(erpTrans);
                log.info("[ERP DB] 주문 상세 {} 건 저장", erpTrans.size());
            });
        } catch (Exception e) {
            // ERP 저장 실패 → Web DB 데이터도 롤백 (보상 트랜잭션)
            log.error("[ERP DB] 저장 실패! Web DB 데이터 롤백 시작", e);
            try {
                webTxTemplate.executeWithoutResult(status -> {
                    // Web DB에서 방금 저장한 데이터 삭제
                    List<WebOrderTran> webTrans = webOrderTranRepository
                            .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                    today, finalSosok, finalUjcd, acnoForErp);
                    webOrderTranRepository.deleteAll(webTrans);

                    WebOrderMast.WebOrderMastId mastId = new WebOrderMast.WebOrderMastId(
                            today, finalSosok, finalUjcd, acnoForErp);
                    webOrderMastRepository.deleteById(mastId);
                    log.info("[Web DB] 보상 롤백 완료 - ACNO: {}", acnoForErp);
                });
            } catch (Exception rollbackEx) {
                log.error("[Web DB] 보상 롤백도 실패! ERP_SYNC_FAILED 마킹 시도 - date: {}, acno: {}",
                        today, acnoForErp, rollbackEx);
                try {
                    webTxTemplate.executeWithoutResult(s -> {
                        WebOrderMast.WebOrderMastId mid = new WebOrderMast.WebOrderMastId(
                                today, finalSosok, finalUjcd, acnoForErp);
                        webOrderMastRepository.findById(mid).ifPresent(m -> {
                            m.updateStatus("ERP_SYNC_FAILED");
                            webOrderMastRepository.save(m);
                        });
                    });
                    log.warn("[Web DB] ERP_SYNC_FAILED 마킹 완료 - date: {}, acno: {}", today, acnoForErp);
                } catch (Exception markEx) {
                    log.error("[Web DB] ERP_SYNC_FAILED 마킹도 실패! 수동 확인 필수 - date: {}, acno: {}",
                            today, acnoForErp, markEx);
                }
            }
            throw new RuntimeException("ERP DB 저장에 실패했습니다. 주문이 취소되었습니다.", e);
        }

        log.info("===== 주문 생성 완료 (Web + ERP) - ACNO: {} =====", acnoForErp);

        // 알림: 활성 관리자 전원에게 신규 발주 알림
        try {
            String orderKey = webMast.getOrderKey();
            int itemCount = itemDataList.size();

            String custName = null;
            String memberCustCode = member.getCustCode();
            if (memberCustCode != null && !memberCustCode.isBlank()) {
                try {
                    custName = erpTxTemplate.execute(status ->
                            customerRepository.findById(Integer.parseInt(memberCustCode.trim()))
                                    .map(c -> c.getCustCodeName())
                                    .orElse(null));
                } catch (Exception ignored) {}
            }
            if (custName == null || custName.isBlank()) {
                custName = member.getMemberName() != null ? member.getMemberName() : loginId;
            }

            String notiMessage = custName + " 발주 " + itemCount + "건이 접수되었습니다.";

            List<Member> admins = memberRepository.findByRoleAndUseYnTrue(MemberRole.ADMIN);
            for (Member admin : admins) {
                notificationService.send(admin.getId(), "ORDER_CREATED",
                        "신규 발주", notiMessage, orderKey);
            }
        } catch (Exception e) {
            log.warn("주문 생성 알림 발송 실패 (주문은 정상 처리됨)", e);
        }

        // 응답 생성
        List<WebOrderTran> savedTrans = webTxTemplate.execute(status ->
                webOrderTranRepository
                        .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                today, finalSosok, finalUjcd, acnoForErp));

        List<OrderTranResponse> tranResponses = savedTrans.stream()
                .map(OrderTranResponse::from)
                .collect(Collectors.toList());

        return OrderMastResponse.from(webMast, tranResponses);
    }

    /**
     * 주문 항목 데이터 준비 (ERP 품목 정보 조회)
     */
    private List<OrderItemData> prepareOrderItems(OrderCreateRequest request, Member member,
                                                   String loginId, LocalDateTime now) {
        List<OrderItemData> dataList = new ArrayList<>();

        // 회원의 custCode로 거래처별 단가 조회
        Integer custCode = parseCustCode(member.getCustCode());
        java.util.Map<Integer, BigDecimal> custRateMap = java.util.Map.of();
        if (custCode != null && custCode > 0) {
            final Integer cc = custCode;
            custRateMap = erpTxTemplate.execute(status ->
                    itemSrateRepository.findByIdItemSrateCust(cc).stream()
                            .collect(Collectors.toMap(ItemSrate::getItemCode, ItemSrate::getItemSrateRate))
            );
            if (custRateMap == null) custRateMap = java.util.Map.of();
        }
        final java.util.Map<Integer, BigDecimal> finalCustRateMap = custRateMap;

        if (request.isFromCart() && request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            for (Long cartItemId : request.getCartItemIds()) {
                CartItem cartItem = webTxTemplate.execute(status ->
                        cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 항목: " + cartItemId)));

                if (!cartItem.getMemberId().equals(member.getId())) {
                    throw new IllegalArgumentException("본인의 장바구니 항목만 주문할 수 있습니다");
                }

                if (cartItem.isCustomItem()) {
                    CustomItem ci = webTxTemplate.execute(status ->
                            customItemRepository.findById(cartItem.getCustomItemId())
                                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커스텀 아이템: " + cartItem.getCustomItemId())));
                    dataList.add(OrderItemData.forCustomItem(ci, BigDecimal.valueOf(cartItem.getQuantity())));
                } else {
                    ItemCode item = erpTxTemplate.execute(status ->
                            itemCodeRepository.findById(cartItem.getItemCode())
                                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + cartItem.getItemCode())));

                    BigDecimal rate = resolveRate(item, finalCustRateMap);
                    dataList.add(new OrderItemData(item, BigDecimal.valueOf(cartItem.getQuantity()), rate, ""));
                }
            }
        } else if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (OrderItemRequest reqItem : request.getItems()) {
                ItemCode item = erpTxTemplate.execute(status ->
                        itemCodeRepository.findById(reqItem.getItemCode())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + reqItem.getItemCode())));

                BigDecimal rate = reqItem.getRate() != null ? reqItem.getRate() : resolveRate(item, finalCustRateMap);
                String itemRemark = reqItem.getRemark() != null ? reqItem.getRemark() : "";

                dataList.add(new OrderItemData(item, reqItem.getQuantity(), rate, itemRemark));
            }
        } else {
            throw new IllegalArgumentException("주문 항목이 없습니다. items 또는 cartItemIds를 입력해주세요.");
        }

        return dataList;
    }

    private BigDecimal resolveRate(ItemCode item, java.util.Map<Integer, BigDecimal> custRateMap) {
        BigDecimal custRate = custRateMap.get(item.getItemCodeCode());
        if (custRate != null && custRate.compareTo(BigDecimal.ZERO) > 0) {
            return custRate;
        }
        return item.getItemCodeSrate();
    }

    private Integer parseCustCode(String custCodeStr) {
        if (custCodeStr == null || custCodeStr.isBlank()) return null;
        try { return Integer.parseInt(custCodeStr.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    /**
     * Web DB용 OrderTran 빌드
     */
    public WebOrderTran buildWebOrderTran(WebOrderMast mast, int seq, OrderItemData data,
                                            String loginId, LocalDateTime now) {
        AmountCalc calc = calculateAmounts(data);

        return WebOrderTran.builder()
                .orderTranDate(mast.getOrderMastDate())
                .orderTranSosok(mast.getOrderMastSosok())
                .orderTranUjcd(mast.getOrderMastUjcd())
                .orderTranAcno(mast.getOrderMastAcno())
                .orderTranSeq(seq)
                .orderTranItem(data.getItemCode())
                .orderTranDeta(data.getDeta())
                .orderTranSpec(data.getSpec())
                .orderTranUnit(data.getUnit())
                .orderTranCalc(data.getCalc())
                .orderTranVdiv(data.getVdiv())
                .orderTranAdiv(data.getAdiv())
                .orderTranRate(data.rate)
                .orderTranCnt(data.cnt)
                .orderTranDcPer(calc.dcPer)
                .orderTranDcAmt(calc.dcAmt)
                .orderTranAmt(calc.amt)
                .orderTranNet(calc.net)
                .orderTranVat(calc.vat)
                .orderTranAdv(calc.adv)
                .orderTranTot(calc.tot)
                .orderTranLrate(BigDecimal.ZERO)
                .orderTranPrice(data.getPrate())
                .orderTranPrice2(BigDecimal.ZERO)
                .orderTranLdiv(0)
                .orderTranRemark(data.remark)
                .orderTranStau(STAU_ORDER_REGISTERED)
                .orderTranFdate(now)
                .orderTranFuser(loginId)
                .orderTranLdate(now)
                .orderTranLuser(loginId)
                .orderTranCustomitem(data.customItemId)
                .build();
    }

    /**
     * ERP DB용 OrderTran 빌드
     */
    public OrderTran buildErpOrderTran(String date, Integer sosok, String ujcd, Integer acno,
                                         int seq, OrderItemData data,
                                         String loginId, LocalDateTime now) {
        AmountCalc calc = calculateAmounts(data);

        return OrderTran.builder()
                .orderTranDate(date)
                .orderTranSosok(sosok)
                .orderTranUjcd(ujcd)
                .orderTranAcno(acno)
                .orderTranSeq(seq)
                .orderTranItem(data.getItemCode())
                .orderTranDeta(data.getDeta())
                .orderTranSpec(data.getSpec())
                .orderTranUnit(data.getUnit())
                .orderTranCalc(data.getCalc())
                .orderTranVdiv(data.getVdiv())
                .orderTranAdiv(data.getAdiv())
                .orderTranRate(data.rate)
                .orderTranCnt(data.cnt)
                .orderTranDcPer(calc.dcPer)
                .orderTranDcAmt(calc.dcAmt)
                .orderTranAmt(calc.amt)
                .orderTranNet(calc.net)
                .orderTranVat(calc.vat)
                .orderTranAdv(calc.adv)
                .orderTranTot(calc.tot)
                .orderTranLrate(BigDecimal.ZERO)
                .orderTranPrice(data.getPrate())
                .orderTranPrice2(BigDecimal.ZERO)
                .orderTranLdiv(0)
                .orderTranRemark(data.remark)
                .orderTranStau(STAU_ORDER_REGISTERED)
                .orderTranFdate(now)
                .orderTranFuser(loginId)
                .orderTranLdate(now)
                .orderTranLuser(loginId)
                .build();
    }

    /**
     * 금액 계산 공통 로직
     */
    AmountCalc calculateAmounts(OrderItemData data) {
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal amt = data.rate;
        BigDecimal net = amt.multiply(data.cnt);
        BigDecimal vat = (data.getVdiv() == 1)
                ? net.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP) : zero;
        BigDecimal tot = net.add(vat);

        return new AmountCalc(amt, zero, zero, net, vat, zero, tot);
    }

    Integer nullSafe(Integer value) {
        return value != null ? value : 0;
    }

    // ======================== 조회 API ========================

    /**
     * 내 주문 목록 조회 (날짜/상태 필터)
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderMastResponse> getMyOrders(String loginId, String startDate, String endDate, String status) {
        Member member = memberRepository.findByMemberId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + loginId));

        // ORDER_MAST_CUST 기준으로 조회 (회원 직접 주문 + 관리자 출고 주문 모두 포함)
        Integer custCode = null;
        if (member.getCustCode() != null && !member.getCustCode().isBlank()) {
            try { custCode = Integer.parseInt(member.getCustCode().trim()); }
            catch (NumberFormatException ignored) {}
        }

        List<WebOrderMast> orders;
        if (custCode != null && custCode > 0) {
            orders = webOrderMastRepository.findByOrderMastCustOrderByOrderMastDateDescOrderMastAcnoDesc(custCode);
        } else {
            orders = webOrderMastRepository.findByWebMemberIdOrderByOrderMastDateDescOrderMastAcnoDesc(member.getId());
        }

        if (startDate != null && !startDate.isBlank()) {
            orders = orders.stream()
                    .filter(o -> o.getOrderMastDate().compareTo(startDate) >= 0)
                    .collect(Collectors.toList());
        }
        if (endDate != null && !endDate.isBlank()) {
            orders = orders.stream()
                    .filter(o -> o.getOrderMastDate().compareTo(endDate) <= 0)
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isBlank()) {
            orders = orders.stream()
                    .filter(o -> status.equals(o.getWebOrderStatus()))
                    .collect(Collectors.toList());
        }

        return orders.stream()
                .map(mast -> {
                    List<WebOrderTran> trans = webOrderTranRepository
                            .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                    mast.getOrderMastDate(), mast.getOrderMastSosok(),
                                    mast.getOrderMastUjcd(), mast.getOrderMastAcno());
                    List<OrderTranResponse> tranResponses = trans.stream()
                            .map(t -> OrderTranResponse.from(t, resolveStauName(t.getOrderTranStau())))
                            .collect(Collectors.toList());
                    String stau = trans.isEmpty() ? null : trans.get(0).getOrderTranStau();
                    String custName = resolveOrderCustName(mast.getOrderMastCust());

                    OrderMastResponse resp = OrderMastResponse.from(mast, tranResponses, stau, resolveStauName(stau));
                    return OrderMastResponse.builder()
                            .orderDate(resp.getOrderDate()).sosok(resp.getSosok()).ujcd(resp.getUjcd())
                            .acno(resp.getAcno()).cust(resp.getCust()).sawon(resp.getSawon())
                            .odate(resp.getOdate()).project(resp.getProject()).remark(resp.getRemark())
                            .fdate(resp.getFdate()).fuser(resp.getFuser()).ldate(resp.getLdate()).luser(resp.getLuser())
                            .webMemberId(resp.getWebMemberId()).webOrderStatus(resp.getWebOrderStatus())
                            .orderKey(resp.getOrderKey()).items(resp.getItems())
                            .itemCount(resp.getItemCount()).totalAmount(resp.getTotalAmount())
                            .stau(resp.getStau()).stauName(resp.getStauName())
                            .memberName(custName)
                            .custCodeName(custName)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 주문 상세 조회
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public OrderMastResponse getOrderDetail(String date, Integer sosok, String ujcd, Integer acno) {
        WebOrderMast.WebOrderMastId id = new WebOrderMast.WebOrderMastId(date, sosok, ujcd, acno);
        WebOrderMast mast = webOrderMastRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        List<WebOrderTran> trans = webOrderTranRepository
                .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                        date, sosok, ujcd, acno);

        List<OrderTranResponse> tranResponses = trans.stream()
                .map(t -> {
                    String num = itemCodeRepository.findById(t.getOrderTranItem())
                            .map(ItemCode::getItemCodeNum).orElse(null);
                    String thumbUrl = getThumbnailUrl(t);
                    return OrderTranResponse.from(t, resolveStauName(t.getOrderTranStau()), num, thumbUrl);
                })
                .collect(Collectors.toList());

        String stau = trans.isEmpty() ? null : trans.get(0).getOrderTranStau();

        // ORDER_MAST_CUST 기준 거래처명 조회 (통일)
        String custName = resolveOrderCustName(mast.getOrderMastCust());

        // 비회원(custCode==0)인 경우 GuestOrder에서 조회
        GuestOrder guest = null;
        if (mast.getOrderMastCust() == null || mast.getOrderMastCust() == 0) {
            guest = guestOrderRepository.findByOrderKey(mast.getOrderKey()).orElse(null);
            if (guest != null) {
                custName = guest.getCompanyName();
            }
        }

        OrderMastResponse resp = OrderMastResponse.from(mast, tranResponses, stau, resolveStauName(stau));
        OrderMastResponse.OrderMastResponseBuilder builder = OrderMastResponse.builder()
                .orderDate(resp.getOrderDate()).sosok(resp.getSosok()).ujcd(resp.getUjcd())
                .acno(resp.getAcno()).cust(resp.getCust()).sawon(resp.getSawon())
                .odate(resp.getOdate()).project(resp.getProject()).remark(resp.getRemark())
                .fdate(resp.getFdate()).fuser(resp.getFuser()).ldate(resp.getLdate()).luser(resp.getLuser())
                .webMemberId(resp.getWebMemberId()).webOrderStatus(resp.getWebOrderStatus())
                .orderKey(resp.getOrderKey()).items(resp.getItems())
                .itemCount(resp.getItemCount()).totalAmount(resp.getTotalAmount())
                .stau(resp.getStau()).stauName(resp.getStauName())
                .memberName(custName)
                .custCodeName(custName)
                .webDriverId(mast.getWebDriverId())
                .driverName(mast.getWebDriverId() != null
                        ? memberRepository.findById(mast.getWebDriverId())
                                .map(Member::getMemberName).orElse(null) : null)
                .driverSign(mast.getWebDriverSign())
                .driverSignAt(mast.getWebDriverSignAt() != null
                        ? mast.getWebDriverSignAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .driverSignName(mast.getWebDriverSignMemberId() != null
                        ? memberRepository.findById(mast.getWebDriverSignMemberId())
                                .map(Member::getMemberName).orElse(null) : null)
                .custSign(mast.getWebCustSign())
                .custSignAt(mast.getWebCustSignAt() != null
                        ? mast.getWebCustSignAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .custSignName(mast.getWebCustSignMemberId() != null
                        ? memberRepository.findById(mast.getWebCustSignMemberId())
                                .map(Member::getMemberName).orElse(null) : null);

        if (guest != null) {
            builder.guestCompanyName(guest.getCompanyName())
                   .guestManagerName(guest.getManagerName())
                   .guestContact(guest.getContact())
                   .guestAddress(guest.getAddress());
        }

        return builder.build();
    }

    /**
     * ORDER_MAST_CUST 기준으로 거래처명 조회 (통일된 방식)
     * - custCode > 0: ERP Customer 테이블에서 조회
     * - custCode == 0: 비회원 (GuestOrder에서 별도 처리)
     */
    private String resolveOrderCustName(Integer custCode) {
        if (custCode == null || custCode <= 0) return null;
        try {
            return customerRepository.findById(custCode)
                    .map(c -> c.getDisplayName())
                    .orElse(null);
        } catch (Exception e) {
            log.warn("거래처명 조회 실패 - custCode: {}", custCode);
            return null;
        }
    }


    private String resolveStauName(String stauCode) {
        if (stauCode == null || stauCode.isBlank()) return null;
        return commonCode3Repository.findByCommCod3Code(stauCode.trim())
                .map(c -> c.getCommCod3Hnam())
                .orElse(stauCode);
    }

    private String getThumbnailUrl(WebOrderTran t) {
        try {
            if (t.getOrderTranCustomitem() != null) {
                return itemImageService.getCustomItemThumbnailUrl(t.getOrderTranCustomitem());
            }
            if (t.getOrderTranItem() != null && t.getOrderTranItem() > 0) {
                ItemImageResponse thumb = itemImageService.getThumbnail(t.getOrderTranItem());
                return thumb != null ? thumb.getImageUrl() : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 주문 상태 변경 (관리자용)
     */
    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse updateOrderStatus(String date, Integer sosok, String ujcd, Integer acno, String status) {
        WebOrderMast.WebOrderMastId id = new WebOrderMast.WebOrderMastId(date, sosok, ujcd, acno);
        WebOrderMast mast = webOrderMastRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        mast.updateStatus(status);
        webOrderMastRepository.save(mast);
        log.info("주문 상태 변경 - key: {}, status: {}", mast.getOrderKey(), status);

        return OrderMastResponse.from(mast);
    }

    /**
     * 전체 주문 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderMastResponse> getAllOrders(String startDate, String endDate, String status) {
        List<WebOrderMast> orders;
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();
        if (hasStart || hasEnd) {
            String sd = hasStart ? startDate : "00000000";
            String ed = hasEnd ? endDate : "99999999";
            orders = webOrderMastRepository.findByDateRange(sd, ed);
        } else {
            String today = LocalDate.now().format(DATE_FMT);
            orders = webOrderMastRepository.findByOrderMastDateOrderByOrderMastAcnoDesc(today);
        }

        if (status != null && !status.isBlank()) {
            orders = orders.stream()
                    .filter(o -> status.equals(o.getWebOrderStatus()))
                    .collect(Collectors.toList());
        }

        return orders.stream()
                .map(mast -> {
                    List<WebOrderTran> trans = webOrderTranRepository
                            .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                    mast.getOrderMastDate(), mast.getOrderMastSosok(),
                                    mast.getOrderMastUjcd(), mast.getOrderMastAcno());
                    List<OrderTranResponse> tranResponses = trans.stream()
                            .map(OrderTranResponse::from)
                            .collect(Collectors.toList());
                    String stau = trans.isEmpty() ? null : trans.get(0).getOrderTranStau();

                    // ORDER_MAST_CUST 기준 거래처명 조회 (통일)
                    String custName = resolveOrderCustName(mast.getOrderMastCust());

                    // 비회원(custCode==0)인 경우 GuestOrder에서 조회
                    GuestOrder guest = null;
                    if (mast.getOrderMastCust() == null || mast.getOrderMastCust() == 0) {
                        guest = guestOrderRepository.findByOrderKey(mast.getOrderKey()).orElse(null);
                        if (guest != null) {
                            custName = guest.getCompanyName();
                        }
                    }

                    OrderMastResponse resp = OrderMastResponse.from(mast, tranResponses, stau, resolveStauName(stau));
                    OrderMastResponse.OrderMastResponseBuilder builder = OrderMastResponse.builder()
                            .orderDate(resp.getOrderDate()).sosok(resp.getSosok()).ujcd(resp.getUjcd())
                            .acno(resp.getAcno()).cust(resp.getCust()).sawon(resp.getSawon())
                            .odate(resp.getOdate()).project(resp.getProject()).remark(resp.getRemark())
                            .fdate(resp.getFdate()).fuser(resp.getFuser()).ldate(resp.getLdate()).luser(resp.getLuser())
                            .webMemberId(resp.getWebMemberId()).webOrderStatus(resp.getWebOrderStatus())
                            .orderKey(resp.getOrderKey()).items(resp.getItems())
                            .itemCount(resp.getItemCount()).totalAmount(resp.getTotalAmount())
                            .stau(resp.getStau()).stauName(resp.getStauName())
                            .memberName(custName)
                            .custCodeName(custName);

                    if (guest != null) {
                        builder.guestCompanyName(guest.getCompanyName())
                               .guestManagerName(guest.getManagerName())
                               .guestContact(guest.getContact())
                               .guestAddress(guest.getAddress());
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 상태별 주문 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderMastResponse> getOrdersByStatus(String status) {
        return webOrderMastRepository.findByWebOrderStatusOrderByOrderMastDateDescOrderMastAcnoDesc(status)
                .stream()
                .map(OrderMastResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 거래처별 발주 집계
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderStatsByCustResponse> getStatsByCust(String startDate, String endDate) {
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();
        String sd = hasStart ? startDate : (hasEnd ? "00000000" : LocalDate.now().format(DATE_FMT));
        String ed = hasEnd ? endDate : (hasStart ? "99999999" : sd);

        List<WebOrderMast> masters = webOrderMastRepository.findByDateRange(sd, ed);

        // ORDER_MAST_CUST 기준 그룹핑 (통일된 거래처 기준)
        java.util.Map<Integer, List<WebOrderMast>> grouped = masters.stream()
                .filter(m -> m.getOrderMastCust() != null)
                .collect(Collectors.groupingBy(WebOrderMast::getOrderMastCust));

        return grouped.entrySet().stream().map(entry -> {
            Integer custCode = entry.getKey();
            List<WebOrderMast> orders = entry.getValue();

            String custName;
            if (custCode == 0) {
                // 비회원: GuestOrder에서 업체명 가져오기 (첫 번째 주문 기준)
                custName = orders.stream()
                        .map(m -> guestOrderRepository.findByOrderKey(m.getOrderKey()).orElse(null))
                        .filter(g -> g != null)
                        .map(GuestOrder::getCompanyName)
                        .findFirst().orElse("비회원");
            } else {
                custName = resolveOrderCustName(custCode);
            }

            BigDecimal totalQty = BigDecimal.ZERO;
            BigDecimal totalAmt = BigDecimal.ZERO;
            StringBuilder remarks = new StringBuilder();

            for (WebOrderMast mast : orders) {
                List<WebOrderTran> trans = webOrderTranRepository
                        .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                mast.getOrderMastDate(), mast.getOrderMastSosok(),
                                mast.getOrderMastUjcd(), mast.getOrderMastAcno());
                for (WebOrderTran t : trans) {
                    if (t.getOrderTranCnt() != null) totalQty = totalQty.add(t.getOrderTranCnt());
                    if (t.getOrderTranTot() != null) totalAmt = totalAmt.add(t.getOrderTranTot());
                }
                if (mast.getOrderMastRemark() != null && !mast.getOrderMastRemark().isBlank()) {
                    if (remarks.length() > 0) remarks.append(" / ");
                    remarks.append(mast.getOrderMastRemark());
                }
            }

            return OrderStatsByCustResponse.builder()
                    .memberId(custCode.longValue())
                    .memberName(custName)
                    .orderCount(orders.size())
                    .totalQuantity(totalQty)
                    .totalAmount(totalAmt)
                    .remark(remarks.toString())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 거래처별 상세 (품목별 그룹핑) - custCode 기준으로 필터링
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderStatsByItemResponse> getStatsByCustDetail(Long custCodeParam, String startDate, String endDate) {
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();
        String sd = hasStart ? startDate : (hasEnd ? "00000000" : LocalDate.now().format(DATE_FMT));
        String ed = hasEnd ? endDate : (hasStart ? "99999999" : sd);

        Integer custCode = custCodeParam.intValue();
        List<WebOrderMast> masters = webOrderMastRepository.findByDateRange(sd, ed).stream()
                .filter(m -> custCode.equals(m.getOrderMastCust()))
                .collect(Collectors.toList());

        List<WebOrderTran> allTrans = new ArrayList<>();
        for (WebOrderMast mast : masters) {
            allTrans.addAll(webOrderTranRepository
                    .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                            mast.getOrderMastDate(), mast.getOrderMastSosok(),
                            mast.getOrderMastUjcd(), mast.getOrderMastAcno()));
        }

        java.util.Map<Integer, List<WebOrderTran>> grouped = allTrans.stream()
                .filter(t -> t.getOrderTranItem() != null)
                .collect(Collectors.groupingBy(WebOrderTran::getOrderTranItem));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Integer itemCd = entry.getKey();
                    List<WebOrderTran> trans = entry.getValue();

                    String itemName = itemCodeRepository.findById(itemCd)
                            .map(ItemCode::getItemCodeHnam).orElse("-");

                    BigDecimal totalQty = BigDecimal.ZERO;
                    BigDecimal totalAmt = BigDecimal.ZERO;
                    BigDecimal rateSum = BigDecimal.ZERO;
                    int rateCount = 0;

                    for (WebOrderTran t : trans) {
                        if (t.getOrderTranCnt() != null) totalQty = totalQty.add(t.getOrderTranCnt());
                        if (t.getOrderTranTot() != null) totalAmt = totalAmt.add(t.getOrderTranTot());
                        if (t.getOrderTranRate() != null) { rateSum = rateSum.add(t.getOrderTranRate()); rateCount++; }
                    }

                    BigDecimal avgRate = rateCount > 0
                            ? rateSum.divide(BigDecimal.valueOf(rateCount), 0, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return OrderStatsByItemResponse.builder()
                            .itemCode(itemCd).itemName(itemName)
                            .avgRate(avgRate).totalQuantity(totalQty).totalAmount(totalAmt)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 품목별 발주 집계
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<OrderStatsByItemResponse> getStatsByItem(String startDate, String endDate, boolean marketOnly) {
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();
        String sd = hasStart ? startDate : (hasEnd ? "00000000" : LocalDate.now().format(DATE_FMT));
        String ed = hasEnd ? endDate : (hasStart ? "99999999" : sd);

        List<WebOrderTran> allTrans = webOrderTranRepository.findByDateRange(sd, ed);

        // 품목코드별 그룹핑
        java.util.Map<Integer, List<WebOrderTran>> grouped = allTrans.stream()
                .filter(t -> t.getOrderTranItem() != null)
                .collect(Collectors.groupingBy(WebOrderTran::getOrderTranItem));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Integer itemCd = entry.getKey();
                    List<WebOrderTran> trans = entry.getValue();

                    var itemOpt = itemCodeRepository.findById(itemCd);
                    if (itemOpt.isEmpty()) return null;
                    ItemCode item = itemOpt.get();

                    if (marketOnly && (item.getItemCodeMarket() == null || item.getItemCodeMarket() != 1)) {
                        return null;
                    }

                    BigDecimal totalQty = BigDecimal.ZERO;
                    BigDecimal totalAmt = BigDecimal.ZERO;
                    BigDecimal rateSum = BigDecimal.ZERO;
                    int rateCount = 0;

                    for (WebOrderTran t : trans) {
                        if (t.getOrderTranCnt() != null) totalQty = totalQty.add(t.getOrderTranCnt());
                        if (t.getOrderTranTot() != null) totalAmt = totalAmt.add(t.getOrderTranTot());
                        if (t.getOrderTranRate() != null) {
                            rateSum = rateSum.add(t.getOrderTranRate());
                            rateCount++;
                        }
                    }

                    BigDecimal avgRate = rateCount > 0
                            ? rateSum.divide(BigDecimal.valueOf(rateCount), 0, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return OrderStatsByItemResponse.builder()
                            .itemCode(itemCd)
                            .itemName(item.getItemCodeHnam())
                            .avgRate(avgRate)
                            .totalQuantity(totalQty)
                            .totalAmount(totalAmt)
                            .build();
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    // ======================== 내부 데이터 클래스 ========================

    /** 주문 항목 데이터 (DB 저장 전 준비 단계) — ERP 품목 / 커스텀 품목 공통 */
    public static class OrderItemData {
        final ItemCode item;
        final Long customItemId;
        final String customName;
        final String customSpec;
        final String customUnit;
        final Integer customVdiv;
        final BigDecimal cnt;
        final BigDecimal rate;
        final String remark;

        public OrderItemData(ItemCode item, BigDecimal cnt, BigDecimal rate, String remark) {
            this.item = item;
            this.customItemId = null;
            this.customName = null;
            this.customSpec = null;
            this.customUnit = null;
            this.customVdiv = null;
            this.cnt = cnt;
            this.rate = rate;
            this.remark = remark;
        }

        private OrderItemData(Long customItemId, String name, String spec, String unit,
                              Integer vdiv, BigDecimal cnt, BigDecimal rate, String remark) {
            this.item = null;
            this.customItemId = customItemId;
            this.customName = name;
            this.customSpec = spec;
            this.customUnit = unit;
            this.customVdiv = vdiv;
            this.cnt = cnt;
            this.rate = rate;
            this.remark = remark;
        }

        public static OrderItemData forCustomItem(CustomItem ci, BigDecimal cnt) {
            return new OrderItemData(
                    ci.getCustomItemCode(),
                    ci.getCustomItemHnam(),
                    ci.getCustomItemSpec() != null ? ci.getCustomItemSpec() : "",
                    ci.getCustomItemUnit() != null ? ci.getCustomItemUnit() : "",
                    ci.getCustomItemVdiv(),
                    cnt,
                    ci.getCustomItemSrate(),
                    ci.getCustomItemDesc() != null ? ci.getCustomItemDesc() : ""
            );
        }

        public boolean isCustom() { return customItemId != null; }

        public Integer getItemCode() {
            return item != null ? item.getItemCodeCode() : 0;
        }
        public String getDeta() {
            return item != null ? item.getItemCodeHnam() : customName;
        }
        public String getSpec() {
            return item != null ? item.getItemCodeSpec() : customSpec;
        }
        public String getUnit() {
            return item != null ? item.getItemCodeUnit() : customUnit;
        }
        public Integer getCalc() {
            return item != null ? (item.getItemCodeCalc() != null ? item.getItemCodeCalc() : 0) : 0;
        }
        public Integer getVdiv() {
            if (item != null) return item.getItemCodeVdiv() != null ? item.getItemCodeVdiv() : 0;
            return customVdiv != null ? customVdiv : 0;
        }
        public Integer getAdiv() {
            return item != null ? (item.getItemCodeAdiv() != null ? item.getItemCodeAdiv() : 0) : 0;
        }
        public BigDecimal getPrate() {
            if (item != null) return item.getItemCodePrate() != null ? item.getItemCodePrate() : BigDecimal.ZERO;
            return BigDecimal.ZERO;
        }
    }

    /** 금액 계산 결과 */
    static class AmountCalc {
        final BigDecimal amt;     // 판매단가
        final BigDecimal dcPer;   // 할인율
        final BigDecimal dcAmt;   // 할인금액
        final BigDecimal net;     // 공급가
        final BigDecimal vat;     // 부가세
        final BigDecimal adv;     // 예수금
        final BigDecimal tot;     // 합산금액

        AmountCalc(BigDecimal amt, BigDecimal dcPer, BigDecimal dcAmt,
                   BigDecimal net, BigDecimal vat, BigDecimal adv, BigDecimal tot) {
            this.amt = amt;
            this.dcPer = dcPer;
            this.dcAmt = dcAmt;
            this.net = net;
            this.vat = vat;
            this.adv = adv;
            this.tot = tot;
        }
    }
}
