package com.weborder.ordersystem.domain.web.product.controller;

import com.weborder.ordersystem.domain.web.product.dto.UserProductListResponse;
import com.weborder.ordersystem.domain.web.product.service.UserProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class UserProductController {

    private final UserProductService userProductService;

    @GetMapping
    public ResponseEntity<Page<UserProductListResponse>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String scod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "itemCodeHnam") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Integer custCode) {
        List<String> scodList = (scod != null && !scod.isBlank())
                ? Arrays.asList(scod.split(","))
                : null;
        log.info("사용자 상품 목록 API - keyword: {}, scod: {}, page: {}, size: {}, custCode: {}",
                keyword, scodList, page, size, custCode);

        Page<UserProductListResponse> result = userProductService.getProductList(
                keyword, scodList, page, size, sort, direction, custCode);
        return ResponseEntity.ok(result);
    }
}
