package com.pipebank.ordersystem.domain.erp.controller;

import com.pipebank.ordersystem.domain.erp.dto.OrderTranResponse;
import com.pipebank.ordersystem.domain.erp.service.OrderTranService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/erp/order-trans")
@RequiredArgsConstructor
@Slf4j
public class OrderTranController {

    private final OrderTranService orderTranService;

    /**
     * 복합키로 주문 상세 조회
     */
    @GetMapping("/{date}/{sosok}/{ujcd}/{acno}/{seq}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderTranResponse> getOrderTran(@PathVariable String date,
                                                         @PathVariable Integer sosok,
                                                         @PathVariable String ujcd,
                                                         @PathVariable Integer acno,
                                                         @PathVariable Integer seq) {
        log.info("주문 상세 조회 API 호출 - 날짜: {}, 소속: {}, 업장: {}, 계정: {}, 순번: {}", date, sosok, ujcd, acno, seq);
        OrderTranResponse response = orderTranService.getOrderTran(date, sosok, ujcd, acno, seq);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 주문의 모든 상세 내역 조회
     */
    @GetMapping("/order/{date}/{sosok}/{ujcd}/{acno}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByOrderMast(@PathVariable String date,
                                                                           @PathVariable Integer sosok,
                                                                           @PathVariable String ujcd,
                                                                           @PathVariable Integer acno) {
        log.info("주문별 상세 내역 조회 API 호출 - 날짜: {}, 소속: {}, 업장: {}, 계정: {}", date, sosok, ujcd, acno);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByOrderMast(date, sosok, ujcd, acno);
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 주문의 모든 상세 내역 조회 (페이징)
     */
    @GetMapping("/order/{date}/{sosok}/{ujcd}/{acno}/paged")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<OrderTranResponse>> getOrderTransByOrderMastPaged(@PathVariable String date,
                                                                                @PathVariable Integer sosok,
                                                                                @PathVariable String ujcd,
                                                                                @PathVariable Integer acno,
                                                                                @PageableDefault(size = 20, sort = "orderTranSeq", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("주문별 상세 내역 조회 API 호출 (페이징) - 날짜: {}, 소속: {}, 업장: {}, 계정: {}", date, sosok, ujcd, acno);
        Page<OrderTranResponse> responses = orderTranService.getOrderTransByOrderMast(date, sosok, ujcd, acno, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문일자별 상세 내역 조회
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByDate(@PathVariable String date) {
        log.info("주문일자별 상세 내역 조회 API 호출 - 날짜: {}", date);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByDate(date);
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문일자 범위별 상세 내역 조회
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByDateRange(@RequestParam String startDate,
                                                                           @RequestParam String endDate) {
        log.info("주문일자 범위별 상세 내역 조회 API 호출 - 시작: {}, 종료: {}", startDate, endDate);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문일자 범위별 상세 내역 조회 (페이징)
     */
    @GetMapping("/date-range/paged")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<OrderTranResponse>> getOrderTransByDateRangePaged(@RequestParam String startDate,
                                                                                @RequestParam String endDate,
                                                                                @PageableDefault(size = 20, sort = "orderTranDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("주문일자 범위별 상세 내역 조회 API 호출 (페이징) - 시작: {}, 종료: {}", startDate, endDate);
        Page<OrderTranResponse> responses = orderTranService.getOrderTransByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 품목코드별 주문 상세 조회
     */
    @GetMapping("/item/{itemCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByItem(@PathVariable Integer itemCode) {
        log.info("품목코드별 주문 상세 조회 API 호출 - 품목코드: {}", itemCode);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByItem(itemCode);
        return ResponseEntity.ok(responses);
    }

    /**
     * 품목명으로 검색
     */
    @GetMapping("/search/item-name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> searchOrderTransByItemName(@RequestParam String itemName) {
        log.info("품목명으로 주문 상세 검색 API 호출 - 검색어: {}", itemName);
        List<OrderTranResponse> responses = orderTranService.searchOrderTransByItemName(itemName);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상태코드별 주문 상세 조회
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByStatus(@PathVariable String status) {
        log.info("상태코드별 주문 상세 조회 API 호출 - 상태코드: {}", status);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByStatus(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * 소속별 주문 상세 조회
     */
    @GetMapping("/sosok/{sosok}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransBySosok(@PathVariable Integer sosok) {
        log.info("소속별 주문 상세 조회 API 호출 - 소속: {}", sosok);
        List<OrderTranResponse> responses = orderTranService.getOrderTransBySosok(sosok);
        return ResponseEntity.ok(responses);
    }

    /**
     * 업장코드별 주문 상세 조회
     */
    @GetMapping("/ujcd/{ujcd}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByUjcd(@PathVariable String ujcd) {
        log.info("업장코드별 주문 상세 조회 API 호출 - 업장코드: {}", ujcd);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByUjcd(ujcd);
        return ResponseEntity.ok(responses);
    }

    /**
     * 최신 주문 상세 내역 조회
     */
    @GetMapping("/latest")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getLatestOrderTrans(@RequestParam(defaultValue = "10") int limit) {
        log.info("최신 주문 상세 내역 조회 API 호출 - 제한: {}건", limit);
        List<OrderTranResponse> responses = orderTranService.getLatestOrderTrans(limit);
        return ResponseEntity.ok(responses);
    }

    /**
     * 복합 조건으로 주문 상세 검색
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<OrderTranResponse>> searchOrderTrans(@RequestParam(required = false) String startDate,
                                                                   @RequestParam(required = false) String endDate,
                                                                   @RequestParam(required = false) Integer sosok,
                                                                   @RequestParam(required = false) String ujcd,
                                                                   @RequestParam(required = false) Integer itemCode,
                                                                   @RequestParam(required = false) String itemName,
                                                                   @RequestParam(required = false) String status,
                                                                   @PageableDefault(size = 20, sort = "orderTranDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("복합 조건으로 주문 상세 검색 API 호출");
        Page<OrderTranResponse> responses = orderTranService.searchOrderTrans(startDate, endDate, sosok, ujcd, itemCode, itemName, status, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문번호로 주문 상세 내역 조회
     */
    @GetMapping("/order-number/{orderNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<OrderTranResponse>> getOrderTransByOrderNumber(@PathVariable String orderNumber) {
        log.info("주문번호로 주문 상세 내역 조회 API 호출 - 주문번호: {}", orderNumber);
        List<OrderTranResponse> responses = orderTranService.getOrderTransByOrderNumber(orderNumber);
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문 상세 통계 정보 조회
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderTranService.OrderTranStatistics> getOrderTranStatistics() {
        log.info("주문 상세 통계 정보 조회 API 호출");
        OrderTranService.OrderTranStatistics statistics = orderTranService.getOrderTranStatistics();
        return ResponseEntity.ok(statistics);
    }
} 