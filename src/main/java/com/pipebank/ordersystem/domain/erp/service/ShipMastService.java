package com.pipebank.ordersystem.domain.erp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.erp.dto.OrderShipmentDetailResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipMastListResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipListResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipSummaryResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipmentDetailResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipmentItemResponse;
import com.pipebank.ordersystem.domain.erp.entity.OrderMast;
import com.pipebank.ordersystem.domain.erp.entity.OrderTran;
import com.pipebank.ordersystem.domain.erp.entity.ShipMast;
import com.pipebank.ordersystem.domain.erp.entity.ShipOrder;
import com.pipebank.ordersystem.domain.erp.entity.ShipTran;
import com.pipebank.ordersystem.domain.erp.repository.CustomerRepository;
import com.pipebank.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.pipebank.ordersystem.domain.erp.repository.OrderTranRepository;
import com.pipebank.ordersystem.domain.erp.repository.ShipMastRepository;
import com.pipebank.ordersystem.domain.erp.repository.ShipOrderRepository;
import com.pipebank.ordersystem.domain.erp.repository.ShipTranRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShipMastService {

    private final ShipMastRepository shipMastRepository;
    private final ShipTranRepository shipTranRepository;
    private final ShipOrderRepository shipOrderRepository;
    private final OrderTranRepository orderTranRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final CustomerRepository customerRepository;
    private final CommonCodeService commonCodeService;

    /**
     * 거래처별 출하 목록 조회 (페이징 + 필터링)
     */
    public Page<ShipMastListResponse> getShipMastsByCustomerWithFilters(
            Integer custId, String shipDate, String startDate, String endDate, 
            String shipNumber, String sdiv, String comName, Pageable pageable) {
        
        log.info("거래처별 출하 목록 조회 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, shipNumber={}, sdiv={}, comName={}", 
                custId, shipDate, startDate, endDate, shipNumber, sdiv, comName);

        Page<Object[]> results = shipMastRepository.findShipMastWithOrderMastByCustomerWithFilters(
                custId, shipDate, startDate, endDate, shipNumber, sdiv, comName, pageable);

        return results.map(this::convertToShipMastListResponse);
    }

    /**
     * Object[] 결과를 ShipMastListResponse로 변환
     */
    private ShipMastListResponse convertToShipMastListResponse(Object[] result) {
        ShipMast shipMast = (ShipMast) result[0];
        OrderMast orderMast = (OrderMast) result[1];

        // 출하번호 생성
        String shipNumber = shipMast.getShipMastDate() + "-" + shipMast.getShipMastAcno();

        // 출고형태명 조회
        String orderMastSdivDisplayName = "";
        if (orderMast.getOrderMastSdiv() != null) {
            orderMastSdivDisplayName = commonCodeService.getDisplayNameByCode(orderMast.getOrderMastSdiv());
        }

        // 상태 계산
        String status = calculateShipStatus(shipMast);
        String statusDisplayName = getStatusDisplayName(status);

        // 거래처명 조회
        String customerName = "";
        try {
            customerName = customerRepository.findById(shipMast.getShipMastCust())
                    .map(customer -> customer.getCustCodeName())
                    .orElse("");
        } catch (Exception e) {
            log.warn("거래처명 조회 실패: {}", shipMast.getShipMastCust());
        }

        return ShipMastListResponse.builder()
                .shipNumber(shipNumber)
                .shipOrderDate(shipMast.getShipMastDate())
                .shipMastCust(shipMast.getShipMastCust())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(orderMastSdivDisplayName)
                .shipMastComname(shipMast.getShipMastComname())
                .orderMastOdate(orderMast.getOrderMastOdate())
                .status(status)
                .statusDisplayName(statusDisplayName)
                .customerName(customerName)
                .shipMastRemark(shipMast.getShipMastRemark())
                .build();
    }

    /**
     * ShipTran 상태값을 기반으로 출하 상태 계산
     */
    private String calculateShipStatus(ShipMast shipMast) {
        List<String> shipTranStatuses = shipTranRepository.findShipTranStatusByShipKey(
                shipMast.getShipMastDate(),
                shipMast.getShipMastSosok(),
                shipMast.getShipMastUjcd(),
                shipMast.getShipMastAcno()
        );

        if (shipTranStatuses.isEmpty()) {
            return "5380010001"; // 출하등록
        }

        // 모든 상태가 매출확정이면 매출확정
        boolean allConfirmed = shipTranStatuses.stream()
                .allMatch(status -> "5380030001".equals(status));
        if (allConfirmed) {
            return "5380030001"; // 매출확정
        }

        // 모든 상태가 출하완료 이상이면 출하완료
        boolean allCompleted = shipTranStatuses.stream()
                .allMatch(status -> "5380010002".equals(status) || "5380030001".equals(status));
        if (allCompleted) {
            return "5380010002"; // 출하완료
        }

        // 하나라도 진행중이면 출하진행
        boolean anyInProgress = shipTranStatuses.stream()
                .anyMatch(status -> "5380020001".equals(status) || "5380010002".equals(status) || "5380030001".equals(status));
        if (anyInProgress) {
            return "5380020001"; // 출하진행
        }

        return "5380010001"; // 출하등록
    }

    /**
     * 상태 코드를 표시명으로 변환
     */
    private String getStatusDisplayName(String status) {
        return switch (status) {
            case "5380010001" -> "출하등록";
            case "5380010002" -> "출하완료";
            case "5380020001" -> "출하진행";
            case "5380030001" -> "매출확정";
            default -> "알 수 없음";
        };
    }

    /**
     * 출하번호별 출고현황 조회
     */
    public List<ShipmentDetailResponse> getShipmentDetail(String shipNumber) {
        log.info("출고현황 조회 요청 - 출하번호: {}", shipNumber);

        // 출하번호를 DATE와 ACNO로 분리
        String[] parts = shipNumber.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 출하번호 형식입니다. 올바른 형식: YYYYMMDD-숫자 (예: 20240101-1)");
        }

        String shipDate = parts[0];
        Integer shipAcno;
        try {
            shipAcno = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 출하번호 형식입니다. ACNO는 숫자여야 합니다.");
        }

        // ShipMast 조회하여 소속, 업장 정보 가져오기
        List<ShipMast> shipMasts = shipMastRepository.findByShipMastDateAndShipMastAcno(shipDate, shipAcno);
        if (shipMasts.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 출하번호입니다: " + shipNumber);
        }

        ShipMast shipMast = shipMasts.get(0); // 첫 번째 결과 사용

        // ShipTran 조회
        List<ShipTran> shipTrans = shipTranRepository.findByShipKey(
                shipMast.getShipMastDate(),
                shipMast.getShipMastSosok(), 
                shipMast.getShipMastUjcd(),
                shipMast.getShipMastAcno()
        );

        // ShipTran을 ShipmentDetailResponse로 변환
        List<ShipmentDetailResponse> responses = shipTrans.stream()
                .map(shipTran -> convertToShipmentDetailResponse(shipTran, shipNumber, shipMast))
                .toList();

        log.info("출고현황 조회 완료 - 출하번호: {}, 건수: {}", shipNumber, responses.size());
        return responses;
    }

    /**
     * ShipTran을 ShipmentDetailResponse로 변환
     */
    private ShipmentDetailResponse convertToShipmentDetailResponse(ShipTran shipTran, String shipNumber, ShipMast shipMast) {
        // 제품코드 조회
        String itemCodeNum = "";
        if (shipTran.getShipTranItem() != null) {
            try {
                itemCodeNum = itemCodeRepository.findById(shipTran.getShipTranItem())
                        .map(itemCode -> itemCode.getItemCodeNum())
                        .orElse("");
            } catch (Exception e) {
                log.warn("제품코드 조회 실패 - shipTranItem: {}", shipTran.getShipTranItem(), e);
            }
        }

        // 주문량 조회: ShipOrder를 통해 OrderTran 찾기
        BigDecimal orderQuantity = getOrderQuantity(shipTran, shipMast);

        return ShipmentDetailResponse.fromShipTran(shipTran, shipNumber, itemCodeNum, orderQuantity);
    }

    /**
     * ShipTran에 해당하는 OrderTran의 주문량 조회
     */
    private BigDecimal getOrderQuantity(ShipTran shipTran, ShipMast shipMast) {
        try {
            // ShipOrder를 통해 해당 ShipTran에 매핑된 OrderTran 찾기
            List<ShipOrder> shipOrders = shipOrderRepository.findByShipKeyAndSeq(
                    shipMast.getShipMastDate(),
                    shipMast.getShipMastSosok(),
                    shipMast.getShipMastUjcd(), 
                    shipMast.getShipMastAcno(),
                    shipTran.getShipTranSeq()
            );

            if (!shipOrders.isEmpty()) {
                ShipOrder shipOrder = shipOrders.get(0);
                
                // OrderTran 조회
                List<OrderTran> orderTrans = orderTranRepository.findByOrderMastKeyAndSeq(
                        shipOrder.getShipOrderOdate(),
                        shipOrder.getShipOrderSosok(),
                        shipOrder.getShipOrderUjcd(),
                        shipOrder.getShipOrderOacno(),
                        shipOrder.getShipOrderOseq()
                );

                if (!orderTrans.isEmpty()) {
                    OrderTran orderTran = orderTrans.get(0);
                    return orderTran.getOrderTranCnt() != null ? orderTran.getOrderTranCnt() : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            log.warn("주문량 조회 실패 - ShipTran: {}", shipTran.getShipTranSeq(), e);
        }

        return BigDecimal.ZERO; // 매핑된 주문이 없으면 0
    }

    /**
     * 전표번호별 출고전표현황 조회 (합계 포함)
     */
    public ShipSlipSummaryResponse getShipSlipDetail(String slipNumber) {
        log.info("출고전표현황 조회 요청 - 전표번호: {}", slipNumber);

        // 전표번호를 DATE와 ACNO로 분리
        String[] parts = slipNumber.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 전표번호 형식입니다. 올바른 형식: YYYYMMDD-숫자 (예: 20240101-1)");
        }

        String slipDate = parts[0];
        Integer slipAcno;
        try {
            slipAcno = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 전표번호 형식입니다. ACNO는 숫자여야 합니다.");
        }

        // ShipTran 조회
        List<ShipTran> shipTrans = shipTranRepository.findBySlipNumber(slipDate, slipAcno);

        if (shipTrans.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 전표번호입니다: " + slipNumber);
        }

        // ShipTran을 ShipSlipResponse로 변환
        List<ShipSlipResponse> responses = shipTrans.stream()
                .map(ShipSlipResponse::fromShipTran)
                .collect(Collectors.toList());

        // 합계 정보를 포함한 Response 생성
        ShipSlipSummaryResponse summaryResponse = ShipSlipSummaryResponse.fromDetails(slipNumber, responses);

        log.info("출고전표현황 조회 완료 - 전표번호: {}, 건수: {}, 합계금액: {}", 
                slipNumber, responses.size(), summaryResponse.getTotalAmount());
        return summaryResponse;
    }

    /**
     * 거래처별 출고전표 목록 조회 (페이징 + 필터링)
     */
    public Page<ShipSlipListResponse> getShipSlipListByCustomer(
            Integer custId, String shipDate, String startDate, String endDate, 
            String orderNumber, String shipNumber, String comName, Pageable pageable) {
        
        log.info("거래처별 출고전표 목록 조회 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, orderNumber={}, shipNumber={}, comName={}", 
                custId, shipDate, startDate, endDate, orderNumber, shipNumber, comName);

        // 전체 데이터 조회
        List<Object[]> allResults = shipMastRepository.findShipSlipListByCustomerWithFiltersNative(
                custId, shipDate, startDate, endDate, orderNumber, shipNumber, comName);

        // 출하번호별로 중복 제거 (LinkedHashMap으로 순서 보장)
        Map<String, Object[]> uniqueResults = new LinkedHashMap<>();
        for (Object[] result : allResults) {
            ShipMast shipMast = (ShipMast) result[0];
            String shipKey = makeShipKey(shipMast);
            if (!uniqueResults.containsKey(shipKey)) {
                uniqueResults.put(shipKey, result);
            }
        }

        // List로 변환
        List<Object[]> deduplicatedResults = new ArrayList<>(uniqueResults.values());

        // 배치로 출고금액 계산
        Map<String, BigDecimal> amountMap = calculateBatchShipAmounts(deduplicatedResults);

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), deduplicatedResults.size());
        List<Object[]> pageResults = deduplicatedResults.subList(start, end);

        // 변환
        List<ShipSlipListResponse> responses = pageResults.stream()
                .map(result -> convertToShipSlipListResponse(result, amountMap))
                .collect(Collectors.toList());

        // Page 객체 생성
        Page<ShipSlipListResponse> page = new PageImpl<>(responses, pageable, deduplicatedResults.size());

        log.info("거래처별 출고전표 목록 조회 완료 - 총 {}건 (중복제거 후: {}건)", allResults.size(), deduplicatedResults.size());
        return page;
    }

    /**
     * Object[] 결과를 ShipSlipListResponse로 변환
     */
    private ShipSlipListResponse convertToShipSlipListResponse(Object[] result, Map<String, BigDecimal> amountMap) {
        ShipMast shipMast = (ShipMast) result[0];
        OrderMast orderMast = (OrderMast) result[1];

        // 주문번호 및 출하번호 생성
        String orderNumber = orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno();
        String shipNumber = shipMast.getShipMastDate() + "-" + shipMast.getShipMastAcno();
        String shipKey = makeShipKey(shipMast);

        // 출고금액 조회
        BigDecimal totalAmount = amountMap.getOrDefault(shipKey, BigDecimal.ZERO);

        // 거래처명 조회
        String customerName = "";
        try {
            customerName = customerRepository.findById(shipMast.getShipMastCust())
                    .map(customer -> customer.getCustCodeName())
                    .orElse("");
        } catch (Exception e) {
            log.warn("거래처명 조회 실패: {}", shipMast.getShipMastCust());
        }

        return ShipSlipListResponse.builder()
                .orderNumber(orderNumber)
                .shipNumber(shipNumber)
                .shipMastComname(shipMast.getShipMastComname())
                .shipMastDate(shipMast.getShipMastDate())
                .totalAmount(totalAmount)
                .shipMastAcno(shipMast.getShipMastAcno())
                .customerName(customerName)
                .build();
    }

    /**
     * 여러 출하의 출고금액을 배치로 계산
     */
    private Map<String, BigDecimal> calculateBatchShipAmounts(List<Object[]> results) {
        Map<String, BigDecimal> amountMap = new HashMap<>();

        for (Object[] result : results) {
            ShipMast shipMast = (ShipMast) result[0];
            String shipKey = makeShipKey(shipMast);

            try {
                // ShipTran들의 금액 합계 조회
                List<ShipTran> shipTrans = shipTranRepository.findByShipKey(
                        shipMast.getShipMastDate(),
                        shipMast.getShipMastSosok(),
                        shipMast.getShipMastUjcd(),
                        shipMast.getShipMastAcno()
                );

                BigDecimal totalAmount = shipTrans.stream()
                        .map(ShipTran::getShipTranTot)
                        .filter(tot -> tot != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                amountMap.put(shipKey, totalAmount);
            } catch (Exception e) {
                log.warn("출고금액 계산 실패 - shipKey: {}", shipKey, e);
                amountMap.put(shipKey, BigDecimal.ZERO);
            }
        }

        return amountMap;
    }

    /**
     * 거래처별 현장별 출하조회 (ShipTran 단위) - 페이징 + 필터링
     * 🔥 고급 검색: 제품명1 AND/OR 제품명2, 규격1 AND/OR 규격2 지원
     */
    public Page<ShipmentItemResponse> getShipmentItemsByCustomer(
            Integer custId, String shipDate, String startDate, String endDate,
            String shipNumber, String orderNumber, String itemName1, String itemName2,
            String spec1, String spec2, String itemNameOperator, String specOperator,
            String comName, Pageable pageable) {
        
        log.info("거래처별 현장별 출하조회 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, shipNumber={}, orderNumber={}, itemName1={}, itemName2={}, spec1={}, spec2={}, itemNameOp={}, specOp={}, comName={}", 
                custId, shipDate, startDate, endDate, shipNumber, orderNumber, itemName1, itemName2, spec1, spec2, itemNameOperator, specOperator, comName);

        // 새로운 Repository 메서드 호출
        Page<Object[]> shipmentData = shipMastRepository.findShipmentItemsByCustomerWithFilters(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, itemNameOperator, specOperator,
                comName, pageable);

        // Object[] 결과를 ShipmentItemResponse로 변환
        Page<ShipmentItemResponse> responses = shipmentData.map(this::convertToShipmentItemResponse);

        log.info("거래처별 현장별 출하조회 완료 - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 🔥 하위호환성을 위한 기존 메서드 (단일 itemName만 지원)
     */
    public Page<ShipmentItemResponse> getShipmentItemsByCustomer(
            Integer custId, String shipDate, String startDate, String endDate,
            String shipNumber, String orderNumber, String itemName, String comName, Pageable pageable) {
        
        // 기존 단일 itemName을 itemName1로 매핑하여 새 메서드 호출
        return getShipmentItemsByCustomer(custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName, null, null, null, "AND", "AND", comName, pageable);
    }

    /**
     * Object[] 결과를 ShipmentItemResponse로 변환
     */
    private ShipmentItemResponse convertToShipmentItemResponse(Object[] result) {
        ShipMast shipMast = (ShipMast) result[0];
        ShipTran shipTran = (ShipTran) result[1];
        ShipOrder shipOrder = (ShipOrder) result[2]; // 쿼리에서 조인된 ShipOrder (null 가능)
        OrderMast orderMast = (OrderMast) result[3]; // 쿼리에서 조인된 OrderMast (null 가능)

        // 출하번호 생성
        String shipNumber = shipMast.getShipMastDate() + "-" + shipMast.getShipMastAcno();

        // 주문번호 생성 (OrderMast가 조회된 경우)
        String orderNumber = "";
        if (orderMast != null) {
            orderNumber = orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno();
        }

        // 🔥 차량톤수 표시명 조회 (CommonCode3에서)
        String cartonDisplayName = "";
        if (shipMast.getShipMastCarton() != null && !shipMast.getShipMastCarton().trim().isEmpty()) {
            try {
                cartonDisplayName = commonCodeService.getDisplayNameByCode(shipMast.getShipMastCarton());
            } catch (Exception e) {
                log.warn("차량톤수 코드 조회 실패: {}", shipMast.getShipMastCarton(), e);
            }
        }

        return ShipmentItemResponse.builder()
                .shipMastComname(shipMast.getShipMastComname())
                .shipNumber(shipNumber)
                .orderNumber(orderNumber)
                .shipTranDeta(shipTran.getShipTranDeta())
                .shipTranSpec(shipTran.getShipTranSpec())
                .shipTranUnit(shipTran.getShipTranUnit())
                .shipTranDate(shipTran.getShipTranDate())
                .shipTranCnt(shipTran.getShipTranCnt())
                .shipTranTot(shipTran.getShipTranTot())
                // 🔥 새로 추가된 운송 관련 정보
                .shipMastCarno(shipMast.getShipMastCarno())
                .shipMastTname(shipMast.getShipMastTname())
                .shipMastTtel(shipMast.getShipMastTtel())
                .shipMastCarton(shipMast.getShipMastCarton())
                .shipMastCartonDisplayName(cartonDisplayName)
                // 기존 추가 정보
                .shipMastCust(shipMast.getShipMastCust())
                .shipTranSeq(shipTran.getShipTranSeq())
                .build();
    }

    /**
     * ShipMast 키를 문자열로 변환
     */
    private String makeShipKey(ShipMast shipMast) {
        return String.format("%s-%d-%s-%d", 
                shipMast.getShipMastDate(), 
                shipMast.getShipMastSosok(), 
                shipMast.getShipMastUjcd(), 
                shipMast.getShipMastAcno());
    }

    /**
     * 🔥 주문-출하 통합 상세 조회 (페이징 + 2중 필터링)
     * OrderMast + OrderTran + ItemCode + ShipTran 통합 조회
     * 
     * @param custId 거래처ID (ORDER_MAST_CUST 기준)
     * @param shipDate 출하일자 (정확 일치)
     * @param startDate 시작일자 (범위 조회)
     * @param endDate 종료일자 (범위 조회)
     * @param orderNumber 주문번호 (부분 검색)
     * @param itemName1 품명1 (부분 검색)
     * @param itemName2 품명2 (부분 검색)
     * @param spec1 규격1 (부분 검색)
     * @param spec2 규격2 (부분 검색)
     * @param itemNameOperator 품명 검색 연산자 (AND/OR)
     * @param specOperator 규격 검색 연산자 (AND/OR)
     * @param siteName 현장명 (부분 검색)
     * @param excludeCompleted 완료 내역 제외 여부
     * @param statusFilter 특정 상태만 조회
     * @param pageable 페이징 정보
     * @return 통합 조회 결과 (17개 필드)
     */
    public Page<OrderShipmentDetailResponse> getOrderShipmentDetailByCustomer(
            Integer custId, String shipDate, String startDate, String endDate, String orderNumber,
            String itemName1, String itemName2, String spec1, String spec2,
            String itemNameOperator, String specOperator, String siteName,
            boolean excludeCompleted, String statusFilter, Pageable pageable) {
        
        log.info("주문-출하 통합 상세 조회 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, orderNumber={}, itemName1={}, itemName2={}, spec1={}, spec2={}, itemNameOp={}, specOp={}, siteName={}, excludeCompleted={}, statusFilter={}", 
                custId, shipDate, startDate, endDate, orderNumber, itemName1, itemName2, spec1, spec2, itemNameOperator, specOperator, siteName, excludeCompleted, statusFilter);
        
        // Repository에서 복잡한 JOIN 쿼리 실행
        Page<Object[]> rawData = shipMastRepository.findOrderShipmentDetailByCustomer(
                custId, shipDate, startDate, endDate, orderNumber,
                itemName1, itemName2, spec1, spec2, itemNameOperator, specOperator, siteName,
                excludeCompleted, statusFilter, pageable);
        
        // Object[] 배열을 OrderShipmentDetailResponse로 변환
        Page<OrderShipmentDetailResponse> responses = rawData.map(OrderShipmentDetailResponse::from);
        
        log.info("주문-출하 통합 상세 조회 완료 - 총 {}건", responses.getTotalElements());
        return responses;
    }
}