package com.weborder.ordersystem.domain.web.customitem.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.weborder.ordersystem.domain.web.customitem.dto.CustomItemPriceRequest;
import com.weborder.ordersystem.domain.web.customitem.dto.CustomItemRequest;
import com.weborder.ordersystem.domain.web.customitem.dto.CustomItemResponse;
import com.weborder.ordersystem.domain.web.customitem.service.CustomItemService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.erp.entity.OrderTran;
import com.weborder.ordersystem.domain.erp.repository.OrderTranRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/custom-items")
@RequiredArgsConstructor
public class CustomItemController {

    private final CustomItemService customItemService;
    private final MemberRepository memberRepository;
    private final OrderTranRepository erpOrderTranRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomItemResponse> createCustomItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") @Valid CustomItemRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        Member member = getMember(userDetails.getUsername());
        Integer custCode = parseCustCode(member.getCustCode());
        if (custCode == null || custCode <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CustomItemResponse response = customItemService.createCustomItem(
                custCode, request, images, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomItemResponse>> getMyCustomItems(
            @AuthenticationPrincipal UserDetails userDetails) {

        Member member = getMember(userDetails.getUsername());
        Integer custCode = parseCustCode(member.getCustCode());
        if (custCode == null || custCode <= 0) {
            return ResponseEntity.ok(List.of());
        }

        List<CustomItemResponse> items = customItemService.getCustomItems(custCode);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomItemResponse> getCustomItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        CustomItemResponse response = customItemService.getCustomItem(id);
        if (!isAdminOrOwner(userDetails, response.getCustCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCustomItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        CustomItemResponse item = customItemService.getCustomItem(id);
        if (!isAdminOrOwner(userDetails, item.getCustCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        customItemService.deactivateCustomItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/price")
    @Transactional(transactionManager = "erpTransactionManager")
    public ResponseEntity<CustomItemResponse> updatePrice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody @Valid CustomItemPriceRequest request) {

        String loginId = userDetails.getUsername();
        CustomItemResponse response = customItemService.updatePrice(id, request, loginId);

        if (request.getOrderDate() != null && request.getOrderSeq() != null) {
            try {
                OrderTran.OrderTranId erpTranId = new OrderTran.OrderTranId(
                        request.getOrderDate(), request.getOrderSosok(),
                        request.getOrderUjcd(), request.getOrderAcno(), request.getOrderSeq());
                erpOrderTranRepository.findById(erpTranId).ifPresent(erpTran -> {
                    erpTran.updateCustomItemPrice(request.getSrate(), request.getSpec(), request.getUnit(), request.getVatDiv(), loginId);
                    erpOrderTranRepository.save(erpTran);
                });
            } catch (Exception e) {
                log.warn("ERP 주문 트랜 가격 업데이트 실패 - customItemId: {}", id, e);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/files/{imageId}")
    public ResponseEntity<Resource> serveImage(@PathVariable Long imageId) throws IOException {
        Resource resource = customItemService.loadImageFile(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(resource);
    }

    private Member getMember(String loginId) {
        return memberRepository.findByMemberId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }

    private Integer parseCustCode(String custCode) {
        if (custCode == null || custCode.isBlank()) return null;
        try {
            return Integer.parseInt(custCode.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isAdminOrOwner(UserDetails userDetails, Integer itemCustCode) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;
        Member member = getMember(userDetails.getUsername());
        Integer myCustCode = parseCustCode(member.getCustCode());
        return myCustCode != null && myCustCode.equals(itemCustCode);
    }
}
