package com.pipebank.ordersystem.domain.web.temp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastListResponse;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastResponse;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import com.pipebank.ordersystem.domain.web.temp.service.TempWebOrderMastService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/web/temp/order-mast")
@RequiredArgsConstructor
@Slf4j
public class TempWebOrderMastController {

    private final TempWebOrderMastService tempWebOrderMastService;

    // 생성
    @PostMapping
    public ResponseEntity<TempWebOrderMastResponse> create(@RequestBody TempWebOrderMastCreateRequest request) {
        TempWebOrderMastResponse response = tempWebOrderMastService.create(request);
        return ResponseEntity.ok(response);
    }

    // 통합 생성 (Mast + Tran 한 번에 처리) - 새로 추가
    @PostMapping("/with-trans")
    public ResponseEntity<TempWebOrderMastResponse> createWithTrans(@RequestBody TempWebOrderMastCreateRequest request) {
        TempWebOrderMastResponse response = tempWebOrderMastService.createWithTrans(request);
        return ResponseEntity.ok(response);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<TempWebOrderMastResponse>> findAll() {
        List<TempWebOrderMastResponse> responses = tempWebOrderMastService.findAll();
        return ResponseEntity.ok(responses);
    }

    /**
     * 거래처별 임시저장 주문 목록 조회 (페이징 + 필터링)
     * GET /api/web/temp/order-mast/customer/{custId}
     * 
     * 필터링 파라미터:
     * - orderDate: 주문일자 (정확히 일치)
     * - startDate: 시작 주문일자 (범위 조회)
     * - endDate: 종료 주문일자 (범위 조회)
     * - orderNumber: 주문번호 (부분 검색)
     * - userId: 작성자 (부분 검색)
     * - comName: 현장명 (부분 검색)
     * 
     * 응답 필드:
     * - orderNumber: 주문번호 (orderMastDate + "-" + orderMastAcno)
     * - userId: 작성자
     * - orderMastComname: 현장명
     * - orderMastDate: 주문일자
     * 
     * 조건: send = false인 임시저장 주문만 조회
     * 
     * 예시:
     * - GET /api/web/temp/order-mast/customer/2808
     * - GET /api/web/temp/order-mast/customer/2808?startDate=20240101&endDate=20240131
     * - GET /api/web/temp/order-mast/customer/2808?userId=user123&comName=현장
     */
    @GetMapping("/customer/{custId}")
    public ResponseEntity<Page<TempWebOrderMastListResponse>> getTempOrdersByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String orderDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String comName,
            @PageableDefault(size = 20, sort = "orderMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("거래처별 임시저장 주문 조회 API 호출 - 거래처ID: {}, 필터: orderDate={}, startDate={}, endDate={}, orderNumber={}, userId={}, comName={}", 
                custId, orderDate, startDate, endDate, orderNumber, userId, comName);
        
        Page<TempWebOrderMastListResponse> response = tempWebOrderMastService.getTempOrdersByCustomerWithFilters(
                custId, orderDate, startDate, endDate, orderNumber, userId, comName, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문번호로 조회 
     * GET /api/web/temp/order-mast/by-order-number/{orderNumber}
     * 
     * @param orderNumber 주문번호 (형식: "YYYYMMDD-숫자", 예: "20250710-1")
     * @return TempWebOrderMastResponse
     * 
     * 예시:
     * - GET /api/web/temp/order-mast/by-order-number/20250710-1
     * - GET /api/web/temp/order-mast/by-order-number/20240315-5
     */
    @GetMapping("/by-order-number/{orderNumber}")
    public ResponseEntity<TempWebOrderMastResponse> findByOrderNumber(@PathVariable String orderNumber) {
        log.info("주문번호로 임시저장 주문 조회 API 호출 - 주문번호: {}", orderNumber);
        
        return tempWebOrderMastService.findByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 주문번호로 통합 수정 (OrderMast + OrderTran 한 번에 처리)
     * PUT /api/web/temp/order-mast/by-order-number/{orderNumber}/with-trans
     * 
     * @param orderNumber 주문번호 (형식: "YYYYMMDD-숫자", 예: "20250710-1")
     * @param request 수정 요청 데이터
     * @return 수정된 TempWebOrderMastResponse (OrderTran 포함)
     * 
     * 사용 시나리오:
     * 1. 임시저장 재저장: send=false로 기존 데이터 수정
     * 2. 수정 후 전송: send=true로 수정된 데이터 전송
     * 
     * 예시:
     * - PUT /api/web/temp/order-mast/by-order-number/20250710-1/with-trans
     * - Body: { "send": false, "orderTrans": [...] } // 임시저장 재저장
     * - Body: { "send": true, "orderTrans": [...] } // 수정 후 전송
     */
    @PutMapping("/by-order-number/{orderNumber}/with-trans")
    public ResponseEntity<TempWebOrderMastResponse> updateWithTransByOrderNumber(
            @PathVariable String orderNumber,
            @RequestBody TempWebOrderMastCreateRequest request) {
        
        log.info("주문번호로 통합 수정 API 호출 - 주문번호: {}, send: {}", orderNumber, request.getSend());
        
        return tempWebOrderMastService.updateWithTransByOrderNumber(orderNumber, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 수정
    @PutMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}")
    public ResponseEntity<TempWebOrderMastResponse> update(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno,
            @RequestBody TempWebOrderMastCreateRequest request) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        return tempWebOrderMastService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 삭제
    @DeleteMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}")
    public ResponseEntity<Void> delete(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        boolean deleted = tempWebOrderMastService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // send 상태를 true로 변경하여 WebOrderMast로 변환
    @PatchMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}/send")
    public ResponseEntity<?> markAsSent(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        try {
            // 현재 임시저장 데이터 조회
            return tempWebOrderMastService.findById(id)
                    .map(tempOrder -> {
                        if (Boolean.TRUE.equals(tempOrder.getSend())) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "이미 전송된 주문입니다."));
                        }
                        
                        // send를 true로 변경하는 요청 생성
                        TempWebOrderMastCreateRequest updateRequest = TempWebOrderMastCreateRequest.builder()
                                .orderMastDate(tempOrder.getOrderMastDate())
                                .orderMastSosok(tempOrder.getOrderMastSosok())
                                .orderMastUjcd(tempOrder.getOrderMastUjcd())
                                // orderMastAcno는 자동생성이므로 제거
                                .orderMastCust(tempOrder.getOrderMastCust())
                                .orderMastScust(tempOrder.getOrderMastScust())
                                .orderMastSawon(tempOrder.getOrderMastSawon())
                                .orderMastSawonBuse(tempOrder.getOrderMastSawonBuse())
                                .orderMastOdate(tempOrder.getOrderMastOdate())
                                .orderMastProject(tempOrder.getOrderMastProject())
                                .orderMastRemark(tempOrder.getOrderMastRemark())
                                // 날짜/사용자 필드들은 자동생성되므로 제거
                                .orderMastComaddr1(tempOrder.getOrderMastComaddr1())
                                .orderMastComaddr2(tempOrder.getOrderMastComaddr2())
                                .orderMastComname(tempOrder.getOrderMastComname())
                                .orderMastComuname(tempOrder.getOrderMastComuname())
                                .orderMastComutel(tempOrder.getOrderMastComutel())
                                .orderMastReason(tempOrder.getOrderMastReason())
                                .orderMastTcomdiv(tempOrder.getOrderMastTcomdiv())
                                .orderMastCurrency(tempOrder.getOrderMastCurrency())
                                .orderMastCurrencyPer(tempOrder.getOrderMastCurrencyPer())
                                .orderMastSdiv(tempOrder.getOrderMastSdiv())
                                .orderMastDcust(tempOrder.getOrderMastDcust())
                                .orderMastIntype(tempOrder.getOrderMastIntype())
                                .orderMastOtime(tempOrder.getOrderMastOtime())
                                .send(true) // 핵심: send를 true로 변경
                                .build();
                        
                        // 업데이트 실행 (내부적으로 WebOrderMast 생성됨)
                        return tempWebOrderMastService.update(id, updateRequest)
                                .map(updatedOrder -> ResponseEntity.ok(Map.of(
                                        "message", "임시저장 주문이 정식 주문으로 변환되었습니다.",
                                        "orderKey", updatedOrder.getOrderKey(),
                                        "tempOrder", updatedOrder
                                )))
                                .orElse(ResponseEntity.notFound().build());
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "주문 변환 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 