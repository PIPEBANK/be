package com.pipebank.ordersystem.domain.erp.service;

import com.pipebank.ordersystem.domain.erp.dto.OrderMastResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderMastListResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderDetailResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderShipmentResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderTranDetailResponse;
import com.pipebank.ordersystem.domain.erp.entity.OrderMast;
import com.pipebank.ordersystem.domain.erp.entity.OrderTran;
import com.pipebank.ordersystem.domain.erp.entity.ShipOrder;
import com.pipebank.ordersystem.domain.erp.repository.OrderMastRepository;
import com.pipebank.ordersystem.domain.erp.repository.OrderTranRepository;
import com.pipebank.ordersystem.domain.erp.repository.ShipOrderRepository;
import com.pipebank.ordersystem.domain.erp.repository.ItemCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, transactionManager = "erpTransactionManager")
@Slf4j
public class OrderMastService {

    private final OrderMastRepository orderMastRepository;
    private final OrderTranRepository orderTranRepository;
    private final ShipOrderRepository shipOrderRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final CommonCodeService commonCodeService;
    private final CustomerService customerService;

    /**
     * 복합키로 주문 조회
     */
    public OrderMastResponse getOrderMast(String orderMastDate, Integer orderMastSosok, 
                                         String orderMastUjcd, Integer orderMastAcno) {
        log.info("주문 조회 요청 - 날짜: {}, 소속: {}, 업장: {}, 계정: {}", 
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        OrderMast orderMast = orderMastRepository.findByOrderMastDateAndOrderMastSosokAndOrderMastUjcdAndOrderMastAcno(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다: " + 
                        orderMastDate + "-" + orderMastSosok + "-" + orderMastUjcd + "-" + orderMastAcno));
        
        OrderMastResponse response = convertToResponse(orderMast);
        
        return response;
    }

    /**
     * 주문일자별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersByDate(String orderMastDate) {
        log.info("주문일자별 주문 목록 조회 요청 - 날짜: {}", orderMastDate);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastDateOrderByOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastDate);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문일자별 주문 목록 조회 완료 - 날짜: {}, 건수: {}", orderMastDate, responses.size());
        return responses;
    }

    /**
     * 주문일자 범위별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersByDateRange(String startDate, String endDate) {
        log.info("주문일자 범위별 주문 목록 조회 요청 - 시작: {}, 종료: {}", startDate, endDate);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastDateBetween(startDate, endDate);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문일자 범위별 주문 목록 조회 완료 - 시작: {}, 종료: {}, 건수: {}", startDate, endDate, responses.size());
        return responses;
    }

    /**
     * 주문일자 범위별 주문 목록 조회 (페이징)
     */
    public Page<OrderMastResponse> getOrdersByDateRange(String startDate, String endDate, Pageable pageable) {
        log.info("주문일자 범위별 주문 목록 조회 요청 (페이징) - 시작: {}, 종료: {}", startDate, endDate);
        
        Page<OrderMast> orders = orderMastRepository.findByOrderMastDateBetween(startDate, endDate, pageable);
        Page<OrderMastResponse> responses = orders.map(this::convertToResponse);
        
        log.info("주문일자 범위별 주문 목록 조회 완료 (페이징) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 거래처별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersByCustomer(Integer orderMastCust) {
        log.info("거래처별 주문 목록 조회 요청 - 거래처ID: {}", orderMastCust);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastCustOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastCust);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("거래처별 주문 목록 조회 완료 - 거래처ID: {}, 건수: {}", orderMastCust, responses.size());
        return responses;
    }

    /**
     * 거래처별 주문 목록 조회 (페이징)
     */
    public Page<OrderMastResponse> getOrdersByCustomer(Integer orderMastCust, Pageable pageable) {
        log.info("거래처별 주문 목록 조회 요청 (페이징) - 거래처ID: {}", orderMastCust);
        
        Page<OrderMast> orders = orderMastRepository.findByOrderMastCustOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastCust, pageable);
        Page<OrderMastResponse> responses = orders.map(this::convertToResponse);
        
