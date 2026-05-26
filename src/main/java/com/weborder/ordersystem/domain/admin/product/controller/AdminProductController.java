package com.weborder.ordersystem.domain.admin.product.controller;

import com.weborder.ordersystem.domain.admin.product.dto.ProductCreateRequest;
import com.weborder.ordersystem.domain.admin.product.dto.ProductDetailResponse;
import com.weborder.ordersystem.domain.admin.product.dto.ProductListResponse;
import com.weborder.ordersystem.domain.admin.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * 제품 목록 조회
     * GET /api/admin/products?keyword=테스트&useOnly=true&page=0&size=20&sort=itemCodeCode&direction=desc
     */
    @GetMapping
    public ResponseEntity<Page<ProductListResponse>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer useStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("제품 목록 조회 API - keyword: {}, useStatus: {}, sort: {} {}", keyword, useStatus, sort, direction);

        Page<ProductListResponse> products = adminProductService.getProductList(keyword, useStatus, page, size, sort, direction);
        return ResponseEntity.ok(products);
    }

    /**
     * 제품 상세 조회
     * GET /api/admin/products/{itemCode}
     */
    @GetMapping("/{itemCode}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
            @PathVariable Integer itemCode) {
        log.info("제품 상세 조회 API - itemCode: {}", itemCode);

        ProductDetailResponse product = adminProductService.getProductDetail(itemCode);
        return ResponseEntity.ok(product);
    }

    /**
     * 제품 등록
     * POST /api/admin/products
     */
    @PostMapping
    public ResponseEntity<ProductDetailResponse> createProduct(
            @RequestBody ProductCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("제품 등록 API - itemName: {}, user: {}", request.getItemName(), userDetails.getUsername());

        ProductDetailResponse product = adminProductService.createProduct(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * 제품 수정
     * PUT /api/admin/products/{itemCode}
     */
    @PutMapping("/{itemCode}")
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @PathVariable Integer itemCode,
            @RequestBody ProductCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("제품 수정 API - itemCode: {}, user: {}", itemCode, userDetails.getUsername());

        ProductDetailResponse product = adminProductService.updateProduct(itemCode, request, userDetails.getUsername());
        return ResponseEntity.ok(product);
    }

    /**
     * 제품 사용여부 변경
     * PATCH /api/admin/products/{itemCode}/use
     */
    @PatchMapping("/{itemCode}/use")
    public ResponseEntity<ProductDetailResponse> updateProductUse(
            @PathVariable Integer itemCode,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer use = body.get("use");
        log.info("제품 사용여부 변경 API - itemCode: {}, use: {}", itemCode, use);

        ProductDetailResponse product = adminProductService.updateProductUse(itemCode, use, userDetails.getUsername());
        return ResponseEntity.ok(product);
    }
}
