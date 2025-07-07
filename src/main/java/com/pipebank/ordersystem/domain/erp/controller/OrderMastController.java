package com.pipebank.ordersystem.domain.erp.controller;

import com.pipebank.ordersystem.domain.erp.dto.OrderMastResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderMastListResponse;
import com.pipebank.ordersystem.domain.erp.dto.OrderDetailResponse;
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
     * 주문 상세조회 (주문번호 기준)
     * GET /api/erp/orders/detail/{orderNumber}
     * 예: /api/erp/orders/detail/20240101-1
     * 
     * OrderMast(헤더) + OrderTran(상세) 정보 포함
     */
    @GetMapping("/detail/{orderNumber}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @PathVariable String orderNumber) {
        
        log.info("주문 상세조회 API 호출 - 주문번호: {}", orderNumber);
        
        OrderDetailResponse response = orderMastService.getOrderDetail(orderNumber);
        return ResponseEntity.ok(response);
    }

} 