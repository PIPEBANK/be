package com.pipebank.ordersystem.domain.erp.service;

import com.pipebank.ordersystem.domain.erp.dto.OrderTranResponse;
import com.pipebank.ordersystem.domain.erp.entity.OrderTran;
import com.pipebank.ordersystem.domain.erp.repository.OrderTranRepository;
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
public class OrderTranService {

    private final OrderTranRepository orderTranRepository;
    private final CommonCodeService commonCodeService;

    /**
     * 복합키로 주문 상세 조회
     */
    public OrderTranResponse getOrderTran(String orderTranDate, Integer orderTranSosok, 
                                         String orderTranUjcd, Integer orderTranAcno, Integer orderTranSeq) {
        log.info("주문 상세 조회 요청 - 날짜: {}, 소속: {}, 업장: {}, 계정: {}, 순번: {}", 
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, orderTranSeq);
        
        OrderTran orderTran = orderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoAndOrderTranSeq(
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, orderTranSeq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상세입니다: " + 
                        orderTranDate + "-" + orderTranSosok + "-" + orderTranUjcd + "-" + orderTranAcno + "-" + orderTranSeq));
        
        OrderTranResponse response = convertToResponse(orderTran);
        
        log.info("주문 상세 조회 완료 - 주문키: {}, 품목: {}", response.getOrderTranKey(), response.getOrderTranDeta());
        return response;
    }

    /**
     * 특정 주문의 모든 상세 내역 조회
     */
    public List<OrderTranResponse> getOrderTransByOrderMast(String orderTranDate, Integer orderTranSosok, 
                                                           String orderTranUjcd, Integer orderTranAcno) {
        log.info("주문별 상세 내역 조회 요청 - 날짜: {}, 소속: {}, 업장: {}, 계정: {}", 
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderMastKey(orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문별 상세 내역 조회 완료 - 건수: {}", responses.size());
        return responses;
    }

    /**
     * 특정 주문의 모든 상세 내역 조회 (페이징)
     */
    public Page<OrderTranResponse> getOrderTransByOrderMast(String orderTranDate, Integer orderTranSosok, 
                                                           String orderTranUjcd, Integer orderTranAcno, Pageable pageable) {
        log.info("주문별 상세 내역 조회 요청 (페이징) - 날짜: {}, 소속: {}, 업장: {}, 계정: {}", 
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno);
        
        Page<OrderTran> orderTrans = orderTranRepository.findByOrderMastKey(orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, pageable);
        Page<OrderTranResponse> responses = orderTrans.map(this::convertToResponse);
        
        log.info("주문별 상세 내역 조회 완료 (페이징) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 주문일자별 상세 내역 조회
     */
    public List<OrderTranResponse> getOrderTransByDate(String orderTranDate) {
        log.info("주문일자별 상세 내역 조회 요청 - 날짜: {}", orderTranDate);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranDateOrderByKeys(orderTranDate);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문일자별 상세 내역 조회 완료 - 날짜: {}, 건수: {}", orderTranDate, responses.size());
        return responses;
    }

    /**
     * 주문일자 범위별 상세 내역 조회
     */
    public List<OrderTranResponse> getOrderTransByDateRange(String startDate, String endDate) {
        log.info("주문일자 범위별 상세 내역 조회 요청 - 시작: {}, 종료: {}", startDate, endDate);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranDateBetweenOrderByKeys(startDate, endDate);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문일자 범위별 상세 내역 조회 완료 - 시작: {}, 종료: {}, 건수: {}", startDate, endDate, responses.size());
        return responses;
    }

    /**
     * 주문일자 범위별 상세 내역 조회 (페이징)
     */
    public Page<OrderTranResponse> getOrderTransByDateRange(String startDate, String endDate, Pageable pageable) {
        log.info("주문일자 범위별 상세 내역 조회 요청 (페이징) - 시작: {}, 종료: {}", startDate, endDate);
        
        Page<OrderTran> orderTrans = orderTranRepository.findByOrderTranDateBetweenOrderByKeys(startDate, endDate, pageable);
        Page<OrderTranResponse> responses = orderTrans.map(this::convertToResponse);
        
        log.info("주문일자 범위별 상세 내역 조회 완료 (페이징) - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 품목코드별 주문 상세 조회
     */
    public List<OrderTranResponse> getOrderTransByItem(Integer orderTranItem) {
        log.info("품목코드별 주문 상세 조회 요청 - 품목코드: {}", orderTranItem);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranItemOrderByDateDesc(orderTranItem);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("품목코드별 주문 상세 조회 완료 - 품목코드: {}, 건수: {}", orderTranItem, responses.size());
        return responses;
    }

    /**
     * 품목명으로 검색
     */
    public List<OrderTranResponse> searchOrderTransByItemName(String itemName) {
        log.info("품목명으로 주문 상세 검색 요청 - 검색어: {}", itemName);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranDetaContainingOrderByDateDesc(itemName);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("품목명으로 주문 상세 검색 완료 - 검색어: {}, 건수: {}", itemName, responses.size());
        return responses;
    }

    /**
     * 상태코드별 주문 상세 조회
     */
    public List<OrderTranResponse> getOrderTransByStatus(String orderTranStau) {
        log.info("상태코드별 주문 상세 조회 요청 - 상태코드: {}", orderTranStau);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranStauOrderByDateDesc(orderTranStau);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("상태코드별 주문 상세 조회 완료 - 상태코드: {}, 건수: {}", orderTranStau, responses.size());
        return responses;
    }

    /**
     * 소속별 주문 상세 조회
     */
    public List<OrderTranResponse> getOrderTransBySosok(Integer orderTranSosok) {
        log.info("소속별 주문 상세 조회 요청 - 소속: {}", orderTranSosok);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranSosokOrderByDateDesc(orderTranSosok);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("소속별 주문 상세 조회 완료 - 소속: {}, 건수: {}", orderTranSosok, responses.size());
        return responses;
    }

    /**
     * 업장코드별 주문 상세 조회
     */
    public List<OrderTranResponse> getOrderTransByUjcd(String orderTranUjcd) {
        log.info("업장코드별 주문 상세 조회 요청 - 업장코드: {}", orderTranUjcd);
        
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranUjcdOrderByDateDesc(orderTranUjcd);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("업장코드별 주문 상세 조회 완료 - 업장코드: {}, 건수: {}", orderTranUjcd, responses.size());
        return responses;
    }

    /**
     * 최신 주문 상세 내역 조회
     */
    public List<OrderTranResponse> getLatestOrderTrans(int limit) {
        log.info("최신 주문 상세 내역 조회 요청 - 제한: {}건", limit);
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<OrderTran> orderTrans = orderTranRepository.findLatestOrderTrans(pageable);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("최신 주문 상세 내역 조회 완료 - 건수: {}", responses.size());
        return responses;
    }

    /**
     * 복합 조건으로 주문 상세 검색
     */
    public Page<OrderTranResponse> searchOrderTrans(String startDate, String endDate, Integer sosok, String ujcd,
                                                   Integer itemCode, String itemName, String status, Pageable pageable) {
        log.info("복합 조건으로 주문 상세 검색 요청 - 시작: {}, 종료: {}, 소속: {}, 업장: {}, 품목코드: {}, 품목명: {}, 상태: {}", 
                startDate, endDate, sosok, ujcd, itemCode, itemName, status);
        
        Page<OrderTran> orderTrans = orderTranRepository.searchOrderTrans(
                startDate, endDate, sosok, ujcd, itemCode, itemName, status, pageable);
        
        Page<OrderTranResponse> responses = orderTrans.map(this::convertToResponse);
        
        log.info("복합 조건으로 주문 상세 검색 완료 - 총 {}건", responses.getTotalElements());
        return responses;
    }

    /**
     * 주문번호로 주문 상세 내역 조회 (DATE + "-" + ACNO 형식)
     * 예: "20210101-1"
     */
    public List<OrderTranResponse> getOrderTransByOrderNumber(String orderNumber) {
        log.info("주문번호로 주문 상세 내역 조회 요청 - 주문번호: {}", orderNumber);
        
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
        
        // 해당 날짜와 ACNO의 모든 상세 내역 조회 (모든 SOSOK, UJCD 포함)
        List<OrderTran> orderTrans = orderTranRepository.findByOrderTranDateAndOrderTranAcnoOrderByOrderTranSosokAscOrderTranUjcdAscOrderTranSeqAsc(orderDate, acno);
        
        List<OrderTranResponse> responses = orderTrans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("주문번호로 주문 상세 내역 조회 완료 - 주문번호: {}, 건수: {}", orderNumber, responses.size());
        return responses;
    }

    /**
     * 주문 상세 통계 정보 조회
     */
    public OrderTranStatistics getOrderTranStatistics() {
        log.info("주문 상세 통계 정보 조회 요청");
        
        long totalOrderTrans = orderTranRepository.count();
        
        // 오늘 날짜 생성 (YYYYMMDD 형식)
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long todayOrderTrans = orderTranRepository.countByOrderTranDate(today);
        
        // 이번 달 첫날과 마지막날
        java.time.LocalDate now = java.time.LocalDate.now();
        String monthStart = now.withDayOfMonth(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String monthEnd = now.withDayOfMonth(now.lengthOfMonth()).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long monthOrderTrans = orderTranRepository.countByOrderTranDateBetween(monthStart, monthEnd);
        
        OrderTranStatistics statistics = OrderTranStatistics.builder()
                .totalOrderTrans(totalOrderTrans)
                .todayOrderTrans(todayOrderTrans)
                .monthOrderTrans(monthOrderTrans)
                .build();
        
        log.info("주문 상세 통계 정보 조회 완료 - 전체: {}, 오늘: {}, 이번달: {}", totalOrderTrans, todayOrderTrans, monthOrderTrans);
        return statistics;
    }

    /**
     * OrderTran Entity를 OrderTranResponse로 변환 (모든 코드 표시명 포함)
     */
    private OrderTranResponse convertToResponse(OrderTran orderTran) {
        // 상태 코드 표시명 조회
        String stauDisplayName = getDisplayNameSafely(orderTran.getOrderTranStau());
        
        // 업장코드 표시명 조회
        String ujcdDisplayName = getDisplayNameSafely(orderTran.getOrderTranUjcd());
        
        // 소속 코드명 조회
        String sosokName = getSosokCodeNameSafely(orderTran.getOrderTranSosok());
        
        return OrderTranResponse.from(orderTran, stauDisplayName, ujcdDisplayName, sosokName);
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
     * 주문 상세 통계 정보 클래스
     */
    @lombok.Builder
    @lombok.Getter
    public static class OrderTranStatistics {
        private final long totalOrderTrans;
        private final long todayOrderTrans;
        private final long monthOrderTrans;
    }
} 