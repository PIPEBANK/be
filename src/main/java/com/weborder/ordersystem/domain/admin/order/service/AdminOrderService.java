package com.weborder.ordersystem.domain.admin.order.service;

import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderCreateRequest;
import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderDetailResponse;
import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderListResponse;
import com.weborder.ordersystem.domain.admin.order.dto.OrderStatusUpdateRequest;
import com.weborder.ordersystem.domain.erp.entity.Customer;
import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import com.weborder.ordersystem.domain.erp.entity.OrderMast;
import com.weborder.ordersystem.domain.erp.entity.OrderTran;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemSrateRepository;
import com.weborder.ordersystem.domain.erp.repository.OrderMastRepository;
import com.weborder.ordersystem.domain.erp.repository.OrderTranRepository;
import com.weborder.ordersystem.domain.web.order.dto.OrderItemRequest;
import com.weborder.ordersystem.domain.web.order.dto.OrderMastResponse;
import com.weborder.ordersystem.domain.web.order.dto.OrderTranResponse;
import com.weborder.ordersystem.domain.web.customitem.entity.CustomItem;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemRepository;
import com.weborder.ordersystem.domain.web.order.entity.GuestOrder;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import com.weborder.ordersystem.domain.web.order.repository.GuestOrderRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import com.weborder.ordersystem.domain.web.order.service.WebOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.entity.MemberRole;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.notification.service.NotificationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminOrderService {

    private final WebOrderMastRepository webOrderMastRepository;
    private final WebOrderTranRepository webOrderTranRepository;
    private final OrderMastRepository erpOrderMastRepository;
    private final OrderTranRepository erpOrderTranRepository;
    private final CustomerRepository customerRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final ItemSrateRepository itemSrateRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final CustomItemRepository customItemRepository;
    private final WebOrderService webOrderService;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final TransactionTemplate webTxTemplate;
    private final TransactionTemplate erpTxTemplate;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Integer DEFAULT_SOSOK = 1;
    private static final String DEFAULT_UJCD = "0013001000";
    private static final int MAX_ACNO_RETRY = 3;

    public AdminOrderService(
            WebOrderMastRepository webOrderMastRepository,
            WebOrderTranRepository webOrderTranRepository,
            OrderMastRepository erpOrderMastRepository,
            OrderTranRepository erpOrderTranRepository,
            CustomerRepository customerRepository,
            ItemCodeRepository itemCodeRepository,
            ItemSrateRepository itemSrateRepository,
            GuestOrderRepository guestOrderRepository,
            CustomItemRepository customItemRepository,
            WebOrderService webOrderService,
            MemberRepository memberRepository,
            NotificationService notificationService,
            @Qualifier("webTransactionManager") PlatformTransactionManager webTxManager,
            @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager) {
        this.webOrderMastRepository = webOrderMastRepository;
        this.webOrderTranRepository = webOrderTranRepository;
        this.erpOrderMastRepository = erpOrderMastRepository;
        this.erpOrderTranRepository = erpOrderTranRepository;
        this.customerRepository = customerRepository;
        this.itemCodeRepository = itemCodeRepository;
        this.itemSrateRepository = itemSrateRepository;
        this.guestOrderRepository = guestOrderRepository;
        this.customItemRepository = customItemRepository;
        this.webOrderService = webOrderService;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
        this.webTxTemplate = new TransactionTemplate(webTxManager);
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
    }

    /**
     * 관리자 출고주문 생성 (ERP 거래처 / 비회원 분기)
     */
    public OrderMastResponse createAdminOrder(AdminOrderCreateRequest request, String createdBy) {
        log.info("===== 출고주문 생성 시작 - type: {}, user: {} =====", request.getCustomerType(), createdBy);

        boolean isGuest = "guest".equalsIgnoreCase(request.getCustomerType());

        final Integer custCode;
        final String custName;

        if (!isGuest) {
            if (request.getCustCode() == null) throw new IllegalArgumentException("ERP 거래처코드는 필수입니다.");
            Customer customer = erpTxTemplate.execute(status ->
                    customerRepository.findById(request.getCustCode())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처: " + request.getCustCode())));
            if (customer == null) throw new IllegalArgumentException("거래처 조회에 실패했습니다.");
            custCode = request.getCustCode();
            custName = customer.getDisplayName();
            log.info("ERP 거래처 - code: {}, name: {}", custCode, custName);
        } else {
            if (request.getCompanyName() == null || request.getCompanyName().isBlank())
                throw new IllegalArgumentException("비회원 업체명은 필수입니다.");
            if (request.getManagerName() == null || request.getManagerName().isBlank())
                throw new IllegalArgumentException("비회원 담당자명은 필수입니다.");
            if (request.getContact() == null || request.getContact().isBlank())
                throw new IllegalArgumentException("비회원 연락처는 필수입니다.");
            custCode = 0;
            custName = request.getCompanyName();
        }

        String today = LocalDate.now().format(DATE_FMT);
        LocalDateTime now = LocalDateTime.now();
        Integer sosok = DEFAULT_SOSOK;
        String ujcd = DEFAULT_UJCD;

        String odate = request.getOdate() != null ? request.getOdate() : today;
        String remark = request.getRemark() != null ? request.getRemark() : "";

        // 거래처별 단가 조회
        final Integer cc = custCode;
        Map<Integer, BigDecimal> custRateMap = (!isGuest && cc > 0)
                ? erpTxTemplate.execute(status ->
                    itemSrateRepository.findByIdItemSrateCust(cc).stream()
                            .collect(Collectors.toMap(ItemSrate::getItemCode, ItemSrate::getItemSrateRate)))
                : Map.of();
        if (custRateMap == null) custRateMap = Map.of();
        final Map<Integer, BigDecimal> finalCustRateMap = custRateMap;

        List<WebOrderService.OrderItemData> itemDataList = new ArrayList<>();
        for (OrderItemRequest reqItem : request.getItems()) {
            if (reqItem.getCustomItemId() != null) {
                CustomItem ci = webTxTemplate.execute(status ->
                        customItemRepository.findById(reqItem.getCustomItemId())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미등록 품목: " + reqItem.getCustomItemId())));
                itemDataList.add(WebOrderService.OrderItemData.forCustomItem(ci, reqItem.getQuantity()));
            } else {
                if (reqItem.getItemCode() == null) {
                    throw new IllegalArgumentException("itemCode 또는 customItemId가 필요합니다");
                }
                ItemCode item = erpTxTemplate.execute(status ->
                        itemCodeRepository.findById(reqItem.getItemCode())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + reqItem.getItemCode())));
                BigDecimal rate;
                if (reqItem.getRate() != null) {
                    rate = reqItem.getRate();
                } else {
                    BigDecimal custRate = finalCustRateMap.get(item.getItemCodeCode());
                    rate = (custRate != null && custRate.compareTo(BigDecimal.ZERO) > 0)
                            ? custRate : item.getItemCodeSrate();
                }
                String itemRemark = reqItem.getRemark() != null ? reqItem.getRemark() : "";
                itemDataList.add(new WebOrderService.OrderItemData(item, reqItem.getQuantity(), rate, itemRemark));
            }
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
                            .orderMastCust(custCode)
                            .orderMastSawon(0)
                            .orderMastOdate(finalOdate)
                            .orderMastProject(0)
                            .orderMastRemark(finalRemark)
                            .orderMastFdate(now)
                            .orderMastFuser(createdBy)
                            .orderMastLdate(now)
                            .orderMastLuser(createdBy)
                            .webMemberId(null)
                            .webOrderStatus("ORDERED")
                            .build();

                    webOrderMastRepository.save(mast);
                    log.info("[Web DB] 출고주문 마스터 저장 - key: {}, user: {}", mast.getOrderKey(), createdBy);

                    List<WebOrderTran> webTrans = new ArrayList<>();
                    int seq = 1;
                    for (WebOrderService.OrderItemData data : itemDataList) {
                        webTrans.add(webOrderService.buildWebOrderTran(mast, seq++, data, createdBy, now));
                    }
                    webOrderTranRepository.saveAll(webTrans);
                    log.info("[Web DB] 출고주문 상세 {} 건 저장 - user: {}", webTrans.size(), createdBy);

                    if (isGuest) {
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
                        .orderMastCust(custCode)
                        .orderMastSawon(0)
                        .orderMastOdate(finalOdate)
                        .orderMastProject(0)
                        .orderMastRemark(finalRemark)
                        .orderMastFdate(now)
                        .orderMastFuser(createdBy)
                        .orderMastLdate(now)
                        .orderMastLuser(createdBy)
                        .build();

                erpOrderMastRepository.save(erpMast);
                log.info("[ERP DB] 출고주문 마스터 저장 - user: {}", createdBy);

                List<OrderTran> erpTrans = new ArrayList<>();
                int seq = 1;
                for (WebOrderService.OrderItemData data : itemDataList) {
                    erpTrans.add(webOrderService.buildErpOrderTran(today, sosok, ujcd, acnoForErp,
                            seq++, data, createdBy, now));
                }
                erpOrderTranRepository.saveAll(erpTrans);
                log.info("[ERP DB] 관리자 출고주문 상세 {} 건 저장", erpTrans.size());
            });
        } catch (Exception e) {
            log.error("[ERP DB] 관리자 출고주문 저장 실패! Web DB 롤백 시작", e);
            try {
                webTxTemplate.executeWithoutResult(status -> {
                    List<WebOrderTran> webTrans = webOrderTranRepository
                            .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                    today, sosok, ujcd, acnoForErp);
                    webOrderTranRepository.deleteAll(webTrans);
                    if (isGuest) {
                        guestOrderRepository.findByOrderKey(webMastForRollback.getOrderKey())
                                .ifPresent(guestOrderRepository::delete);
                    }
                    WebOrderMast.WebOrderMastId mastId = new WebOrderMast.WebOrderMastId(today, sosok, ujcd, acnoForErp);
                    webOrderMastRepository.deleteById(mastId);
                    log.info("[Web DB] 관리자 출고주문 보상 롤백 완료");
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

        log.info("===== 관리자 출고주문 생성 완료 - key: {} =====", webMast.getOrderKey());

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
                .memberName(isGuest ? request.getCompanyName() : custName)
                .custCodeName(isGuest ? request.getCompanyName() : custName)
                .guestCompanyName(isGuest ? request.getCompanyName() : null)
                .guestManagerName(isGuest ? request.getManagerName() : null)
                .guestContact(isGuest ? request.getContact() : null)
                .guestAddress(isGuest ? request.getAddress() : null)
                .build();
    }

    /**
     * 배송기사 출고주문 생성
     * - 기존 createAdminOrder 로직을 그대로 활용한 뒤
     * - 생성된 주문을 즉시 CONFIRMED + 본인 기사 배정 처리
     */
    public OrderMastResponse createDriverOrder(AdminOrderCreateRequest request, Long driverMemberId) {
        log.info("===== 배송기사 출고주문 생성 시작 - driverId: {} =====", driverMemberId);

        Member driver = memberRepository.findById(driverMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        if (driver.getRole() != MemberRole.DRIVER) {
            throw new IllegalArgumentException("배송기사 권한의 회원만 출고주문 생성이 가능합니다");
        }

        OrderMastResponse orderResponse = createAdminOrder(request, driver.getMemberId());

        String[] parts = orderResponse.getOrderKey().split("-");
        webTxTemplate.executeWithoutResult(txStatus -> {
            WebOrderMast.WebOrderMastId mastId = new WebOrderMast.WebOrderMastId(
                    parts[0], Integer.parseInt(parts[1]), parts[2], Integer.parseInt(parts[3]));
            WebOrderMast mast = webOrderMastRepository.findById(mastId)
                    .orElseThrow(() -> new IllegalStateException("생성된 주문을 찾을 수 없습니다"));
            mast.assignDriver(driverMemberId, driver.getMemberId());
            webOrderMastRepository.save(mast);
        });

        log.info("===== 배송기사 출고주문 생성 완료 - key: {}, driverId: {} =====",
                orderResponse.getOrderKey(), driverMemberId);

        if (orderResponse.getCust() != null && orderResponse.getCust() > 0) {
            List<Member> custMembers = memberRepository.findByCustCodeAndUseYn(
                    String.valueOf(orderResponse.getCust()), true);
            for (Member m : custMembers) {
                notificationService.send(m.getId(), "ORDER_CONFIRMED",
                        "접수완료", "주문이 접수되었습니다. 배송 준비중입니다.",
                        orderResponse.getOrderKey());
            }
        }

        // 알림: 관리자들에게 (배송기사가 직접 주문 생성했으므로)
        List<Member> admins = memberRepository.findByRoleAndUseYnTrue(MemberRole.ADMIN);
        String custName = orderResponse.getCustCodeName() != null ? orderResponse.getCustCodeName() : "";
        for (Member admin : admins) {
            notificationService.send(admin.getId(), "ORDER_CREATED",
                    "출고주문", driver.getMemberName() + " 기사가 " + custName + " 출고주문을 생성했습니다.",
                    orderResponse.getOrderKey());
        }

        return OrderMastResponse.builder()
                .orderDate(orderResponse.getOrderDate())
                .sosok(orderResponse.getSosok())
                .ujcd(orderResponse.getUjcd())
                .acno(orderResponse.getAcno())
                .cust(orderResponse.getCust())
                .odate(orderResponse.getOdate())
                .remark(orderResponse.getRemark())
                .fdate(orderResponse.getFdate())
                .webOrderStatus("CONFIRMED")
                .orderKey(orderResponse.getOrderKey())
                .items(orderResponse.getItems())
                .itemCount(orderResponse.getItemCount())
                .totalAmount(orderResponse.getTotalAmount())
                .memberName(orderResponse.getMemberName())
                .custCodeName(orderResponse.getCustCodeName())
                .guestCompanyName(orderResponse.getGuestCompanyName())
                .guestManagerName(orderResponse.getGuestManagerName())
                .guestContact(orderResponse.getGuestContact())
                .guestAddress(orderResponse.getGuestAddress())
                .webDriverId(driverMemberId)
                .driverName(driver.getMemberName())
                .driverMemberId(driver.getMemberId())
                .build();
    }

    /**
     * 관리자 직접배송 출고주문 생성
     * - 기존 createAdminOrder 로직 그대로 활용
     * - 생성된 주문을 즉시 CONFIRMED + 본인 기사 배정 처리
     */
    public OrderMastResponse createDirectDeliveryOrder(AdminOrderCreateRequest request, String loginId) {
        log.info("===== 관리자 직접배송 주문 생성 시작 - user: {} =====", loginId);

        Member admin = memberRepository.findByMemberId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        OrderMastResponse orderResponse = createAdminOrder(request, loginId);

        String[] parts = orderResponse.getOrderKey().split("-");
        webTxTemplate.executeWithoutResult(txStatus -> {
            WebOrderMast.WebOrderMastId mastId = new WebOrderMast.WebOrderMastId(
                    parts[0], Integer.parseInt(parts[1]), parts[2], Integer.parseInt(parts[3]));
            WebOrderMast mast = webOrderMastRepository.findById(mastId)
                    .orElseThrow(() -> new IllegalStateException("생성된 주문을 찾을 수 없습니다"));
            mast.assignDriver(admin.getId(), loginId);
            webOrderMastRepository.save(mast);
        });

        log.info("===== 관리자 직접배송 주문 생성 완료 - key: {}, adminId: {} =====",
                orderResponse.getOrderKey(), admin.getId());

        if (orderResponse.getCust() != null && orderResponse.getCust() > 0) {
            List<Member> custMembers = memberRepository.findByCustCodeAndUseYn(
                    String.valueOf(orderResponse.getCust()), true);
            for (Member m : custMembers) {
                notificationService.send(m.getId(), "ORDER_CONFIRMED",
                        "접수완료", "주문이 접수되었습니다. 배송 준비중입니다.",
                        orderResponse.getOrderKey());
            }
        }

        return OrderMastResponse.builder()
                .orderDate(orderResponse.getOrderDate())
                .sosok(orderResponse.getSosok())
                .ujcd(orderResponse.getUjcd())
                .acno(orderResponse.getAcno())
                .cust(orderResponse.getCust())
                .odate(orderResponse.getOdate())
                .remark(orderResponse.getRemark())
                .fdate(orderResponse.getFdate())
                .webOrderStatus("CONFIRMED")
                .orderKey(orderResponse.getOrderKey())
                .items(orderResponse.getItems())
                .itemCount(orderResponse.getItemCount())
                .totalAmount(orderResponse.getTotalAmount())
                .memberName(orderResponse.getMemberName())
                .custCodeName(orderResponse.getCustCodeName())
                .guestCompanyName(orderResponse.getGuestCompanyName())
                .guestManagerName(orderResponse.getGuestManagerName())
                .guestContact(orderResponse.getGuestContact())
                .guestAddress(orderResponse.getGuestAddress())
                .webDriverId(admin.getId())
                .driverName(admin.getMemberName())
                .driverMemberId(admin.getMemberId())
                .build();
    }

    /**
     * 전체 주문 목록 조회 (날짜범위, 상태, 거래처 필터)
     */
    public List<AdminOrderListResponse> getOrderList(String startDate, String endDate,
                                                      String status, Integer cust) {
        log.info("관리자 주문 목록 조회 - startDate: {}, endDate: {}, status: {}, cust: {}",
                startDate, endDate, status, cust);

        List<WebOrderMast> orders = webTxTemplate.execute(txStatus -> {
            if (cust != null && startDate != null && endDate != null) {
                return webOrderMastRepository.findByCustAndDateRange(cust, startDate, endDate);
            } else if (cust != null) {
                return webOrderMastRepository.findByOrderMastCustOrderByOrderMastDateDescOrderMastAcnoDesc(cust);
            } else if (status != null && startDate != null && endDate != null) {
                return webOrderMastRepository.findByStatusAndDateRange(status, startDate, endDate);
            } else if (status != null) {
                return webOrderMastRepository.findByWebOrderStatusOrderByOrderMastDateDescOrderMastAcnoDesc(status);
            } else if (startDate != null && endDate != null) {
                return webOrderMastRepository.findByDateRange(startDate, endDate);
            } else {
                String today = LocalDate.now().format(DATE_FMT);
                return webOrderMastRepository.findByOrderMastDateOrderByOrderMastAcnoDesc(today);
            }
        });

        if (orders == null || orders.isEmpty()) return List.of();

        return buildListResponses(orders);
    }

    /**
     * 주문 상세 조회 (마스터 + 상세항목 + 거래처명)
     */
    public AdminOrderDetailResponse getOrderDetail(String date, Integer sosok, String ujcd, Integer acno) {
        log.info("관리자 주문 상세 조회 - {}-{}-{}-{}", date, sosok, ujcd, acno);

        WebOrderMast mast = webTxTemplate.execute(txStatus -> {
            WebOrderMast.WebOrderMastId id = new WebOrderMast.WebOrderMastId(date, sosok, ujcd, acno);
            return webOrderMastRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));
        });

        List<WebOrderTran> trans = webTxTemplate.execute(txStatus ->
                webOrderTranRepository
                        .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                date, sosok, ujcd, acno));

        List<OrderTranResponse> tranResponses = trans.stream()
                .map(OrderTranResponse::from)
                .collect(Collectors.toList());

        String custName = getCustName(mast.getOrderMastCust());

        // 비회원 주문인 경우 GuestOrder 정보 조회
        boolean isGuest = mast.getOrderMastCust() != null && mast.getOrderMastCust() == 0;
        if (isGuest) {
            return webTxTemplate.execute(txStatus -> {
                var guestOpt = guestOrderRepository.findByOrderKey(mast.getOrderKey());
                if (guestOpt.isPresent()) {
                    var g = guestOpt.get();
                    return AdminOrderDetailResponse.from(mast, custName, tranResponses,
                            g.getCompanyName(), g.getManagerName(), g.getContact(), g.getAddress());
                }
                return AdminOrderDetailResponse.from(mast, custName, tranResponses);
            });
        }

        return AdminOrderDetailResponse.from(mast, custName, tranResponses);
    }

    /**
     * 주문 상태 변경 (Web DB + ERP DB 동시 변경)
     *
     * Web DB: webOrderStatus (ORDERED/CONFIRMED/SHIPPING/DELIVERED)
     * ERP DB: ORDER_TRAN_STAU (4010010001 수주등록 / 4010020001 수주진행 / 4010030001 출하완료 / 4010030002 강제종료)
     */
    public AdminOrderDetailResponse updateOrderStatus(String date, Integer sosok, String ujcd, Integer acno,
                                                        OrderStatusUpdateRequest request, String loginId) {
        log.info("관리자 주문 상태 변경 - {}-{}-{}-{}, webStatus: {}, erpStau: {}",
                date, sosok, ujcd, acno, request.getWebOrderStatus(), request.getErpStau());

        // 1. Web DB 상태 변경
        webTxTemplate.executeWithoutResult(txStatus -> {
            WebOrderMast.WebOrderMastId id = new WebOrderMast.WebOrderMastId(date, sosok, ujcd, acno);
            WebOrderMast mast = webOrderMastRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

            if (request.getWebOrderStatus() != null) {
                mast.updateStatus(request.getWebOrderStatus(), loginId);
                webOrderMastRepository.save(mast);
                log.info("[Web DB] 주문 마스터 상태 변경 -> {}", request.getWebOrderStatus());
            }

            if (request.getErpStau() != null) {
                List<WebOrderTran> webTrans = webOrderTranRepository
                        .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                date, sosok, ujcd, acno);
                for (WebOrderTran tran : webTrans) {
                    tran.updateStau(request.getErpStau(), loginId);
                }
                webOrderTranRepository.saveAll(webTrans);
                log.info("[Web DB] 주문 상세 {} 건 STAU 변경 -> {}", webTrans.size(), request.getErpStau());
            }
        });

        // 2. ERP DB 상태 동기화
        if (request.getErpStau() != null) {
            try {
                erpTxTemplate.executeWithoutResult(txStatus -> {
                    List<OrderTran> erpTrans = erpOrderTranRepository.findByOrderMastKey(date, sosok, ujcd, acno);
                    for (OrderTran tran : erpTrans) {
                        tran.updateStau(request.getErpStau(), loginId);
                    }
                    erpOrderTranRepository.saveAll(erpTrans);
                    log.info("[ERP DB] 주문 상세 {} 건 STAU 변경 -> {}", erpTrans.size(), request.getErpStau());
                });
            } catch (Exception e) {
                log.error("[ERP DB] STAU 변경 실패 - 수동 확인 필요! date: {}, acno: {}", date, acno, e);
            }
        }

        return getOrderDetail(date, sosok, ujcd, acno);
    }

    /**
     * 거래처별 주문 목록 조회
     */
    public List<AdminOrderListResponse> getOrdersByCust(Integer cust) {
        log.info("거래처별 주문 조회 - cust: {}", cust);

        List<WebOrderMast> orders = webTxTemplate.execute(txStatus ->
                webOrderMastRepository.findByOrderMastCustOrderByOrderMastDateDescOrderMastAcnoDesc(cust));

        if (orders == null || orders.isEmpty()) return List.of();

        return buildListResponses(orders);
    }

    // ========== Private Helpers ==========

    private List<AdminOrderListResponse> buildListResponses(List<WebOrderMast> orders) {
        // 1) 거래처명 일괄 조회
        List<Integer> custCodes = orders.stream()
                .map(WebOrderMast::getOrderMastCust)
                .filter(c -> c != null && c > 0)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, String> custNameMap;
        if (!custCodes.isEmpty()) {
            try {
                custNameMap = erpTxTemplate.execute(txStatus ->
                        customerRepository.findAllById(custCodes).stream()
                                .collect(Collectors.toMap(Customer::getCustCodeCode, Customer::getDisplayName)));
            } catch (Exception e) {
                log.warn("거래처명 일괄 조회 실패", e);
                custNameMap = Map.of();
            }
        } else {
            custNameMap = Map.of();
        }
        final Map<Integer, String> finalCustNameMap = custNameMap != null ? custNameMap : Map.of();

        // 2) 주문 상세(Tran) 일괄 조회
        List<String> orderKeys = orders.stream()
                .map(WebOrderMast::getOrderKey)
                .collect(Collectors.toList());

        List<WebOrderTran> allTrans = webTxTemplate.execute(txStatus ->
                webOrderTranRepository.findByOrderKeys(orderKeys));

        Map<String, List<WebOrderTran>> transByKey = (allTrans != null ? allTrans : List.<WebOrderTran>of())
                .stream()
                .collect(Collectors.groupingBy(t ->
                        t.getOrderTranDate() + "-" + t.getOrderTranSosok() + "-" + t.getOrderTranUjcd() + "-" + t.getOrderTranAcno()));

        // 3) 비회원 정보 일괄 조회
        List<String> guestOrderKeys = orders.stream()
                .filter(m -> m.getOrderMastCust() != null && m.getOrderMastCust() == 0)
                .map(WebOrderMast::getOrderKey)
                .collect(Collectors.toList());

        Map<String, String> guestNameMap;
        if (!guestOrderKeys.isEmpty()) {
            List<GuestOrder> guestOrders = guestOrderRepository.findByOrderKeyIn(guestOrderKeys);
            guestNameMap = guestOrders.stream()
                    .collect(Collectors.toMap(GuestOrder::getOrderKey, GuestOrder::getCompanyName, (a, b) -> a));
        } else {
            guestNameMap = Map.of();
        }

        // 4) 결과 조합
        return orders.stream().map(mast -> {
            String custName = finalCustNameMap.getOrDefault(mast.getOrderMastCust(), "");
            List<WebOrderTran> trans = transByKey.getOrDefault(mast.getOrderKey(), List.of());
            BigDecimal totalAmount = trans.stream()
                    .map(WebOrderTran::getOrderTranTot)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            String guestCompanyName = guestNameMap.get(mast.getOrderKey());
            return AdminOrderListResponse.from(mast, custName, totalAmount, trans.size(), guestCompanyName);
        }).collect(Collectors.toList());
    }

    private String getCustName(Integer custCode) {
        if (custCode == null || custCode == 0) return "";
        try {
            return erpTxTemplate.execute(txStatus ->
                    customerRepository.findById(custCode)
                            .map(Customer::getDisplayName)
                            .orElse(""));
        } catch (Exception e) {
            log.warn("거래처명 조회 실패 - custCode: {}", custCode);
            return "";
        }
    }
}
