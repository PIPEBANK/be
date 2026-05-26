package com.weborder.ordersystem.domain.admin.ship.service;

import com.weborder.ordersystem.domain.admin.ship.dto.ShipCreateRequest;
import com.weborder.ordersystem.domain.admin.ship.dto.ShipDetailResponse;
import com.weborder.ordersystem.domain.admin.ship.dto.ShipTranResponse;
import com.weborder.ordersystem.domain.erp.entity.*;
import com.weborder.ordersystem.domain.erp.repository.*;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
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
public class AdminShipService {

    private final ShipMastRepository shipMastRepository;
    private final ShipTranRepository shipTranRepository;
    private final ShipOrderRepository shipOrderRepository;
    private final OrderTranRepository erpOrderTranRepository;
    private final CustomerRepository customerRepository;
    private final WebOrderMastRepository webOrderMastRepository;
    private final WebOrderTranRepository webOrderTranRepository;
    private final TransactionTemplate erpTxTemplate;
    private final TransactionTemplate erpReadOnlyTxTemplate;
    private final TransactionTemplate webTxTemplate;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String STAU_ORDER_PROGRESS = "4010020001";
    private static final String STAU_ORDER_SHIPPED = "4010030001";

    public AdminShipService(
            ShipMastRepository shipMastRepository,
            ShipTranRepository shipTranRepository,
            ShipOrderRepository shipOrderRepository,
            OrderTranRepository erpOrderTranRepository,
            CustomerRepository customerRepository,
            WebOrderMastRepository webOrderMastRepository,
            WebOrderTranRepository webOrderTranRepository,
            @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager,
            @Qualifier("webTransactionManager") PlatformTransactionManager webTxManager) {
        this.shipMastRepository = shipMastRepository;
        this.shipTranRepository = shipTranRepository;
        this.shipOrderRepository = shipOrderRepository;
        this.erpOrderTranRepository = erpOrderTranRepository;
        this.customerRepository = customerRepository;
        this.webOrderMastRepository = webOrderMastRepository;
        this.webOrderTranRepository = webOrderTranRepository;
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
        this.erpReadOnlyTxTemplate = new TransactionTemplate(erpTxManager);
        this.erpReadOnlyTxTemplate.setReadOnly(true);
        this.webTxTemplate = new TransactionTemplate(webTxManager);
    }