        log.info("거래처별 주문 목록 조회 완료 (페이징) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 거래처별 주문 목록 조회 (페이징 + 필터링)
     */
    public Page<OrderMastResponse> getOrdersByCustomerWithFilters(Integer custId, String orderDate, 
                                                                 String startDate, String endDate,
                                                                 String orderNumber, String sdiv, String comName, 
                                                                 Pageable pageable) {
        log.info("거래처별 주문 목록 조회 요청 (필터링) - 거래처ID: {}, 주문일자: {}, 범위: {}-{}, 주문번호: {}, 출고형태: {}, 현장명: {}", 
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName);
        
        Page<OrderMast> orders = orderMastRepository.findByCustomerWithFilters(
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName, pageable);
        Page<OrderMastResponse> responses = orders.map(this::convertToResponse);
        
        log.info("거래처별 주문 목록 조회 완료 (필터링) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 거래처별 주문 목록 조회 (페이징) - 성능 최적화용
     */
    public Page<OrderMastListResponse> getOrdersByCustomerForList(Integer orderMastCust, Pageable pageable) {
        log.info("거래처별 주문 목록 조회 요청 (최적화, 페이징) - 거래처ID: {}", orderMastCust);
        
        Page<OrderMast> orders = orderMastRepository.findByOrderMastCustOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastCust, pageable);
        
        // 배치 상태 계산을 한 번만 수행
        Map<String, String> statusMap = calculateBatchStatusByCustomer(orderMastCust, orders.getContent());
        
        // 상태 정보를 포함한 변환
        Page<OrderMastListResponse> responses = orders.map(orderMast -> 
                convertToListResponseWithPreCalculatedStatus(orderMast, statusMap));
        
        log.info("거래처별 주문 목록 조회 완료 (최적화, 페이징) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 거래처별 주문 목록 조회 (페이징 + 필터링) - 성능 최적화용
     */
    public Page<OrderMastListResponse> getOrdersByCustomerWithFiltersForList(Integer custId, String orderDate, 
                                                                            String startDate, String endDate,
                                                                            String orderNumber, String sdiv, String comName, 
                                                                            Pageable pageable) {
        log.info("거래처별 주문 목록 조회 요청 (필터링 최적화) - 거래처ID: {}, 주문일자: {}, 범위: {}-{}, 주문번호: {}, 출고형태: {}, 현장명: {}", 
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName);
        
        Page<OrderMast> orders = orderMastRepository.findByCustomerWithFilters(
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName, pageable);
        
        // 배치 상태 계산을 한 번만 수행 (거래처별 간단한 방식 사용)
        Map<String, String> statusMap = calculateBatchStatusByCustomer(custId, orders.getContent());
        
        // 상태 정보를 포함한 변환
        Page<OrderMastListResponse> responses = orders.map(orderMast -> 
                convertToListResponseWithPreCalculatedStatus(orderMast, statusMap));
        
        log.info("거래처별 주문 목록 조회 완료 (필터링 최적화) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 담당자별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersBySawon(Integer orderMastSawon) {
        log.info("담당자별 주문 목록 조회 요청 - 담당자ID: {}", orderMastSawon);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastSawonOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastSawon);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("담당자별 주문 목록 조회 완료 - 담당자ID: {}, 건수: {}", orderMastSawon, responses.size());
        return responses;
    }

    /**
     * 담당자별 주문 목록 조회 (페이징)
     */
    public Page<OrderMastResponse> getOrdersBySawon(Integer orderMastSawon, Pageable pageable) {
        log.info("담당자별 주문 목록 조회 요청 (페이징) - 담당자ID: {}", orderMastSawon);
        
        Page<OrderMast> orders = orderMastRepository.findByOrderMastSawonOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastSawon, pageable);
        Page<OrderMastResponse> responses = orders.map(this::convertToResponse);
        
        log.info("담당자별 주문 목록 조회 완료 (페이징) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 소속별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersBySosok(Integer orderMastSosok) {
        log.info("소속별 주문 목록 조회 요청 - 소속ID: {}", orderMastSosok);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastSosokOrderByOrderMastDateDescOrderMastUjcdAscOrderMastAcnoAsc(orderMastSosok);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("소속별 주문 목록 조회 완료 - 소속ID: {}, 건수: {}", orderMastSosok, responses.size());
        return responses;
    }

    /**
     * 업장코드별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersByUjcd(String orderMastUjcd) {
        log.info("업장코드별 주문 목록 조회 요청 - 업장코드: {}", orderMastUjcd);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastUjcdOrderByOrderMastDateDescOrderMastSosokAscOrderMastAcnoAsc(orderMastUjcd);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("업장코드별 주문 목록 조회 완료 - 업장코드: {}, 건수: {}", orderMastUjcd, responses.size());
        return responses;
    }

    /**
     * 프로젝트별 주문 목록 조회
     */
    public List<OrderMastResponse> getOrdersByProject(Integer orderMastProject) {
        log.info("프로젝트별 주문 목록 조회 요청 - 프로젝트ID: {}", orderMastProject);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastProjectOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(orderMastProject);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("프로젝트별 주문 목록 조회 완료 - 프로젝트ID: {}, 건수: {}", orderMastProject, responses.size());
        return responses;
    }

    /**
     * 회사명으로 주문 검색
     */
    public List<OrderMastResponse> searchOrdersByCompanyName(String companyName) {
        log.info("회사명으로 주문 검색 요청 - 검색어: {}", companyName);
        
        List<OrderMast> orders = orderMastRepository.findByOrderMastComnameContaining(companyName);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("회사명으로 주문 검색 완료 - 검색어: {}, 건수: {}", companyName, responses.size());
        return responses;
    }

    /**
     * 복합 조건으로 주문 검색
     */
    public Page<OrderMastResponse> searchOrders(String startDate, String endDate, Integer orderMastCust,
                                               Integer orderMastSawon, Integer orderMastSosok, String orderMastUjcd,
                                               Integer orderMastProject, String companyName, Pageable pageable) {
        log.info("복합 조건으로 주문 검색 요청");
        
        Page<OrderMast> orders = orderMastRepository.findOrdersWithConditions(
                startDate, endDate, orderMastCust, orderMastSawon, orderMastSosok, 
                orderMastUjcd, orderMastProject, companyName, pageable);
        
        Page<OrderMastResponse> responses = orders.map(this::convertToResponse);
        
        log.info("복합 조건으로 주문 검색 완료 - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 최신 주문 목록 조회
     */
    public List<OrderMastResponse> getLatestOrders(int limit) {
        log.info("최신 주문 목록 조회 요청 - 제한: {}건", limit);
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<OrderMast> orders = orderMastRepository.findLatestOrders(pageable);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("최신 주문 목록 조회 완료 - 건수: {}", responses.size());
        return responses;
    }

    /**
     * 특정 거래처의 최신 주문 조회
     */
    public List<OrderMastResponse> getLatestOrdersByCustomer(Integer custId, int limit) {
        log.info("특정 거래처의 최신 주문 조회 요청 - 거래처ID: {}, 제한: {}건", custId, limit);
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<OrderMast> orders = orderMastRepository.findLatestOrdersByCustomer(custId, pageable);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("특정 거래처의 최신 주문 조회 완료 - 거래처ID: {}, 건수: {}", custId, responses.size());
        return responses;
    }

    /**
     * 특정 담당자의 최신 주문 조회
     */
    public List<OrderMastResponse> getLatestOrdersBySawon(Integer sawonId, int limit) {
        log.info("특정 담당자의 최신 주문 조회 요청 - 담당자ID: {}, 제한: {}건", sawonId, limit);
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<OrderMast> orders = orderMastRepository.findLatestOrdersBySawon(sawonId, pageable);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("특정 담당자의 최신 주문 조회 완료 - 담당자ID: {}, 건수: {}", sawonId, responses.size());
        return responses;
    }

    /**
     * 주문 통계 정보 조회
     */
    public OrderStatistics getOrderStatistics() {
        log.info("주문 통계 정보 조회 요청");
        
        long totalOrders = orderMastRepository.count();
        
        // 오늘 날짜 생성 (YYYYMMDD 형식)
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long todayOrders = orderMastRepository.countByOrderMastDate(today);
        
        // 이번 달 첫날과 마지막날
        java.time.LocalDate now = java.time.LocalDate.now();
        String monthStart = now.withDayOfMonth(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String monthEnd = now.withDayOfMonth(now.lengthOfMonth()).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long monthOrders = orderMastRepository.countByOrderMastDateBetween(monthStart, monthEnd);
        
        OrderStatistics statistics = OrderStatistics.builder()
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .monthOrders(monthOrders)
                .build();
        
        log.info("주문 통계 정보 조회 완료 - 전체: {}, 오늘: {}, 이번달: {}", totalOrders, todayOrders, monthOrders);
        return statistics;
    }

    /**
     * 주문번호로 주문 조회 (DATE + "-" + ACNO 형식)
     * 예: "20210101-1"
     */
    public List<OrderMastResponse> getOrdersByOrderNumber(String orderNumber) {
        log.info("주문번호로 주문 조회 요청 - 주문번호: {}", orderNumber);
        
        // 주문번호를 DATE와 ACNO로 분리
        String[] parts = orderNumber.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. 올바른 형식: YYYYMMDD-숫자 (예: 20210101-1)");
        }
        
        String orderDate = parts[0];
        Integer acno;
        try {
            acno = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. ACNO는 숫자여야 합니다.");
        }
        
        // 해당 날짜와 ACNO로 주문 조회
        List<OrderMast> orders = orderMastRepository.findByOrderMastDateAndOrderMastAcno(orderDate, acno);
        
        List<OrderMastResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문번호로 주문 조회 완료 - 주문번호: {}, 건수: {}", orderNumber, responses.size());
        return responses;
    }

    /**
     * 주문 상세조회 (OrderMast + OrderTran 정보)
     */
    public OrderDetailResponse getOrderDetail(String orderNumber) {
        log.info("주문 상세조회 요청 - 주문번호: {}", orderNumber);
        
        // 주문번호를 DATE와 ACNO로 분리
        String[] parts = orderNumber.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. 올바른 형식: YYYYMMDD-숫자 (예: 20240101-1)");
        }
        
        String orderDate = parts[0];
        Integer acno;
        try {
            acno = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. ACNO는 숫자여야 합니다.");
        }
        
        // OrderMast 조회 (첫 번째 매치되는 것)
        List<OrderMast> orderMasts = orderMastRepository.findByOrderMastDateAndOrderMastAcno(orderDate, acno);
        if (orderMasts.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 주문번호입니다: " + orderNumber);
        }
        
        OrderMast orderMast = orderMasts.get(0); // 첫 번째 결과 사용
        
        // OrderTran 조회 (동일한 복합키로)
        List<OrderTran> orderTrans = orderTranRepository.findByOrderMastKey(
                orderMast.getOrderMastDate(), 
                orderMast.getOrderMastSosok(), 
                orderMast.getOrderMastUjcd(), 
                orderMast.getOrderMastAcno()
        );
        
        // 기본 정보 변환
        OrderDetailResponse response = OrderDetailResponse.fromOrderMast(orderMast);
        
        // 코드 표시명 추가
        response = addDisplayNames(response, orderMast);
        
        // OrderTran 정보 추가
        if (!orderTrans.isEmpty()) {
            List<OrderTranDetailResponse> orderTranResponses = orderTrans.stream()
                    .map(this::convertOrderTranToDetailResponse)
                    .collect(Collectors.toList());
            
            // 총 금액 계산 (orderTranTot 합계)
            BigDecimal totalAmount = orderTranResponses.stream()
                    .map(OrderTranDetailResponse::getOrderTranTot)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            response = OrderDetailResponse.builder()
                    .orderNumber(response.getOrderNumber())
                    .orderMastDate(response.getOrderMastDate())
                    .orderMastSdiv(response.getOrderMastSdiv())
                    .orderMastSdivDisplayName(response.getOrderMastSdivDisplayName())
                    .orderMastOdate(response.getOrderMastOdate())
                    .orderMastOtime(response.getOrderMastOtime())
                    .orderMastDcust(response.getOrderMastDcust())
                    .orderMastComaddr(response.getOrderMastComaddr())
                    .orderMastComname(response.getOrderMastComname())
                    .orderMastCurrency(response.getOrderMastCurrency())
                    .orderMastCurrencyDisplayName(response.getOrderMastCurrencyDisplayName())
                    .orderMastCurrencyPer(response.getOrderMastCurrencyPer())
                    .orderMastReason(response.getOrderMastReason())
                    .orderMastReasonDisplayName(response.getOrderMastReasonDisplayName())
                    .orderMastComuname(response.getOrderMastComuname())
                    .orderMastComutel(response.getOrderMastComutel())
                    .orderMastRemark(response.getOrderMastRemark())
                    .orderTranList(orderTranResponses)
                    .orderTranTotalAmount(totalAmount)
                    .build();
        }
        
        log.info("주문 상세조회 완료 - 주문번호: {}, OrderTran 건수: {}", orderNumber, orderTrans.size());
        return response;
    }

    /**
     * 출하진행현황 조회 (페이징 + 필터링)
     */
    public Page<OrderShipmentResponse> getShipmentStatus(String orderDate, 
                                                        String startDate, String endDate,
                                                        String orderNumber, String sdiv, String comName, 
                                                        Pageable pageable) {
        log.info("출하진행현황 조회 요청 - 주문일자: {}, 범위: {}-{}, 주문번호: {}, 출고형태: {}, 현장명: {}", 
                orderDate, startDate, endDate, orderNumber, sdiv, comName);
        
        // 기존의 필터링 메서드 활용 (모든 거래처 대상)
        Page<OrderMast> orders = orderMastRepository.findByCustomerWithFilters(
                null, orderDate, startDate, endDate, orderNumber, sdiv, comName, pageable);
        Page<OrderShipmentResponse> responses = orders.map(this::convertToShipmentResponse);
        
        log.info("출하진행현황 조회 완료 - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 거래처별 출하진행현황 조회 (페이징 + 필터링)
     */
    public Page<OrderShipmentResponse> getShipmentStatusByCustomer(Integer custId, String orderDate, 
                                                                  String startDate, String endDate,
                                                                  String orderNumber, String shipNumber, String sdiv, String comName, 
                                                                  Pageable pageable) {
        log.info("거래처별 출하진행현황 조회 요청 - 거래처ID: {}, 주문일자: {}, 범위: {}-{}, 주문번호: {}, 출하번호: {}, 출고형태: {}, 현장명: {}", 
                custId, orderDate, startDate, endDate, orderNumber, shipNumber, sdiv, comName);
        
        // 출하번호 검색이 있는 경우 ShipOrder 조인 쿼리 사용, 없으면 기존 쿼리 사용
        Page<OrderMast> orders;
        if (shipNumber != null && !shipNumber.trim().isEmpty()) {
            orders = orderMastRepository.findByCustomerWithFiltersIncludingShipNumber(
                    custId, orderDate, startDate, endDate, orderNumber, shipNumber, sdiv, comName, pageable);
        } else {
            orders = orderMastRepository.findByCustomerWithFilters(
                    custId, orderDate, startDate, endDate, orderNumber, sdiv, comName, pageable);
        }
        
        // 배치 상태 계산을 한 번만 수행
        Map<String, String> statusMap = calculateBatchStatusByCustomer(custId, orders.getContent());
        
        // 상태 정보를 포함한 변환
        Page<OrderShipmentResponse> responses = orders.map(orderMast -> 
                convertToShipmentResponseWithStatus(orderMast, statusMap));
        
        log.info("거래처별 출하진행현황 조회 완료 - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * OrderDetailResponse에 코드 표시명 추가
     */
    private OrderDetailResponse addDisplayNames(OrderDetailResponse response, OrderMast orderMast) {
        // 출고형태명
        String sdivDisplayName = "";
        if (orderMast.getOrderMastSdiv() != null && !orderMast.getOrderMastSdiv().trim().isEmpty()) {
            try {
                sdivDisplayName = commonCodeService.getDisplayNameByCode(orderMast.getOrderMastSdiv());
            } catch (Exception e) {
                log.warn("출고형태 코드 조회 실패: {}", orderMast.getOrderMastSdiv(), e);
            }
        }
        
        // 화폐코드명
        String currencyDisplayName = "";
        if (orderMast.getOrderMastCurrency() != null && !orderMast.getOrderMastCurrency().trim().isEmpty()) {
            try {
                currencyDisplayName = commonCodeService.getDisplayNameByCode(orderMast.getOrderMastCurrency());
            } catch (Exception e) {
                log.warn("화폐 코드 조회 실패: {}", orderMast.getOrderMastCurrency(), e);
            }
        }
        
        // 용도코드명
        String reasonDisplayName = "";
        if (orderMast.getOrderMastReason() != null && !orderMast.getOrderMastReason().trim().isEmpty()) {
            try {
                reasonDisplayName = commonCodeService.getDisplayNameByCode(orderMast.getOrderMastReason());
            } catch (Exception e) {
                log.warn("용도 코드 조회 실패: {}", orderMast.getOrderMastReason(), e);
            }
        }
        
        // 새로운 Response 생성 (표시명 포함)
        return OrderDetailResponse.builder()
                .orderNumber(response.getOrderNumber())
                .orderMastDate(response.getOrderMastDate())
                .orderMastSdiv(response.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName)
                .orderMastOdate(response.getOrderMastOdate())
                .orderMastOtime(response.getOrderMastOtime())
                .orderMastDcust(response.getOrderMastDcust())
                .orderMastComaddr(response.getOrderMastComaddr())
                .orderMastComname(response.getOrderMastComname())
                .orderMastCurrency(response.getOrderMastCurrency())
                .orderMastCurrencyDisplayName(currencyDisplayName)
                .orderMastCurrencyPer(response.getOrderMastCurrencyPer())
                .orderMastReason(response.getOrderMastReason())
                .orderMastReasonDisplayName(reasonDisplayName)
                .orderMastComuname(response.getOrderMastComuname())
                .orderMastComutel(response.getOrderMastComutel())
                .orderMastRemark(response.getOrderMastRemark())
                .orderTranList(response.getOrderTranList())
                .build();
    }

    /**
     * OrderTran을 OrderTranDetailResponse로 변환 (상세페이지용 간단버전)
     */
    private OrderTranDetailResponse convertOrderTranToDetailResponse(OrderTran orderTran) {
        // 제품코드 조회 (co_item_code.item_code_num)
        String itemCodeNum = "";
        if (orderTran.getOrderTranItem() != null) {
            try {
                itemCodeNum = itemCodeRepository.findById(orderTran.getOrderTranItem())
                        .map(itemCode -> itemCode.getItemCodeNum())
                        .orElse("");
            } catch (Exception e) {
                log.warn("제품코드 조회 실패 - OrderTranItem: {}", orderTran.getOrderTranItem(), e);
            }
        }
        
        // 상태 표시명 추가
        String statusDisplayName = "";
        if (orderTran.getOrderTranStau() != null && !orderTran.getOrderTranStau().trim().isEmpty()) {
            try {
                statusDisplayName = commonCodeService.getDisplayNameByCode(orderTran.getOrderTranStau());
            } catch (Exception e) {
                log.warn("OrderTran 상태 코드 조회 실패: {}", orderTran.getOrderTranStau(), e);
            }
        }
        
        return OrderTranDetailResponse.builder()
                .itemCodeNum(itemCodeNum)                       // 제품코드
                .orderTranItem(orderTran.getOrderTranItem())    // 제품번호 (FK)
                .orderTranDeta(orderTran.getOrderTranDeta())    // 제품명
                .orderTranSpec(orderTran.getOrderTranSpec())    // 규격
                .orderTranUnit(orderTran.getOrderTranUnit())    // 단위
                .orderTranCnt(orderTran.getOrderTranCnt())      // 수량
                .orderTranDcPer(orderTran.getOrderTranDcPer())  // DC(%)
                .orderTranAmt(orderTran.getOrderTranAmt())      // 단가
                .orderTranTot(orderTran.getOrderTranTot())      // 금액
                .orderTranStau(orderTran.getOrderTranStau())    // 상태코드
                .orderTranStauDisplayName(statusDisplayName)   // 상태코드명
                .build();
    }

    /**
     * OrderMast를 OrderShipmentResponse로 변환 (출하진행현황용)
     */
    private OrderShipmentResponse convertToShipmentResponse(OrderMast orderMast) {
        String orderNumber = orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno();
        
        // 출고형태명 조회
        String sdivDisplayName = getDisplayNameSafely(orderMast.getOrderMastSdiv());
        
        return OrderShipmentResponse.builder()
                .orderNumber(orderNumber)
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName)
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastOdate(orderMast.getOrderMastOdate())
                .build();
    }

    /**
     * OrderMast를 OrderShipmentResponse로 변환 (상태 정보 포함)
     */
    private OrderShipmentResponse convertToShipmentResponseWithStatus(OrderMast orderMast, 
                                                                      Map<String, String> statusMap) {
        String orderNumber = orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno();
        String orderKey = makeOrderKey(orderMast);
        
        // 출고형태명 조회
        String sdivDisplayName = getDisplayNameSafely(orderMast.getOrderMastSdiv());
        
        // 상태 정보 가져오기
        String status = statusMap.getOrDefault(orderKey, "");
        String statusDisplayName = "";
        if (!status.isEmpty()) {
            try {
                statusDisplayName = commonCodeService.getDisplayNameByCode(status);
            } catch (Exception e) {
                log.warn("상태 코드 조회 실패: {}", status, e);
            }
        }
        
        // 출하번호 조회 (ShipOrder를 통해)
        String shipNumber = getShipNumberByOrderKey(orderMast);
        
        return OrderShipmentResponse.builder()
                .orderNumber(orderNumber)
                .shipNumber(shipNumber)
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName)
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastOdate(orderMast.getOrderMastOdate())
                .orderMastStatus(status)
                .orderMastStatusDisplayName(statusDisplayName)
                .build();
    }

    /**
     * OrderMast Entity를 OrderMastResponse로 변환 (모든 코드 표시명 포함)
     */
    private OrderMastResponse convertToResponse(OrderMast orderMast) {
        // 코드 표시명들 조회
        String ujcdDisplayName = getDisplayNameSafely(orderMast.getOrderMastUjcd());
        String reasonDisplayName = getDisplayNameSafely(orderMast.getOrderMastReason());
        String tcomdivDisplayName = getDisplayNameSafely(orderMast.getOrderMastTcomdiv());
        String currencyDisplayName = getDisplayNameSafely(orderMast.getOrderMastCurrency());
        String sdivDisplayName = getDisplayNameSafely(orderMast.getOrderMastSdiv());
        String intypeDisplayName = getDisplayNameSafely(orderMast.getOrderMastIntype());
        
        // 소속 코드명 조회
        String sosokName = getSosokCodeNameSafely(orderMast.getOrderMastSosok());
        
        // 사원명 조회
        String sawonName = getInsaMastNameSafely(orderMast.getOrderMastSawon());
        
        // 부서명 조회
        String buseName = getBuseCodeNameSafely(orderMast.getOrderMastSawonBuse());
        
        // FK 관계 테이블 정보 조회
        String orderMastCustName = getCustomerNameSafely(orderMast.getOrderMastCust());
        String orderMastScustName = getCustomerNameSafely(orderMast.getOrderMastScust());
        
        return OrderMastResponse.from(orderMast, ujcdDisplayName, reasonDisplayName, tcomdivDisplayName,
                currencyDisplayName, sdivDisplayName, intypeDisplayName, sosokName, sawonName, buseName,
                orderMastCustName, orderMastScustName);
    }

    /**
     * 안전한 코드 표시명 조회 (null 체크 포함)
     */
    private String getDisplayNameSafely(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "";
        }
        try {
            return commonCodeService.getDisplayNameByCode(code);
        } catch (Exception e) {
            log.warn("코드 표시명 조회 실패: {}", code, e);
            return "";
        }
    }

    /**
     * 안전한 거래처명 조회 (null 체크 포함)
     */
    private String getCustomerNameSafely(Integer custId) {
        if (custId == null) {
            return "";
        }
        try {
            return customerService.getCustomer(custId).getDisplayName();
        } catch (Exception e) {
            log.warn("거래처명 조회 실패: {}", custId, e);
            return "";
        }
    }

    /**
     * 안전한 소속 코드명 조회 (null 체크 포함)
     */
    private String getSosokCodeNameSafely(Integer sosokCode) {
        if (sosokCode == null) {
            return "";
        }
        try {
            return commonCodeService.getSosokCodeName(sosokCode);
        } catch (Exception e) {
            log.warn("소속 코드명 조회 실패: {}", sosokCode, e);
            return "";
        }
    }

    /**
     * Customer ID로 custCodeName 조회 (FK 관계용)
     */
    private String getCustomerNameByIdSafely(Integer custId) {
        if (custId == null || custId == 0) {
            return "";
        }
        try {
            return customerService.getCustomer(custId).getCustCodeName();
        } catch (Exception e) {
            log.warn("Customer FK 조회 실패 - custId: {}", custId, e);
            return "";
        }
    }

    /**
     * 안전한 사원명 조회 (null 체크 포함)
     */
    private String getInsaMastNameSafely(Integer sano) {
        if (sano == null) {
            return "";
        }
        try {
            return commonCodeService.getInsaMastName(sano);
        } catch (Exception e) {
            log.warn("사원명 조회 실패: {}", sano, e);
            return "";
        }
    }

    /**
     * 안전한 부서명 조회 (null 체크 포함)
     */
    private String getBuseCodeNameSafely(Integer buseCode) {
        if (buseCode == null) {
            return "";
        }
        try {
            return commonCodeService.getBuseCodeName(buseCode);
        } catch (Exception e) {
            log.warn("부서명 조회 실패: {}", buseCode, e);
            return "";
        }
    }

    /**
     * 거래처별 주문 상태를 배치로 계산 (간단한 방식)
     */
    private Map<String, String> calculateBatchStatusByCustomer(Integer custId, List<OrderMast> orders) {
        if (orders.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            // 거래처별로 모든 상태 분포 조회 (간단한 쿼리 사용)
            List<Object[]> statusDistribution = orderTranRepository.findStatusDistributionByCustomer(custId);
            
            // 주문별 상태 계산
            Map<String, String> statusMap = new HashMap<>();
            Map<String, Map<String, Long>> orderStatusCounts = new HashMap<>();
            
            // 상태 분포 데이터를 맵으로 변환
            for (Object[] row : statusDistribution) {
                String date = (String) row[0];
                Integer sosok = (Integer) row[1];
                String ujcd = (String) row[2];
                Integer acno = (Integer) row[3];
                String status = (String) row[4];
                Long count = (Long) row[5];
                
                String orderKey = makeOrderKey(date, sosok, ujcd, acno);
                orderStatusCounts.computeIfAbsent(orderKey, k -> new HashMap<>()).put(status, count);
            }
            
            // 조회된 주문들에 대해서만 상태 결정
            for (OrderMast order : orders) {
                String orderKey = makeOrderKey(order);
                if (orderStatusCounts.containsKey(orderKey)) {
                    Map<String, Long> statusCounts = orderStatusCounts.get(orderKey);
                    String finalStatus = determineOrderStatus(statusCounts);
                    statusMap.put(orderKey, finalStatus);
                } else {
                    // OrderTran이 없는 경우 기본 상태
                    statusMap.put(orderKey, "4010020001"); // 수주진행
                }
            }
            
            return statusMap;
        } catch (Exception e) {
            log.error("거래처별 배치 상태 계산 실패 - custId: {}", custId, e);
            // 에러 발생시 빈 맵 반환 (상태 없이 진행)
            return new HashMap<>();
        }
    }

    /**
     * OrderTran 상태 분포를 기반으로 OrderMast의 최종 상태 결정
     */
    private String determineOrderStatus(Map<String, Long> statusCounts) {
        // 상태 코드 상수
        final String STATUS_COMPLETED = "4010030001";  // 출하완료
        final String STATUS_IN_PROGRESS = "4010020001"; // 수주진행  
        final String STATUS_REGISTERED = "4010010001";  // 수주등록
        
        long totalCount = statusCounts.values().stream().mapToLong(Long::longValue).sum();
        long completedCount = statusCounts.getOrDefault(STATUS_COMPLETED, 0L);
        
        // 모든 항목이 출하완료인 경우
        if (completedCount == totalCount && totalCount > 0) {
            return STATUS_COMPLETED;
        }
        
        // 하나라도 수주등록이나 수주진행이 있는 경우
        if (statusCounts.containsKey(STATUS_REGISTERED) || statusCounts.containsKey(STATUS_IN_PROGRESS)) {
            return STATUS_IN_PROGRESS;
        }
        
        // 기본값: 수주진행
        return STATUS_IN_PROGRESS;
    }

    /**
     * OrderMast 키를 문자열로 변환
     */
    private String makeOrderKey(OrderMast orderMast) {
        return makeOrderKey(orderMast.getOrderMastDate(), orderMast.getOrderMastSosok(), 
                           orderMast.getOrderMastUjcd(), orderMast.getOrderMastAcno());
    }

    /**
     * 주문 키를 문자열로 변환
     */
    private String makeOrderKey(String date, Integer sosok, String ujcd, Integer acno) {
        return String.format("%s-%d-%s-%d", date, sosok, ujcd, acno);
    }

    /**
     * OrderMast에 해당하는 출하번호 조회 (ShipOrder를 통해)
     */
    private String getShipNumberByOrderKey(OrderMast orderMast) {
        try {
            // ShipOrder 테이블에서 해당 주문에 매핑된 출하정보 조회
            List<ShipOrder> shipOrders = shipOrderRepository.findByOrderKey(
                    orderMast.getOrderMastDate(),
                    orderMast.getOrderMastSosok(),
                    orderMast.getOrderMastUjcd(),
                    orderMast.getOrderMastAcno()
            );
            
            if (!shipOrders.isEmpty()) {
                ShipOrder shipOrder = shipOrders.get(0); // 첫 번째 출하정보 사용
                return shipOrder.getShipOrderDate() + "-" + shipOrder.getShipOrderAcno();
            }
        } catch (Exception e) {
            log.warn("출하번호 조회 실패 - OrderKey: {}", makeOrderKey(orderMast), e);
        }
        
        return ""; // 출하정보가 없으면 빈 문자열 반환
    }

    /**
     * 주문 통계 정보 클래스
     */
    @lombok.Builder
    @lombok.Getter
    public static class OrderStatistics {
        private final long totalOrders;
        private final long todayOrders;
        private final long monthOrders;
    }

    /**
     * OrderMast Entity를 OrderMastListResponse로 변환 (성능 최적화용)
     */
    private OrderMastListResponse convertToListResponse(OrderMast orderMast) {
        // 출고형태명만 조회 (필요한 경우에만)
        String sdivDisplayName = "";
        if (orderMast.getOrderMastSdiv() != null && !orderMast.getOrderMastSdiv().trim().isEmpty()) {
            try {
                sdivDisplayName = commonCodeService.getDisplayNameByCode(orderMast.getOrderMastSdiv());
            } catch (Exception e) {
                log.warn("출고형태 코드 조회 실패: {}", orderMast.getOrderMastSdiv(), e);
            }
        }
        
        return OrderMastListResponse.fromWithDisplayName(orderMast, sdivDisplayName);
    }

    /**
     * 미리 계산된 상태 맵을 사용한 OrderMastListResponse 변환 (성능 최적화)
     */
    private OrderMastListResponse convertToListResponseWithPreCalculatedStatus(OrderMast orderMast, 
                                                                               Map<String, String> statusMap) {
        String orderKey = makeOrderKey(orderMast);
        
        // 출고형태명 조회
        String sdivDisplayName = "";
        if (orderMast.getOrderMastSdiv() != null && !orderMast.getOrderMastSdiv().trim().isEmpty()) {
            try {
                sdivDisplayName = commonCodeService.getDisplayNameByCode(orderMast.getOrderMastSdiv());
            } catch (Exception e) {
                log.warn("출고형태 코드 조회 실패: {}", orderMast.getOrderMastSdiv(), e);
            }
        }
        
        // 상태 정보 가져오기
        String status = statusMap.getOrDefault(orderKey, "");
        String statusDisplayName = "";
        if (!status.isEmpty()) {
            try {
                statusDisplayName = commonCodeService.getDisplayNameByCode(status);
            } catch (Exception e) {
                log.warn("상태 코드 조회 실패: {}", status, e);
            }
        }
        
        return OrderMastListResponse.fromWithStatusAndDisplayNames(
                orderMast, sdivDisplayName, status, statusDisplayName);
    }

    /**
     * 여러 주문의 상태를 배치로 계산
     */
    private Map<String, String> calculateBatchStatus(List<OrderMast> orders) {
        if (orders.isEmpty()) {
            return new HashMap<>();
        }
        
        // OrderMast 키 목록 생성
        List<Object[]> orderKeys = orders.stream()
                .map(order -> new Object[]{
                        order.getOrderMastDate(),
                        order.getOrderMastSosok(), 
                        order.getOrderMastUjcd(),
                        order.getOrderMastAcno()
                })
                .collect(Collectors.toList());
        
        // 배치로 OrderTran 상태 분포 조회
        List<Object[]> statusDistribution = orderTranRepository.findStatusDistributionByOrderKeys(orderKeys);
        
        // 주문별 상태 계산
        Map<String, String> statusMap = new HashMap<>();
        Map<String, Map<String, Long>> orderStatusCounts = new HashMap<>();
        
        // 상태 분포 데이터를 맵으로 변환
        for (Object[] row : statusDistribution) {
            String date = (String) row[0];
            Integer sosok = (Integer) row[1];
            String ujcd = (String) row[2];
            Integer acno = (Integer) row[3];
            String status = (String) row[4];
            Long count = (Long) row[5];
            
            String orderKey = makeOrderKey(date, sosok, ujcd, acno);
            orderStatusCounts.computeIfAbsent(orderKey, k -> new HashMap<>()).put(status, count);
        }
        
        // 각 주문의 최종 상태 결정
        for (String orderKey : orderStatusCounts.keySet()) {
            Map<String, Long> statusCounts = orderStatusCounts.get(orderKey);
            String finalStatus = determineOrderStatus(statusCounts);
            statusMap.put(orderKey, finalStatus);
        }
        
        return statusMap;
    }
} 