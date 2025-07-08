package com.pipebank.ordersystem.domain.web.order.controller;

import com.pipebank.ordersystem.domain.web.order.dto.WebOrderMastCreateRequest;
import com.pipebank.ordersystem.domain.web.order.dto.WebOrderMastResponse;
import com.pipebank.ordersystem.domain.web.order.service.WebOrderMastService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/web/orders")
@RequiredArgsConstructor
public class WebOrderMastController {

    private final WebOrderMastService webOrderMastService;

    /**
     * 주문 생성
     */
    @PostMapping
    public ResponseEntity<WebOrderMastResponse> createOrder(@Valid @RequestBody WebOrderMastCreateRequest request) {
        log.info("주문 생성 API 호출: {}", request);
        
        WebOrderMastResponse response = webOrderMastService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 전체 주문 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<WebOrderMastResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderMastDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("전체 주문 조회 API 호출: page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderMastResponse> orders = webOrderMastService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 주문일자로 조회 (페이징)
     */
    @GetMapping("/date/{orderDate}")
    public ResponseEntity<Page<WebOrderMastResponse>> getOrdersByDate(
            @PathVariable String orderDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderMastAcno") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("주문일자 조회 API 호출: orderDate={}, page={}, size={}", 
                orderDate, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderMastResponse> orders = webOrderMastService.getOrdersByDate(orderDate, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 거래처로 조회 (페이징)
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<WebOrderMastResponse>> getOrdersByCustomer(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderMastDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("거래처 조회 API 호출: customerId={}, page={}, size={}", 
                customerId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderMastResponse> orders = webOrderMastService.getOrdersByCustomer(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 단건 조회
     */
    @GetMapping("/{orderDate}/{sosok}/{ujcd}/{acno}")
    public ResponseEntity<WebOrderMastResponse> getOrder(
            @PathVariable String orderDate,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno) {
        
        log.info("단건 조회 API 호출: {}-{}-{}-{}", orderDate, sosok, ujcd, acno);

        Optional<WebOrderMastResponse> order = webOrderMastService.getOrder(orderDate, sosok, ujcd, acno);
        
        return order.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
} 