    /**
     * 출하 의뢰 생성 (수주 기반)
     *
     * 1. 수주(ORDER) 상세 조회
     * 2. 출하 마스터(SHIP_MAST) 생성
     * 3. 출하 상세(SHIP_TRAN) 생성 (수주 상세 기반)
     * 4. 수주-출하 매핑(SHIP_ORDER) 생성
     * 5. 수주 상태 → 수주진행(4010020001)
     */
    public ShipDetailResponse createShipment(ShipCreateRequest request, String loginId) {
        log.info("===== 출하 의뢰 생성 시작 =====");
        log.info("수주: {}-{}-{}-{}", request.getOrderDate(), request.getOrderSosok(),
                request.getOrderUjcd(), request.getOrderAcno());

        String shipDate = request.getShipDate() != null ? request.getShipDate()
                : LocalDate.now().format(DATE_FMT);
        LocalDateTime now = LocalDateTime.now();

        Integer sosok = request.getOrderSosok();
        String ujcd = request.getOrderUjcd();

        // 1. ERP에서 수주 상세 조회
        List<OrderTran> orderTrans = erpReadOnlyTxTemplate.execute(status ->
                erpOrderTranRepository.findByOrderMastKey(
                        request.getOrderDate(), sosok, ujcd, request.getOrderAcno()));

        if (orderTrans == null || orderTrans.isEmpty()) {
            throw new IllegalArgumentException("출하할 수주 내역이 없습니다");
        }

        // WebOrderMast에서 거래처 가져오기
        WebOrderMast webOrder = webTxTemplate.execute(status -> {
            WebOrderMast.WebOrderMastId id = new WebOrderMast.WebOrderMastId(
                    request.getOrderDate(), sosok, ujcd, request.getOrderAcno());
            return webOrderMastRepository.findById(id).orElse(null);
        });

        Integer finalCust = webOrder != null ? webOrder.getOrderMastCust() : 0;
        String naddr = request.getNaddr() != null ? request.getNaddr() : "";
        String tdiv = request.getTdiv() != null ? request.getTdiv() : "";
        String remark = request.getRemark() != null ? request.getRemark() : "";

        // 2. ERP DB에 출하 데이터 생성
        final String finalShipDate = shipDate;
        final int[] createdAcno = {0};

        erpTxTemplate.executeWithoutResult(status -> {
            Integer maxAcno = shipMastRepository.findMaxAcno(finalShipDate, sosok, ujcd);
            Integer newAcno = (maxAcno != null ? maxAcno : 0) + 1;
            createdAcno[0] = newAcno;

            ShipMast shipMast = ShipMast.builder()
                    .shipMastDate(finalShipDate)
                    .shipMastSosok(sosok)
                    .shipMastUjcd(ujcd)
                    .shipMastAcno(newAcno)
                    .shipMastCust(finalCust)
                    .shipMastNaddr(naddr)
                    .shipMastSawon(0)
                    .shipMastTdiv(tdiv)
                    .shipMastProject(0)
                    .shipMastCarno("")
                    .shipMastRemark(remark)
                    .shipMastFdate(now)
                    .shipMastFuser(loginId)
                    .shipMastLdate(now)
                    .shipMastLuser(loginId)
                    .build();
            shipMastRepository.save(shipMast);
            log.info("[ERP DB] 출하 마스터 생성 - shipKey: {}", shipMast.getShipKey());

            List<ShipTran> shipTrans = new ArrayList<>();
            List<ShipOrder> shipOrders = new ArrayList<>();

            int seq = 1;
            for (OrderTran ot : orderTrans) {
                BigDecimal zero = BigDecimal.ZERO;

                ShipTran shipTran = ShipTran.builder()
                        .shipTranDate(finalShipDate)
                        .shipTranSosok(sosok)
                        .shipTranUjcd(ujcd)
                        .shipTranAcno(newAcno)
                        .shipTranSeq(seq)
                        .shipTranDcod("")
                        .shipTranItem(ot.getOrderTranItem())
                        .shipTranDeta(ot.getOrderTranDeta())
                        .shipTranSpec(ot.getOrderTranSpec())
                        .shipTranUnit(ot.getOrderTranUnit())
                        .shipTranCalc(ot.getOrderTranCalc())
                        .shipTranVdiv(ot.getOrderTranVdiv())
                        .shipTranAdiv(ot.getOrderTranAdiv())
                        .shipTranRate(ot.getOrderTranRate())
                        .shipTranDcPer(ot.getOrderTranDcPer())
                        .shipTranDcAmt(ot.getOrderTranDcAmt())
                        .shipTranAmt(ot.getOrderTranAmt())
                        .shipTranCnt(ot.getOrderTranCnt())
                        .shipTranNet(ot.getOrderTranNet())
                        .shipTranVat(ot.getOrderTranVat())
                        .shipTranAdv(ot.getOrderTranAdv())
                        .shipTranTot(ot.getOrderTranTot())
                        .shipTranCheck(zero)
                        .shipTranOcnt(ot.getOrderTranCnt())
                        .shipTranLrate(ot.getOrderTranLrate())
                        .shipTranPrice(ot.getOrderTranPrice())
                        .shipTranPrice2(ot.getOrderTranPrice2())
                        .shipTranLdiv(ot.getOrderTranLdiv())
                        .shipTranRemark(ot.getOrderTranRemark())
                        .shipTranFdate(now)
                        .shipTranFuser(loginId)
                        .shipTranLdate(now)
                        .shipTranLuser(loginId)
                        .build();
                shipTrans.add(shipTran);

                ShipOrder shipOrder = ShipOrder.builder()
                        .shipOrderDate(finalShipDate)
                        .shipOrderSosok(sosok)
                        .shipOrderUjcd(ujcd)
                        .shipOrderAcno(newAcno)
                        .shipOrderSeq(seq)
                        .shipOrderOdate(request.getOrderDate())
                        .shipOrderOacno(request.getOrderAcno())
                        .shipOrderOseq(ot.getOrderTranSeq())
                        .build();
                shipOrders.add(shipOrder);

                seq++;
            }

            shipTranRepository.saveAll(shipTrans);
            log.info("[ERP DB] 출하 상세 {} 건 생성", shipTrans.size());

            shipOrderRepository.saveAll(shipOrders);
            log.info("[ERP DB] 수주-출하 매핑 {} 건 생성", shipOrders.size());

            // 수주 상태 → 수주진행 (TX 안에서 fresh 조회)
            List<OrderTran> freshOrderTrans = erpOrderTranRepository.findByOrderMastKey(
                    request.getOrderDate(), sosok, ujcd, request.getOrderAcno());
            for (OrderTran ot : freshOrderTrans) {
                ot.updateStau(STAU_ORDER_PROGRESS, loginId);
            }
            erpOrderTranRepository.saveAll(freshOrderTrans);
            log.info("[ERP DB] 수주 상태 → 수주진행(4010020001)");
        });

        // 3. Web DB 상태 동기화 (실패 시 ERP 보상 롤백)
        try {
            webTxTemplate.executeWithoutResult(status -> {
                // Web DB TX 안에서 fresh 조회
                WebOrderMast.WebOrderMastId webMastId = new WebOrderMast.WebOrderMastId(
                        request.getOrderDate(), sosok, ujcd, request.getOrderAcno());
                webOrderMastRepository.findById(webMastId).ifPresent(freshWebOrder -> {
                    freshWebOrder.updateStatus("SHIPPING", loginId);
                    webOrderMastRepository.save(freshWebOrder);
                });

                List<WebOrderTran> webTrans = webOrderTranRepository
                        .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                request.getOrderDate(), sosok, ujcd, request.getOrderAcno());
                for (WebOrderTran wt : webTrans) {
                    wt.updateStau(STAU_ORDER_PROGRESS, loginId);
                }
                webOrderTranRepository.saveAll(webTrans);
                log.info("[Web DB] 주문 상태 → SHIPPING / STAU 수주진행");
            });
        } catch (Exception e) {
            log.error("[Web DB] 상태 동기화 실패! ERP 보상 롤백 시작", e);
            try {
                final String orderDate = request.getOrderDate();
                final Integer orderAcno = request.getOrderAcno();

                erpTxTemplate.executeWithoutResult(status -> {
                    // 수주 STAU 원복 → 수주등록 (DB에서 fresh 조회)
                    List<OrderTran> freshOrderTrans = erpOrderTranRepository.findByOrderMastKey(
                            orderDate, sosok, ujcd, orderAcno);
                    for (OrderTran ot : freshOrderTrans) {
                        ot.updateStau("4010010001", loginId);
                    }
                    erpOrderTranRepository.saveAll(freshOrderTrans);

                    // 출하 데이터 삭제 (순서: ShipOrder → ShipTran → ShipMast)
                    List<ShipOrder> shipOrders = shipOrderRepository.findByShipKey(
                            finalShipDate, sosok, ujcd, createdAcno[0]);
                    shipOrderRepository.deleteAll(shipOrders);

                    List<ShipTran> shipTrans = shipTranRepository.findByShipKey(
                            finalShipDate, sosok, ujcd, createdAcno[0]);
                    shipTranRepository.deleteAll(shipTrans);

                    ShipMast.ShipMastId mastId = new ShipMast.ShipMastId(
                            finalShipDate, sosok, ujcd, createdAcno[0]);
                    shipMastRepository.deleteById(mastId);

                    log.info("[ERP DB] 보상 롤백 완료 - shipAcno: {}", createdAcno[0]);
                });
            } catch (Exception rollbackEx) {
                log.error("[ERP DB] 보상 롤백도 실패! 수동 확인 필요 - shipDate: {}, acno: {}",
                        finalShipDate, createdAcno[0], rollbackEx);
            }
            throw new RuntimeException("Web DB 동기화에 실패했습니다. 출하가 취소되었습니다.", e);
        }

