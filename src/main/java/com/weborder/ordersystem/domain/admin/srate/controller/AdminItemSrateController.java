package com.weborder.ordersystem.domain.admin.srate.controller;

import com.weborder.ordersystem.domain.admin.srate.dto.ItemSrateRequest;
import com.weborder.ordersystem.domain.admin.srate.dto.ItemSrateResponse;
import com.weborder.ordersystem.domain.admin.srate.service.AdminItemSrateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/item-srate")
@RequiredArgsConstructor
public class AdminItemSrateController {

    private final AdminItemSrateService adminItemSrateService;

    @GetMapping("/{custCode}")
    public ResponseEntity<List<ItemSrateResponse>> getAllItemsWithRate(@PathVariable Integer custCode) {
        log.info("거래처별 판매단가 전체 품목 조회 - custCode: {}", custCode);
        return ResponseEntity.ok(adminItemSrateService.getAllItemsWithCustomerRate(custCode));
    }

    @GetMapping("/{custCode}/set")
    public ResponseEntity<List<ItemSrateResponse>> getCustomerRatesOnly(@PathVariable Integer custCode) {
        log.info("거래처별 판매단가 설정된 품목만 조회 - custCode: {}", custCode);
        return ResponseEntity.ok(adminItemSrateService.getCustomerRates(custCode));
    }

    @PostMapping
    public ResponseEntity<ItemSrateResponse> saveOrUpdate(
            @Valid @RequestBody ItemSrateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminUser = userDetails.getUsername();
        return ResponseEntity.ok(adminItemSrateService.saveOrUpdate(request, adminUser));
    }

    @DeleteMapping("/{itemCode}/{custCode}")
    public ResponseEntity<Void> delete(@PathVariable Integer itemCode, @PathVariable Integer custCode) {
        adminItemSrateService.delete(itemCode, custCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{custCode}/hide-price")
    public ResponseEntity<Map<String, Boolean>> getHidePrice(@PathVariable Integer custCode) {
        boolean hidePrice = adminItemSrateService.isHidePrice(custCode);
        return ResponseEntity.ok(Map.of("hidePrice", hidePrice));
    }

    @PatchMapping("/{custCode}/hide-price")
    public ResponseEntity<Void> updateHidePrice(
            @PathVariable Integer custCode,
            @RequestBody Map<String, Boolean> body) {
        Boolean hidePrice = body.get("hidePrice");
        if (hidePrice == null) {
            return ResponseEntity.badRequest().build();
        }
        adminItemSrateService.updateHidePrice(custCode, hidePrice);
        return ResponseEntity.ok().build();
    }
}
