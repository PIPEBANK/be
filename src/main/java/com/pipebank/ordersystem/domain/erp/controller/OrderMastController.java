package com.pipebank.ordersystem.domain.erp.controller;

import com.pipebank.ordersystem.domain.erp.dto.OrderMastResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderMastListResponse;
import com.pipebank.ordersystem.domain.erp.service.OrderMastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/erp/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderMastController {

    private final OrderMastService orderMastService;

    /**
     * 복합키로 주문 단일 조회
     * GET /api/erp/orders/{date}/{sosok}/{ujcd}/{acno}
     */
    @GetMapping("/{date}/{sosok}/{ujcd}/{acno}")
    public ResponseEntity<OrderMastResponse> getOrderMast(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno) {
        
        log.info("주문 단일 조회 API 호출 - 날짜: {}, 소속: {}, 업장: {}, 계정: {}", date, sosok, ujcd, acno);
        
        OrderMastResponse response = orderMastService.getOrderMast(date, sosok, ujcd, acno);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문일자 범위별 주문 목록 조회 (페이징)
     * GET /api/erp/orders/date-range?startDate=20240101&endDate=20240131
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<OrderMastResponse>> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @PageableDefault(size = 20, sort = "orderMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("주문일자 범위별 조회 API 호출 - 시작: {}, 종료: {}", startDate, endDate);
        
        Page<OrderMastResponse> response = orderMastService.getOrdersByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처별 주문 목록 조회 (페이징 + 필터링) - 성능 최적화용
     * GET /api/erp/orders/customer/{custId}
     * 
     * 필터링 파라미터:
     * - orderDate: 주문일자 (정확히 일치)
     * - startDate: 시작 주문일자 (범위 조회)
     * - endDate: 종료 주문일자 (범위 조회)
     * - orderNumber: 주문번호 (부분 검색)
     * - sdiv: 출고형태 (ORDER_MAST_SDIV)
     * - comName: 납품현장명 (부분 검색)
     * 
     * 예시: 
     * - 특정 날짜: GET /api/erp/orders/customer/9?orderDate=20240101
     * - 날짜 범위: GET /api/erp/orders/customer/9?startDate=20240101&endDate=20240131
     * - 복합 필터: GET /api/erp/orders/customer/9?startDate=20240101&endDate=20240131&sdiv=1&comName=현장명
     */
    @GetMapping("/customer/{custId}")
    public ResponseEntity<Page<OrderMastListResponse>> getOrdersByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String orderDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String sdiv,
            @RequestParam(required = false) String comName,
            @PageableDefault(size = 20, sort = "orderMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("거래처별 주문 조회 API 호출 - 거래처ID: {}, 필터: orderDate={}, startDate={}, endDate={}, orderNumber={}, sdiv={}, comName={}", 
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName);
        
        Page<OrderMastListResponse> response = orderMastService.getOrdersByCustomerWithFiltersForList(
                custId, orderDate, startDate, endDate, orderNumber, sdiv, comName, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합 조건으로 주문 검색
     * GET /api/erp/orders/search
     */
    @GetMapping("/search")
    public ResponseEntity<Page<OrderMastResponse>> searchOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer custId,
            @RequestParam(required = false) Integer sawonId,
            @RequestParam(required = false) Integer sosokId,
            @RequestParam(required = false) String ujcd,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String companyName,
            @PageableDefault(size = 20, sort = "orderMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("주문 복합 검색 API 호출");
        
        Page<OrderMastResponse> response = orderMastService.searchOrders(
                startDate, endDate, custId, sawonId, sosokId, ujcd, projectId, companyName, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 최신 주문 목록 조회
     * GET /api/erp/orders/latest?limit=10
     */
    @GetMapping("/latest")
    public ResponseEntity<List<OrderMastResponse>> getLatestOrders(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("최신 주문 조회 API 호출 - 제한: {}건", limit);
        
        List<OrderMastResponse> response = orderMastService.getLatestOrders(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문번호로 주문 조회 (DATE-ACNO 형식)
     * GET /api/erp/orders/order-number/{orderNumber}
     * 예: /api/erp/orders/order-number/20210101-1
     */
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<List<OrderMastResponse>> getOrdersByOrderNumber(
            @PathVariable String orderNumber) {
        
        log.info("주문번호로 조회 API 호출 - 주문번호: {}", orderNumber);
        
        List<OrderMastResponse> response = orderMastService.getOrdersByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 날짜의 모든 주문 조회
     * GET /api/erp/orders/date/{date}
     * 예: /api/erp/orders/date/20210101
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderMastResponse>> getOrdersByDate(
            @PathVariable String date) {
        
        log.info("날짜별 주문 조회 API 호출 - 날짜: {}", date);
        
        List<OrderMastResponse> response = orderMastService.getOrdersByDate(date);
        return ResponseEntity.ok(response);
    }


} 