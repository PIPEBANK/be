package com.pipebank.ordersystem.domain.erp.service;

import com.pipebank.ordersystem.domain.erp.dto.OrderMastResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderMastListResponse;
import com.pipebank.ordersystem.domain.erp.entity.OrderMast;
import com.pipebank.ordersystem.domain.erp.repository.OrderMastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, transactionManager = "erpTransactionManager")
@Slf4j
public class OrderMastService {

    private final OrderMastRepository orderMastRepository;
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
     * 거래처별 주문 목록 조회 (페이징 + 필터링) - 성능 최적화용
     */
    public Page<OrderMastListResponse> getOrdersByCustomerWithFiltersForList(Integer custId, String orderDate, 
                                                                            String startDate, String endDate,
                                                                            String orderNumber, String sdiv, String comName, 
                                                                            Pageable pageable) {
        log.info("거래처별 주문 목록 조회 요청 (최적화) - 거래처ID: {}, 주문일자: {}, 범위: {}-{}, 주문번호: {}, 출고형태: {}, 현장명: {}", 
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName);
        
        Page<OrderMast> orders = orderMastRepository.findByCustomerWithFilters(
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName, pageable);
        Page<OrderMastListResponse> responses = orders.map(this::convertToListResponse);
        
        log.info("거래처별 주문 목록 조회 완료 (최적화) - 총 {}건", responses.getTotalElements());
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
        String orderMastCustName = getCustomerNameByIdSafely(orderMast.getOrderMastCust());
        String orderMastScustName = getCustomerNameByIdSafely(orderMast.getOrderMastScust());
        
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
     * 주문 통계 정보 클래스
     */
    @lombok.Builder
    @lombok.Getter
    public static class OrderStatistics {
        private final long totalOrders;
        private final long todayOrders;
        private final long monthOrders;
    }
} 