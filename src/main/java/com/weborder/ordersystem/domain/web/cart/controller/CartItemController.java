package com.weborder.ordersystem.domain.web.cart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

import com.weborder.ordersystem.domain.web.cart.dto.CartItemRequest;
import com.weborder.ordersystem.domain.web.cart.dto.CartItemResponse;
import com.weborder.ordersystem.domain.web.cart.service.CartItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * 장바구니 담기
     */
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        log.info("장바구니 담기 API - user: {}, itemCode: {}", userDetails.getUsername(), request.getItemCode());

        CartItemResponse response = cartItemService.addToCart(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 장바구니 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("장바구니 조회 API - user: {}", userDetails.getUsername());

        List<CartItemResponse> items = cartItemService.getCartItems(userDetails.getUsername());
        return ResponseEntity.ok(items);
    }

    /**
     * 장바구니 수량 변경
     */
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        log.info("장바구니 수량 변경 API - user: {}, id: {}, qty: {}", userDetails.getUsername(), cartItemId, quantity);

        CartItemResponse response = cartItemService.updateQuantity(
                userDetails.getUsername(), cartItemId, quantity);
        return ResponseEntity.ok(response);
    }

    /**
     * 장바구니 단건 삭제
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        log.info("장바구니 삭제 API - user: {}, id: {}", userDetails.getUsername(), cartItemId);

        cartItemService.removeFromCart(userDetails.getUsername(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 장바구니 전체 비우기
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("장바구니 전체 비우기 API - user: {}", userDetails.getUsername());

        cartItemService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /**
     * 장바구니 품목 수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCartItemCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = cartItemService.getCartItemCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
