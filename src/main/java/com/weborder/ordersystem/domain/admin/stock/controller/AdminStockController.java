package com.weborder.ordersystem.domain.admin.stock.controller;

import com.weborder.ordersystem.domain.admin.stock.dto.StockResponse;
import com.weborder.ordersystem.domain.admin.stock.dto.StockUpdateRequest;
import com.weborder.ordersystem.domain.admin.stock.service.AdminStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/stock")
@RequiredArgsConstructor
public class AdminStockController {

    private final AdminStockService adminStockService;

    /**
     * 재고 현황 목록 조회
     * GET /api/admin/stock?keyword=테스트
     */
    @GetMapping
    public ResponseEntity<List<StockResponse>> getStockList(
            @RequestParam(required = false) String keyword) {
        log.info("재고 현황 조회 API - keyword: {}", keyword);

        List<StockResponse> stocks = adminStockService.getStockList(keyword);
        return ResponseEntity.ok(stocks);
    }

    /**
     * 품목별 재고 조회
     * GET /api/admin/stock/{itemCode}
     */
    @GetMapping("/{itemCode}")
    public ResponseEntity<StockResponse> getStockByItem(
            @PathVariable Integer itemCode) {
        log.info("품목별 재고 조회 API - itemCode: {}", itemCode);

        StockResponse stock = adminStockService.getStockByItem(itemCode);
        return ResponseEntity.ok(stock);
    }

    /**
     * 재고 수정 (직접 설정 또는 증감)
     * PUT /api/admin/stock/{itemCode}
     *
     * Body 예시 - 직접 설정: {"cnt": 100}
     * Body 예시 - 증감:      {"adjustment": -5}
     */
    @PutMapping("/{itemCode}")
    public ResponseEntity<StockResponse> updateStock(
            @PathVariable Integer itemCode,
            @RequestBody StockUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("재고 수정 API - itemCode: {}, user: {}", itemCode, userDetails.getUsername());

        StockResponse stock = adminStockService.updateStock(itemCode, request, userDetails.getUsername());
        return ResponseEntity.ok(stock);
    }
}
