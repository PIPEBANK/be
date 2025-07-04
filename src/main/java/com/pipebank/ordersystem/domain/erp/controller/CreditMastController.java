package com.pipebank.ordersystem.domain.erp.controller;

import com.pipebank.ordersystem.domain.erp.dto.CreditMastResponse;
import com.pipebank.ordersystem.domain.erp.dto.CreditMastSearchRequest;
import com.pipebank.ordersystem.domain.erp.service.CreditMastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/erp/credit-masts")
@RequiredArgsConstructor
@Slf4j
public class CreditMastController {

    private final CreditMastService creditMastService;

    /**
     * 전체 신용 마스터 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<CreditMastResponse>> getAllCreditMasts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("전체 신용 마스터 조회 요청 - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Page<CreditMastResponse> result = creditMastService.findAll(page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * ID로 신용 마스터 조회
     */
    @GetMapping("/{sosok}/{cust}")
    public ResponseEntity<CreditMastResponse> getCreditMastById(
            @PathVariable Integer sosok,
            @PathVariable Integer cust) {
        
        log.info("신용 마스터 ID 조회 요청 - sosok: {}, cust: {}", sosok, cust);
        
        Optional<CreditMastResponse> result = creditMastService.findById(sosok, cust);
        return result.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 소속별 신용 마스터 조회
     */
    @GetMapping("/sosok/{sosok}")
    public ResponseEntity<Page<CreditMastResponse>> getCreditMastsBySosok(
            @PathVariable Integer sosok,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastCust") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("소속별 신용 마스터 조회 요청 - sosok: {}, page: {}, size: {}", sosok, page, size);
        
        Page<CreditMastResponse> result = creditMastService.findBySosok(sosok, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 거래처별 신용 마스터 조회
     */
    @GetMapping("/customer/{cust}")
    public ResponseEntity<Page<CreditMastResponse>> getCreditMastsByCust(
            @PathVariable Integer cust,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("거래처별 신용 마스터 조회 요청 - cust: {}, page: {}, size: {}", cust, page, size);
        
        Page<CreditMastResponse> result = creditMastService.findByCust(cust, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 신용등급별 신용 마스터 조회
     */
    @GetMapping("/credit-rank/{creditRank}")
    public ResponseEntity<Page<CreditMastResponse>> getCreditMastsByCreditRank(
            @PathVariable String creditRank,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("신용등급별 신용 마스터 조회 요청 - creditRank: {}, page: {}, size: {}", creditRank, page, size);
        
        Page<CreditMastResponse> result = creditMastService.findByCreditRank(creditRank, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 신용점수별 신용 마스터 조회
     */
    @GetMapping("/credit-score/{creditScore}")
    public ResponseEntity<Page<CreditMastResponse>> getCreditMastsByCreditScore(
            @PathVariable String creditScore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("신용점수별 신용 마스터 조회 요청 - creditScore: {}, page: {}, size: {}", creditScore, page, size);
        
        Page<CreditMastResponse> result = creditMastService.findByCreditScore(creditScore, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 채권코드별 신용 마스터 조회
     */
    @GetMapping("/bond-code/{bondDcod}")
    public ResponseEntity<Page<CreditMastResponse>> getCreditMastsByBondDcod(
            @PathVariable String bondDcod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("채권코드별 신용 마스터 조회 요청 - bondDcod: {}, page: {}, size: {}", bondDcod, page, size);
        
        Page<CreditMastResponse> result = creditMastService.findByBondDcod(bondDcod, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 키워드 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CreditMastResponse>> searchCreditMasts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "creditMastSosok") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("신용 마스터 키워드 검색 요청 - keyword: {}, page: {}, size: {}", keyword, page, size);
        
        Page<CreditMastResponse> result = creditMastService.findByKeyword(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    /**
     * 복합 조건 검색
     */
    @PostMapping("/search/advanced")
    public ResponseEntity<Page<CreditMastResponse>> searchCreditMastsAdvanced(
            @RequestBody CreditMastSearchRequest request) {
        
        log.info("신용 마스터 복합 검색 요청 - request: {}", request);
        
        Page<CreditMastResponse> result = creditMastService.findBySearchConditions(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 소속별 신용 마스터 수 조회
     */
    @GetMapping("/count/sosok/{sosok}")
    public ResponseEntity<Long> countBySosok(@PathVariable Integer sosok) {
        log.info("소속별 신용 마스터 수 조회 요청 - sosok: {}", sosok);
        
        long count = creditMastService.countBySosok(sosok);
        return ResponseEntity.ok(count);
    }

    /**
     * 신용등급별 신용 마스터 수 조회
     */
    @GetMapping("/count/credit-rank/{creditRank}")
    public ResponseEntity<Long> countByCreditRank(@PathVariable String creditRank) {
        log.info("신용등급별 신용 마스터 수 조회 요청 - creditRank: {}", creditRank);
        
        long count = creditMastService.countByCreditRank(creditRank);
        return ResponseEntity.ok(count);
    }

    /**
     * 채권코드별 신용 마스터 수 조회
     */
    @GetMapping("/count/bond-code/{bondDcod}")
    public ResponseEntity<Long> countByBondDcod(@PathVariable String bondDcod) {
        log.info("채권코드별 신용 마스터 수 조회 요청 - bondDcod: {}", bondDcod);
        
        long count = creditMastService.countByBondDcod(bondDcod);
        return ResponseEntity.ok(count);
    }

    /**
     * 소속별 신용등급 통계
     */
    @GetMapping("/stats/credit-rank/sosok/{sosok}")
    public ResponseEntity<List<Object[]>> getCreditRankStatsBySosok(@PathVariable Integer sosok) {
        log.info("소속별 신용등급 통계 조회 요청 - sosok: {}", sosok);
        
        List<Object[]> stats = creditMastService.getCreditRankStatsBySosok(sosok);
        return ResponseEntity.ok(stats);
    }

    /**
     * 채권코드별 통계
     */
    @GetMapping("/stats/bond-code")
    public ResponseEntity<List<Object[]>> getBondDcodStats() {
        log.info("채권코드별 통계 조회 요청");
        
        List<Object[]> stats = creditMastService.getBondDcodStats();
        return ResponseEntity.ok(stats);
    }
} 