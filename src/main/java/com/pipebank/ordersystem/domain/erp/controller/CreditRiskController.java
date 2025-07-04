package com.pipebank.ordersystem.domain.erp.controller;

import com.pipebank.ordersystem.domain.erp.dto.CreditRiskResponse;
import com.pipebank.ordersystem.domain.erp.dto.CreditRiskSearchRequest;
import com.pipebank.ordersystem.domain.erp.service.CreditRiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/erp/credit-risks")
@RequiredArgsConstructor
@Slf4j
public class CreditRiskController {

    private final CreditRiskService creditRiskService;

    /**
     * 전체 신용 리스크 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<CreditRiskResponse>> getAllCreditRisks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("전체 신용 리스크 조회 요청 - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Page<CreditRiskResponse> result = creditRiskService.findAll(page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * ID로 신용 리스크 조회
     */
    @GetMapping("/{sosok}/{cust}/{seq}")
    public ResponseEntity<CreditRiskResponse> getCreditRiskById(
            @PathVariable Integer sosok,
            @PathVariable Integer cust,
            @PathVariable Integer seq) {
        
        log.info("신용 리스크 ID 조회 요청 - sosok: {}, cust: {}, seq: {}", sosok, cust, seq);
        
        Optional<CreditRiskResponse> result = creditRiskService.findById(sosok, cust, seq);
        return result.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 소속별 신용 리스크 조회
     */
    @GetMapping("/sosok/{sosok}")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksBySosok(
            @PathVariable Integer sosok,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskCust") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("소속별 신용 리스크 조회 요청 - sosok: {}, page: {}, size: {}", sosok, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findBySosok(sosok, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 거래처별 신용 리스크 조회
     */
    @GetMapping("/customer/{cust}")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksByCust(
            @PathVariable Integer cust,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("거래처별 신용 리스크 조회 요청 - cust: {}, page: {}, size: {}", cust, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findByCust(cust, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 소속+거래처 신용 리스크 조회
     */
    @GetMapping("/sosok/{sosok}/customer/{cust}")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksBySosokAndCust(
            @PathVariable Integer sosok,
            @PathVariable Integer cust,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSeq") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("소속+거래처별 신용 리스크 조회 요청 - sosok: {}, cust: {}, page: {}, size: {}", sosok, cust, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findBySosokAndCust(sosok, cust, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 상태별 신용 리스크 조회
     */
    @GetMapping("/status/{stau}")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksByStau(
            @PathVariable String stau,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("상태별 신용 리스크 조회 요청 - stau: {}, page: {}, size: {}", stau, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findByStau(stau, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 판매일자별 신용 리스크 조회
     */
    @GetMapping("/sale-date/{saleDate}")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksBySaleDate(
            @PathVariable String saleDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("판매일자별 신용 리스크 조회 요청 - saleDate: {}, page: {}, size: {}", saleDate, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findBySaleDate(saleDate, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 기간별 신용 리스크 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSdate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("기간별 신용 리스크 조회 요청 - startDate: {}, endDate: {}, page: {}, size: {}", 
                startDate, endDate, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findByDateRange(startDate, endDate, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 신용한도 범위별 신용 리스크 조회
     */
    @GetMapping("/limit-range")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksByLimitRange(
            @RequestParam BigDecimal minLimit,
            @RequestParam(required = false) BigDecimal maxLimit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskLimitLimit") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("신용한도 범위별 신용 리스크 조회 요청 - minLimit: {}, maxLimit: {}, page: {}, size: {}", 
                minLimit, maxLimit, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findByLimitRange(minLimit, maxLimit, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 미수채권 범위별 신용 리스크 조회
     */
    @GetMapping("/unrecv-bond")
    public ResponseEntity<Page<CreditRiskResponse>> getCreditRisksByUnrecvBond(
            @RequestParam BigDecimal minBond,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskUnrecvBond") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("미수채권 범위별 신용 리스크 조회 요청 - minBond: {}, page: {}, size: {}", minBond, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findByUnrecvBond(minBond, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 키워드 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CreditRiskResponse>> searchCreditRisks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditRiskSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("신용 리스크 키워드 검색 요청 - keyword: {}, page: {}, size: {}", keyword, page, size);
        
        Page<CreditRiskResponse> result = creditRiskService.findByKeyword(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 복합 조건 검색
     */
    @PostMapping("/search/advanced")
    public ResponseEntity<Page<CreditRiskResponse>> searchCreditRisksAdvanced(
            @RequestBody CreditRiskSearchRequest request) {
        
        log.info("신용 리스크 복합 검색 요청 - request: {}", request);
        
        Page<CreditRiskResponse> result = creditRiskService.findBySearchConditions(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 소속별 신용 리스크 수 조회
     */
    @GetMapping("/count/sosok/{sosok}")
    public ResponseEntity<Long> countBySosok(@PathVariable Integer sosok) {
        log.info("소속별 신용 리스크 수 조회 요청 - sosok: {}", sosok);
        
        long count = creditRiskService.countBySosok(sosok);
        return ResponseEntity.ok(count);
    }

    /**
     * 거래처별 신용 리스크 수 조회
     */
    @GetMapping("/count/customer/{cust}")
    public ResponseEntity<Long> countByCust(@PathVariable Integer cust) {
        log.info("거래처별 신용 리스크 수 조회 요청 - cust: {}", cust);
        
        long count = creditRiskService.countByCust(cust);
        return ResponseEntity.ok(count);
    }

    /**
     * 상태별 신용 리스크 수 조회
     */
    @GetMapping("/count/status/{stau}")
    public ResponseEntity<Long> countByStau(@PathVariable String stau) {
        log.info("상태별 신용 리스크 수 조회 요청 - stau: {}", stau);
        
        long count = creditRiskService.countByStau(stau);
        return ResponseEntity.ok(count);
    }

    /**
     * 소속별 상태 통계
     */
    @GetMapping("/stats/status/sosok/{sosok}")
    public ResponseEntity<List<Object[]>> getStatusStatsBySosok(@PathVariable Integer sosok) {
        log.info("소속별 상태 통계 조회 요청 - sosok: {}", sosok);
        
        List<Object[]> stats = creditRiskService.getStatusStatsBySosok(sosok);
        return ResponseEntity.ok(stats);
    }

    /**
     * 거래처별 상태 통계
     */
    @GetMapping("/stats/status/customer/{cust}")
    public ResponseEntity<List<Object[]>> getStatusStatsByCust(@PathVariable Integer cust) {
        log.info("거래처별 상태 통계 조회 요청 - cust: {}", cust);
        
        List<Object[]> stats = creditRiskService.getStatusStatsByCust(cust);
        return ResponseEntity.ok(stats);
    }

    /**
     * 전체 상태별 통계
     */
    @GetMapping("/stats/status")
    public ResponseEntity<List<Object[]>> getStatusStats() {
        log.info("전체 상태별 통계 조회 요청");
        
        List<Object[]> stats = creditRiskService.getStatusStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 신용한도 통계
     */
    @GetMapping("/stats/limit/sosok/{sosok}")
    public ResponseEntity<List<Object[]>> getLimitStatsBySosok(@PathVariable Integer sosok) {
        log.info("소속별 신용한도 통계 조회 요청 - sosok: {}", sosok);
        
        List<Object[]> stats = creditRiskService.getLimitStatsBySosok(sosok);
        return ResponseEntity.ok(stats);
    }

    /**
     * 미수채권 통계
     */
    @GetMapping("/stats/unrecv-bond/sosok/{sosok}")
    public ResponseEntity<List<Object[]>> getUnrecvBondStatsBySosok(@PathVariable Integer sosok) {
        log.info("소속별 미수채권 통계 조회 요청 - sosok: {}", sosok);
        
        List<Object[]> stats = creditRiskService.getUnrecvBondStatsBySosok(sosok);
        return ResponseEntity.ok(stats);
    }

    /**
     * 월별 통계
     */
    @GetMapping("/stats/monthly")
    public ResponseEntity<List<Object[]>> getMonthlyStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("월별 통계 조회 요청 - startDate: {}, endDate: {}", startDate, endDate);
        
        List<Object[]> stats = creditRiskService.getMonthlyStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}