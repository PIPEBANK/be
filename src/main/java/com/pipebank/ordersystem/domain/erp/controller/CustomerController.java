package com.pipebank.ordersystem.domain.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.erp.dto.CustomerResponse;
import com.pipebank.ordersystem.domain.erp.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/erp/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 거래처 조회 (ID)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Integer id) {
        log.info("거래처 조회 API 호출 - ID: {}", id);
        CustomerResponse response = customerService.getCustomer(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처 조회 (거래처 번호)
     */
    @GetMapping("/by-num/{custCodeNum}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CustomerResponse> getCustomerByNum(@PathVariable String custCodeNum) {
        log.info("거래처 조회 API 호출 - 거래처번호: {}", custCodeNum);
        CustomerResponse response = customerService.getCustomerByNum(custCodeNum);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 거래처 목록 조회 (페이징)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "custCodeCode") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.info("전체 거래처 목록 조회 API 호출 - 페이지: {}, 크기: {}", page, size);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<CustomerResponse> response = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 활성 거래처 목록 조회 (페이징 + 통합검색)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<CustomerResponse>> getActiveCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "custCodeCode") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.info("활성 거래처 목록 조회 API 호출 - 검색어: {}, 페이지: {}, 크기: {}", search, page, size);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<CustomerResponse> response = customerService.getActiveCustomersWithSearch(search, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 구매 가능한 거래처 목록 조회
     */
    @GetMapping("/purchaseable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CustomerResponse>> getPurchaseableCustomers() {
        log.info("구매 가능한 거래처 목록 조회 API 호출");
        List<CustomerResponse> response = customerService.getPurchaseableCustomers();
        return ResponseEntity.ok(response);
    }

    /**
     * POS 사용 가능한 거래처 목록 조회
     */
    @GetMapping("/pos-available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CustomerResponse>> getPosAvailableCustomers() {
        log.info("POS 사용 가능한 거래처 목록 조회 API 호출");
        List<CustomerResponse> response = customerService.getPosAvailableCustomers();
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처 이름으로 검색
     */
    @GetMapping("/search/by-name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CustomerResponse>> searchCustomersByName(
            @RequestParam String name) {
        log.info("거래처 이름 검색 API 호출 - 검색어: {}", name);
        List<CustomerResponse> response = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(response);
    }

    /**
     * 조건별 거래처 검색
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(
            @RequestParam(required = false) String custCodeNum,
            @RequestParam(required = false) String custCodeName,
            @RequestParam(required = false) String custCodeDcod,
            @RequestParam(required = false) Integer useAcc,
            @RequestParam(required = false) Integer usePur,
            @RequestParam(required = false) Integer usePos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "custCodeCode") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.info("조건별 거래처 검색 API 호출 - 번호: {}, 이름: {}, 구분: {}", custCodeNum, custCodeName, custCodeDcod);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<CustomerResponse> response = customerService.searchCustomers(
                custCodeNum, custCodeName, custCodeDcod, useAcc, usePur, usePos, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 담당자별 거래처 조회
     */
    @GetMapping("/by-sawon/{sawonId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CustomerResponse>> getCustomersBySawon(@PathVariable Integer sawonId) {
        log.info("담당자별 거래처 조회 API 호출 - 담당자 ID: {}", sawonId);
        List<CustomerResponse> response = customerService.getCustomersBySawon(sawonId);
        return ResponseEntity.ok(response);
    }

    /**
     * 사업부별 거래처 조회
     */
    @GetMapping("/by-buse/{buseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CustomerResponse>> getCustomersByBuse(@PathVariable Integer buseId) {
        log.info("사업부별 거래처 조회 API 호출 - 사업부 ID: {}", buseId);
        List<CustomerResponse> response = customerService.getCustomersByBuse(buseId);
        return ResponseEntity.ok(response);
    }

    /**
     * 지역별 거래처 조회
     */
    @GetMapping("/by-local/{local}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CustomerResponse>> getCustomersByLocal(@PathVariable String local) {
        log.info("지역별 거래처 조회 API 호출 - 지역: {}", local);
        List<CustomerResponse> response = customerService.getCustomersByLocal(local);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처 통계 정보
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerService.CustomerStatistics> getCustomerStatistics() {
        log.info("거래처 통계 정보 조회 API 호출");
        CustomerService.CustomerStatistics statistics = customerService.getCustomerStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 담당자별 거래처 수 조회
     */
    @GetMapping("/count/by-sawon/{sawonId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getCustomerCountBySawon(@PathVariable Integer sawonId) {
        log.info("담당자별 거래처 수 조회 API 호출 - 담당자 ID: {}", sawonId);
        
        long count = customerService.getCustomerCountBySawon(sawonId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("sawonId", sawonId.longValue());
        response.put("customerCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 사업부별 거래처 수 조회
     */
    @GetMapping("/count/by-buse/{buseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getCustomerCountByBuse(@PathVariable Integer buseId) {
        log.info("사업부별 거래처 수 조회 API 호출 - 사업부 ID: {}", buseId);
        
        long count = customerService.getCustomerCountByBuse(buseId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("buseId", buseId.longValue());
        response.put("customerCount", count);
        
        return ResponseEntity.ok(response);
    }
} 