package com.pipebank.ordersystem.domain.erp.service;

import com.pipebank.ordersystem.domain.erp.dto.CreditMastResponse;
import com.pipebank.ordersystem.domain.erp.dto.CreditMastSearchRequest;
import com.pipebank.ordersystem.domain.erp.entity.CreditMast;
import com.pipebank.ordersystem.domain.erp.repository.CreditMastRepository;
import com.pipebank.ordersystem.domain.erp.repository.SosokCodeRepository;
import com.pipebank.ordersystem.domain.erp.repository.CustomerRepository;
import com.pipebank.ordersystem.domain.erp.repository.CommonCode3Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CreditMastService {

    private final CreditMastRepository creditMastRepository;
    private final SosokCodeRepository sosokCodeRepository;
    private final CustomerRepository customerRepository;
    private final CommonCode3Repository commonCode3Repository;

    // 전체 조회 (페이징)
    public Page<CreditMastResponse> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    // ID로 조회
    public Optional<CreditMastResponse> findById(Integer sosok, Integer cust) {
        CreditMast.CreditMastId id = new CreditMast.CreditMastId(sosok, cust);
        return creditMastRepository.findById(id)
                .map(this::convertToResponse);
    }

    // 소속별 조회
    public List<CreditMastResponse> findBySosok(Integer sosok) {
        return creditMastRepository.findByCreditMastSosok(sosok)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<CreditMastResponse> findBySosok(Integer sosok, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findByCreditMastSosok(sosok, pageable)
                .map(this::convertToResponse);
    }

    // 거래처별 조회
    public List<CreditMastResponse> findByCust(Integer cust) {
        return creditMastRepository.findByCreditMastCust(cust)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<CreditMastResponse> findByCust(Integer cust, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findByCreditMastCust(cust, pageable)
                .map(this::convertToResponse);
    }

    // 신용등급별 조회
    public Page<CreditMastResponse> findByCreditRank(String creditRank, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findByCreditMastCreditRank(creditRank, pageable)
                .map(this::convertToResponse);
    }

    // 신용점수별 조회
    public Page<CreditMastResponse> findByCreditScore(String creditScore, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findByCreditMastCreditScore(creditScore, pageable)
                .map(this::convertToResponse);
    }

    // 채권코드별 조회
    public Page<CreditMastResponse> findByBondDcod(String bondDcod, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findByCreditMastBondDcod(bondDcod, pageable)
                .map(this::convertToResponse);
    }

    // 키워드 검색
    public Page<CreditMastResponse> findByKeyword(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditMastRepository.findByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    // 복합 검색
    public Page<CreditMastResponse> findBySearchConditions(CreditMastSearchRequest request) {
        Sort sort = request.getSortDir().equalsIgnoreCase("desc") ? 
            Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        return creditMastRepository.findBySearchConditions(
                request.getSosok(),
                request.getCust(),
                request.getCreditRank(),
                request.getCreditScore(),
                request.getBondDcod(),
                pageable
        ).map(this::convertToResponse);
    }

    // 통계 정보
    public long countBySosok(Integer sosok) {
        return creditMastRepository.countByCreditMastSosok(sosok);
    }

    public long countByCreditRank(String creditRank) {
        return creditMastRepository.countByCreditMastCreditRank(creditRank);
    }

    public long countByBondDcod(String bondDcod) {
        return creditMastRepository.countByCreditMastBondDcod(bondDcod);
    }

    // 소속별 신용등급 통계
    public List<Object[]> getCreditRankStatsBySosok(Integer sosok) {
        return creditMastRepository.findCreditRankStatsBySosok(sosok);
    }

    // 채권코드별 통계
    public List<Object[]> getBondDcodStats() {
        return creditMastRepository.findBondDcodStats();
    }

    // 엔티티를 DTO로 변환하면서 코드 표시명 매핑
    private CreditMastResponse convertToResponse(CreditMast creditMast) {
        CreditMastResponse.CreditMastResponseBuilder builder = CreditMastResponse.builder()
                .creditMastSosok(creditMast.getCreditMastSosok())
                .creditMastCust(creditMast.getCreditMastCust())
                .creditMastCreditRank(creditMast.getCreditMastCreditRank())
                .creditMastCreditScore(creditMast.getCreditMastCreditScore())
                .creditMastBondDcod(creditMast.getCreditMastBondDcod())
                .creditMastSdate(creditMast.getCreditMastSdate())
                .creditMastFdate(creditMast.getCreditMastFdate())
                .creditMastFuser(creditMast.getCreditMastFuser())
                .creditMastLdate(creditMast.getCreditMastLdate())
                .creditMastLuser(creditMast.getCreditMastLuser())
                .creditKey(creditMast.getCreditKey())
                .displayName(creditMast.getDisplayName());

        // 소속명 매핑
        if (creditMast.getCreditMastSosok() != null) {
            sosokCodeRepository.findById(creditMast.getCreditMastSosok())
                    .ifPresent(sosokCode -> builder.creditMastSosokName(sosokCode.getSosokCodeName()));
        }

        // 거래처명 매핑
        if (creditMast.getCreditMastCust() != null) {
            customerRepository.findById(creditMast.getCreditMastCust())
                    .ifPresent(customer -> builder.creditMastCustName(customer.getCustCodeName()));
        }

        // 채권코드 표시명 매핑
        if (creditMast.getCreditMastBondDcod() != null) {
            commonCode3Repository.findByCommCod3Code(creditMast.getCreditMastBondDcod())
                    .ifPresent(commonCode3 -> builder.creditMastBondDcodName(commonCode3.getCommCod3Hnam()));
        }

        return builder.build();
    }
} 