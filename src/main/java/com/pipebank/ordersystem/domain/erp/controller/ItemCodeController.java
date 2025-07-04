package com.pipebank.ordersystem.domain.erp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.erp.dto.ItemCodeResponse;
import com.pipebank.ordersystem.domain.erp.dto.ItemCodeSearchRequest;
import com.pipebank.ordersystem.domain.erp.service.ItemCodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/erp/items")
@RequiredArgsConstructor
public class ItemCodeController {
    
    private final ItemCodeService itemCodeService;
    
    /**
     * 모든 품목 조회 (기존 - 호환성 유지)
     */
    @GetMapping
    public ResponseEntity<List<ItemCodeResponse>> getAllItems() {
        List<ItemCodeResponse> items = itemCodeService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    /**
     * 모든 품목 페이징 조회
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<ItemCodeResponse>> getAllItemsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemCodeResponse> items = itemCodeService.getAllItemsPaged(page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 품목 코드로 조회
     */
    @GetMapping("/{itemCode}")
    public ResponseEntity<ItemCodeResponse> getItemByCode(@PathVariable Integer itemCode) {
        return itemCodeService.getItemByCode(itemCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 품목번호로 조회
     */
    @GetMapping("/by-num/{itemNum}")
    public ResponseEntity<ItemCodeResponse> getItemByNum(@PathVariable String itemNum) {
        return itemCodeService.getItemByNum(itemNum)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 사용중인 품목만 조회 (기존 - 호환성 유지)
     */
    @GetMapping("/active")
    public ResponseEntity<List<ItemCodeResponse>> getActiveItems() {
        List<ItemCodeResponse> items = itemCodeService.getActiveItems();
        return ResponseEntity.ok(items);
    }
    
    /**
     * 사용중인 품목 페이징 조회
     */
    @GetMapping("/active/paged")
    public ResponseEntity<Page<ItemCodeResponse>> getActiveItemsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemCodeResponse> items = itemCodeService.getActiveItemsPaged(page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 오더센터 사용 품목 조회
     */
    @GetMapping("/orderable")
    public ResponseEntity<List<ItemCodeResponse>> getOrderableItems() {
        List<ItemCodeResponse> items = itemCodeService.getOrderableItems();
        return ResponseEntity.ok(items);
    }
    
    /**
     * 재고 관리 품목 조회
     */
    @GetMapping("/stock-managed")
    public ResponseEntity<List<ItemCodeResponse>> getStockManagedItems() {
        List<ItemCodeResponse> items = itemCodeService.getStockManagedItems();
        return ResponseEntity.ok(items);
    }
    
    /**
     * 키워드로 품목 검색 (기존 - 호환성 유지)
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemCodeResponse>> searchItems(@RequestParam String keyword) {
        List<ItemCodeResponse> items = itemCodeService.searchItems(keyword);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 키워드로 품목 검색 (페이징)
     */
    @GetMapping("/search/paged")
    public ResponseEntity<Page<ItemCodeResponse>> searchItemsPaged(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemCodeResponse> items = itemCodeService.searchItemsPaged(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 사용중인 품목 키워드 검색 (기존 - 호환성 유지)
     */
    @GetMapping("/search/active")
    public ResponseEntity<List<ItemCodeResponse>> searchActiveItems(@RequestParam String keyword) {
        List<ItemCodeResponse> items = itemCodeService.searchActiveItems(keyword);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 사용중인 품목 키워드 검색 (페이징)
     */
    @GetMapping("/search/active/paged")
    public ResponseEntity<Page<ItemCodeResponse>> searchActiveItemsPaged(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemCodeResponse> items = itemCodeService.searchActiveItemsPaged(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 제품 분류별 품목 조회
     */
    @GetMapping("/by-division")
    public ResponseEntity<List<ItemCodeResponse>> getItemsByProductDivision(
            @RequestParam(required = false) String div1,
            @RequestParam(required = false) String div2,
            @RequestParam(required = false) String div3,
            @RequestParam(required = false) String div4) {
        List<ItemCodeResponse> items = itemCodeService.getItemsByProductDivision(div1, div2, div3, div4);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 브랜드별 품목 조회
     */
    @GetMapping("/by-brand")
    public ResponseEntity<List<ItemCodeResponse>> getItemsByBrand(@RequestParam String brand) {
        List<ItemCodeResponse> items = itemCodeService.getItemsByBrand(brand);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 매입처별 품목 조회
     */
    @GetMapping("/by-purchase-customer/{custCode}")
    public ResponseEntity<List<ItemCodeResponse>> getItemsByPurchaseCustomer(@PathVariable Integer custCode) {
        List<ItemCodeResponse> items = itemCodeService.getItemsByPurchaseCustomer(custCode);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 매출처별 품목 조회
     */
    @GetMapping("/by-sales-customer/{custCode}")
    public ResponseEntity<List<ItemCodeResponse>> getItemsBySalesCustomer(@PathVariable Integer custCode) {
        List<ItemCodeResponse> items = itemCodeService.getItemsBySalesCustomer(custCode);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 품목 코드 범위 조회
     */
    @GetMapping("/by-code-range")
    public ResponseEntity<List<ItemCodeResponse>> getItemsByCodeRange(
            @RequestParam Integer startCode,
            @RequestParam Integer endCode) {
        List<ItemCodeResponse> items = itemCodeService.getItemsByCodeRange(startCode, endCode);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 복합 검색 조건으로 품목 조회 (기존 - 호환성 유지)
     */
    @PostMapping("/search/advanced")
    public ResponseEntity<List<ItemCodeResponse>> searchItemsWithConditions(@RequestBody ItemCodeSearchRequest request) {
        List<ItemCodeResponse> items = itemCodeService.searchItemsWithConditions(request);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 복합 검색 조건으로 품목 조회 (페이징)
     */
    @PostMapping("/search/advanced/paged")
    public ResponseEntity<Page<ItemCodeResponse>> searchItemsWithConditionsPaged(
            @RequestBody ItemCodeSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemCodeResponse> items = itemCodeService.searchItemsWithConditionsPaged(request, page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 품목 통계 정보 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<ItemStatsResponse> getItemStats() {
        List<ItemCodeResponse> allItems = itemCodeService.getAllItems();
        List<ItemCodeResponse> activeItems = itemCodeService.getActiveItems();
        List<ItemCodeResponse> orderableItems = itemCodeService.getOrderableItems();
        List<ItemCodeResponse> stockManagedItems = itemCodeService.getStockManagedItems();
        
        ItemStatsResponse stats = ItemStatsResponse.builder()
                .totalItems(allItems.size())
                .activeItems(activeItems.size())
                .orderableItems(orderableItems.size())
                .stockManagedItems(stockManagedItems.size())
                .build();
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 품목 통계 응답 DTO
     */
    public static class ItemStatsResponse {
        private int totalItems;
        private int activeItems;
        private int orderableItems;
        private int stockManagedItems;
        
        public static ItemStatsResponseBuilder builder() {
            return new ItemStatsResponseBuilder();
        }
        
        public static class ItemStatsResponseBuilder {
            private int totalItems;
            private int activeItems;
            private int orderableItems;
            private int stockManagedItems;
            
            public ItemStatsResponseBuilder totalItems(int totalItems) {
                this.totalItems = totalItems;
                return this;
            }
            
            public ItemStatsResponseBuilder activeItems(int activeItems) {
                this.activeItems = activeItems;
                return this;
            }
            
            public ItemStatsResponseBuilder orderableItems(int orderableItems) {
                this.orderableItems = orderableItems;
                return this;
            }
            
            public ItemStatsResponseBuilder stockManagedItems(int stockManagedItems) {
                this.stockManagedItems = stockManagedItems;
                return this;
            }
            
            public ItemStatsResponse build() {
                ItemStatsResponse response = new ItemStatsResponse();
                response.totalItems = this.totalItems;
                response.activeItems = this.activeItems;
                response.orderableItems = this.orderableItems;
                response.stockManagedItems = this.stockManagedItems;
                return response;
            }
        }
        
        // Getters
        public int getTotalItems() { return totalItems; }
        public int getActiveItems() { return activeItems; }
        public int getOrderableItems() { return orderableItems; }
        public int getStockManagedItems() { return stockManagedItems; }
    }
} 