        log.info("===== 출하 의뢰 생성 완료 =====");

        return getShipmentByOrder(request.getOrderDate(), sosok, ujcd, request.getOrderAcno());
    }

    /**
     * 출하 완료 처리
     * - 수주 상태 → 출하완료(4010030001)
     * - Web 상태 → DELIVERED
     */
    public ShipDetailResponse completeShipment(String shipDate, Integer sosok, String ujcd,
                                                 Integer acno, String loginId) {
        log.info("출하 완료 처리 - {}-{}-{}-{}", shipDate, sosok, ujcd, acno);

        // 1. 출하에 연결된 수주-출하 매핑 전체 조회
        List<ShipOrder> allShipOrders = erpReadOnlyTxTemplate.execute(status ->
                shipOrderRepository.findByShipKey(shipDate, sosok, ujcd, acno));

        // 2. ERP DB: 출하 상태 → 출하완료 + 수주 상태 → 출하완료
        if (allShipOrders != null && !allShipOrders.isEmpty()) {
            ShipOrder firstOrder = allShipOrders.get(0);
            String orderDate = firstOrder.getShipOrderOdate();
            Integer orderAcno = firstOrder.getShipOrderOacno();

            erpTxTemplate.executeWithoutResult(status -> {
                // 출하 상세 수정자 업데이트
                List<ShipTran> shipTrans = shipTranRepository.findByShipKey(shipDate, sosok, ujcd, acno);
                for (ShipTran st : shipTrans) {
                    st.updateLuser(loginId);
                }
                shipTranRepository.saveAll(shipTrans);
                log.info("[ERP DB] 출하 완료 처리");

                // 수주 상태 → 출하완료
                List<OrderTran> orderTrans = erpOrderTranRepository.findByOrderMastKey(
                        orderDate, sosok, ujcd, orderAcno);
                for (OrderTran ot : orderTrans) {
                    ot.updateStau(STAU_ORDER_SHIPPED, loginId);
                }
                erpOrderTranRepository.saveAll(orderTrans);
                log.info("[ERP DB] 수주 상태 → 출하완료(4010030001)");
            });

            // 3. Web DB 상태 동기화
            webTxTemplate.executeWithoutResult(status -> {
                WebOrderMast.WebOrderMastId webId = new WebOrderMast.WebOrderMastId(
                        orderDate, sosok, ujcd, orderAcno);
                webOrderMastRepository.findById(webId).ifPresent(webMast -> {
                    webMast.updateStatus("DELIVERED", loginId);
                    webOrderMastRepository.save(webMast);
                });

                List<WebOrderTran> webTrans = webOrderTranRepository
                        .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                                orderDate, sosok, ujcd, orderAcno);
                for (WebOrderTran wt : webTrans) {
                    wt.updateStau(STAU_ORDER_SHIPPED, loginId);
                }
                webOrderTranRepository.saveAll(webTrans);
                log.info("[Web DB] 주문 상태 → DELIVERED / STAU 출하완료");
            });
        }

        return getShipDetail(shipDate, sosok, ujcd, acno);
    }

    /**
     * 출하 목록 조회
     */
    public List<ShipDetailResponse> getShipList(String startDate, String endDate) {
        log.info("출하 목록 조회 - {} ~ {}", startDate, endDate);

        List<ShipMast> ships = erpReadOnlyTxTemplate.execute(status -> {
            if (startDate != null && endDate != null) {
                return shipMastRepository.findByDateRange(startDate, endDate);
            } else {
                String today = LocalDate.now().format(DATE_FMT);
                return shipMastRepository.findByShipMastDateOrderByShipMastAcnoDesc(today);
            }
        });

        return ships.stream()
                .map(sm -> {
                    String custName = getCustName(sm.getShipMastCust());
                    List<ShipTran> trans = erpReadOnlyTxTemplate.execute(status ->
                            shipTranRepository.findByShipKey(
                                    sm.getShipMastDate(), sm.getShipMastSosok(),
                                    sm.getShipMastUjcd(), sm.getShipMastAcno()));

                    List<ShipTranResponse> tranResps = trans.stream()
                            .map(ShipTranResponse::from)
                            .collect(Collectors.toList());

                    String orderKey = getOrderKeyFromShip(sm);
                    return ShipDetailResponse.from(sm, custName, orderKey, tranResps);
                })
                .collect(Collectors.toList());
    }

    /**
     * 출하 상세 조회
     */
    public ShipDetailResponse getShipDetail(String date, Integer sosok, String ujcd, Integer acno) {
        log.info("출하 상세 조회 - {}-{}-{}-{}", date, sosok, ujcd, acno);

        ShipMast mast = erpReadOnlyTxTemplate.execute(status -> {
            ShipMast.ShipMastId id = new ShipMast.ShipMastId(date, sosok, ujcd, acno);
            return shipMastRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출하입니다"));
        });

        List<ShipTran> trans = erpReadOnlyTxTemplate.execute(status ->
                shipTranRepository.findByShipKey(date, sosok, ujcd, acno));

        List<ShipTranResponse> tranResps = trans.stream()
                .map(ShipTranResponse::from)
                .collect(Collectors.toList());

        String custName = getCustName(mast.getShipMastCust());
        String orderKey = getOrderKeyFromShip(mast);

        return ShipDetailResponse.from(mast, custName, orderKey, tranResps);
    }

    /**
     * 수주번호로 출하 조회
     */
    public ShipDetailResponse getShipmentByOrder(String orderDate, Integer sosok, String ujcd, Integer orderAcno) {
        List<ShipOrder> shipOrders = erpReadOnlyTxTemplate.execute(status ->
                shipOrderRepository.findByOrderKey(orderDate, sosok, ujcd, orderAcno));

        if (shipOrders == null || shipOrders.isEmpty()) {
            return null;
        }

        ShipOrder first = shipOrders.get(0);
        return getShipDetail(first.getShipOrderDate(), sosok, ujcd, first.getShipOrderAcno());
    }

    // ========== Private Helpers ==========

    private String getCustName(Integer custCode) {
        if (custCode == null || custCode == 0) return "";
        try {
            return erpReadOnlyTxTemplate.execute(status ->
                    customerRepository.findById(custCode)
                            .map(Customer::getDisplayName)
                            .orElse(""));
        } catch (Exception e) {
            return "";
        }
    }

    private String getOrderKeyFromShip(ShipMast sm) {
        try {
            List<ShipOrder> orders = erpReadOnlyTxTemplate.execute(status ->
                    shipOrderRepository.findByShipKeyAndSeq(
                            sm.getShipMastDate(), sm.getShipMastSosok(),
                            sm.getShipMastUjcd(), sm.getShipMastAcno(), 1));

            if (orders != null && !orders.isEmpty()) {
                ShipOrder so = orders.get(0);
                return so.getShipOrderOdate() + "-" + sm.getShipMastSosok() + "-"
                        + sm.getShipMastUjcd() + "-" + so.getShipOrderOacno();
            }
        } catch (Exception e) {
            log.warn("수주키 조회 실패 - shipKey: {}", sm.getShipKey());
        }
        return "";
    }
}
