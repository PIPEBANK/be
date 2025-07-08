package com.pipebank.ordersystem.domain.web.order.controller;

import com.pipebank.ordersystem.domain.web.order.dto.WebOrderTranCreateRequest;
import com.pipebank.ordersystem.domain.web.order.dto.WebOrderTranResponse;
import com.pipebank.ordersystem.domain.web.order.service.WebOrderTranService;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/web/order-trans")
@RequiredArgsConstructor
public class WebOrderTranController {

    private final WebOrderTranService webOrderTranService;

    /**
     * 주문 상세 생성
     */
    @PostMapping
    public ResponseEntity<WebOrderTranResponse> createOrderTran(@Valid @RequestBody WebOrderTranCreateRequest request) {
        log.info("주문 상세 생성 API 호출: {}", request);
        
        WebOrderTranResponse response = webOrderTranService.createOrderTran(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 전체 주문 상세 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<WebOrderTranResponse>> getAllOrderTrans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderTranDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("전체 주문 상세 조회 API 호출: page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderTranResponse> orderTrans = webOrderTranService.getAllOrderTrans(pageable);
        return ResponseEntity.ok(orderTrans);
    }

    /**
     * 특정 주문의 모든 상세 조회
     */
    @GetMapping("/order/{orderDate}/{sosok}/{ujcd}/{acno}")
    public ResponseEntity<List<WebOrderTranResponse>> getOrderTransByOrder(
            @PathVariable String orderDate,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno) {
        
        log.info("특정 주문 상세 조회 API 호출: {}-{}-{}-{}", orderDate, sosok, ujcd, acno);

        List<WebOrderTranResponse> orderTrans = webOrderTranService.getOrderTransByOrder(orderDate, sosok, ujcd, acno);
        return ResponseEntity.ok(orderTrans);
    }

    /**
     * 특정 주문의 모든 상세 조회 (페이징)
     */
    @GetMapping("/order/{orderDate}/{sosok}/{ujcd}/{acno}/paged")
    public ResponseEntity<Page<WebOrderTranResponse>> getOrderTransByOrderPaged(
            @PathVariable String orderDate,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderTranSeq") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("특정 주문 상세 조회 (페이징) API 호출: {}-{}-{}-{}, page={}, size={}", 
                orderDate, sosok, ujcd, acno, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderTranResponse> orderTrans = webOrderTranService.getOrderTransByOrder(orderDate, sosok, ujcd, acno, pageable);
        return ResponseEntity.ok(orderTrans);
    }

    /**
     * 품목코드로 조회 (페이징)
     */
    @GetMapping("/item/{itemCode}")
    public ResponseEntity<Page<WebOrderTranResponse>> getOrderTransByItem(
            @PathVariable Integer itemCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderTranDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("품목코드 조회 API 호출: itemCode={}, page={}, size={}", 
                itemCode, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderTranResponse> orderTrans = webOrderTranService.getOrderTransByItem(itemCode, pageable);
        return ResponseEntity.ok(orderTrans);
    }

    /**
     * 상태코드로 조회 (페이징)
     */
    @GetMapping("/status/{statusCode}")
    public ResponseEntity<Page<WebOrderTranResponse>> getOrderTransByStatus(
            @PathVariable String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderTranDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("상태코드 조회 API 호출: statusCode={}, page={}, size={}", 
                statusCode, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WebOrderTranResponse> orderTrans = webOrderTranService.getOrderTransByStatus(statusCode, pageable);
        return ResponseEntity.ok(orderTrans);
    }

    /**
     * 단건 조회
     */
    @GetMapping("/{orderDate}/{sosok}/{ujcd}/{acno}/{seq}")
    public ResponseEntity<WebOrderTranResponse> getOrderTran(
            @PathVariable String orderDate,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno,
            @PathVariable Integer seq) {
        
        log.info("단건 조회 API 호출: {}-{}-{}-{}-{}", orderDate, sosok, ujcd, acno, seq);

        Optional<WebOrderTranResponse> orderTran = webOrderTranService.getOrderTran(orderDate, sosok, ujcd, acno, seq);
        
        return orderTran.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
} 