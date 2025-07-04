package com.pipebank.ordersystem.domain.erp.service;

import com.pipebank.ordersystem.domain.erp.dto.CreditRiskResponse;
import com.pipebank.ordersystem.domain.erp.dto.CreditRiskSearchRequest;
import com.pipebank.ordersystem.domain.erp.entity.CreditRisk;
import com.pipebank.ordersystem.domain.erp.repository.CreditRiskRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CreditRiskService {

    private final CreditRiskRepository creditRiskRepository;
    private final SosokCodeRepository sosokCodeRepository;
    private final CustomerRepository customerRepository;
    private final CommonCode3Repository commonCode3Repository;

    // 전체 조회 (페이징)
    public Page<CreditRiskResponse> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    // ID로 조회
    public Optional<CreditRiskResponse> findById(Integer sosok, Integer cust, Integer seq) {
        CreditRisk.CreditRiskId id = new CreditRisk.CreditRiskId(sosok, cust, seq);
        return creditRiskRepository.findById(id)
                .map(this::convertToResponse);
    }

    // 소속별 조회
    public Page<CreditRiskResponse> findBySosok(Integer sosok, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByCreditRiskSosok(sosok, pageable)
                .map(this::convertToResponse);
    }

    // 거래처별 조회
    public Page<CreditRiskResponse> findByCust(Integer cust, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByCreditRiskCust(cust, pageable)
                .map(this::convertToResponse);
    }

    // 소속+거래처 조회
    public Page<CreditRiskResponse> findBySosokAndCust(Integer sosok, Integer cust, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByCreditRiskSosokAndCreditRiskCust(sosok, cust, pageable)
                .map(this::convertToResponse);
    }

    // 상태별 조회
    public Page<CreditRiskResponse> findByStau(String stau, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByCreditRiskStau(stau, pageable)
                .map(this::convertToResponse);
    }

    // 판매일자별 조회
    public Page<CreditRiskResponse> findBySaleDate(String saleDate, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByCreditRiskSaleDate(saleDate, pageable)
                .map(this::convertToResponse);
    }

    // 기간별 조회
    public Page<CreditRiskResponse> findByDateRange(String startDate, String endDate, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::convertToResponse);
    }

    // 신용한도 범위별 조회
    public Page<CreditRiskResponse> findByLimitRange(BigDecimal minLimit, BigDecimal maxLimit, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (maxLimit != null) {
            return creditRiskRepository.findByCreditRiskLimitLimitBetween(minLimit, maxLimit, pageable)
                    .map(this::convertToResponse);
        } else {
            return creditRiskRepository.findByCreditRiskLimitLimitGreaterThanEqual(minLimit, pageable)
                    .map(this::convertToResponse);
        }
    }

    // 미수채권 범위별 조회
    public Page<CreditRiskResponse> findByUnrecvBond(BigDecimal minBond, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByCreditRiskUnrecvBondGreaterThan(minBond, pageable)
                .map(this::convertToResponse);
    }

    // 키워드 검색
    public Page<CreditRiskResponse> findByKeyword(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return creditRiskRepository.findByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    // 복합 검색
    public Page<CreditRiskResponse> findBySearchConditions(CreditRiskSearchRequest request) {
        Sort sort = request.getSortDir().equalsIgnoreCase("desc") ? 
            Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        return creditRiskRepository.findBySearchConditions(
                request.getSosok(),
                request.getCust(),
                request.getStau(),
                request.getSaleDate(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMinLimit(),
                request.getMaxLimit(),
                pageable
        ).map(this::convertToResponse);
    }

    // 통계 정보
    public long countBySosok(Integer sosok) {
        return creditRiskRepository.countByCreditRiskSosok(sosok);
    }

    public long countByCust(Integer cust) {
        return creditRiskRepository.countByCreditRiskCust(cust);
    }

    public long countByStau(String stau) {
        return creditRiskRepository.countByCreditRiskStau(stau);
    }

    // 소속별 상태 통계
    public List<Object[]> getStatusStatsBySosok(Integer sosok) {
        return creditRiskRepository.findStatusStatsBySosok(sosok);
    }

    // 거래처별 상태 통계
    public List<Object[]> getStatusStatsByCust(Integer cust) {
        return creditRiskRepository.findStatusStatsByCust(cust);
    }

    // 전체 상태별 통계
    public List<Object[]> getStatusStats() {
        return creditRiskRepository.findStatusStats();
    }

    // 신용한도 통계
    public List<Object[]> getLimitStatsBySosok(Integer sosok) {
        return creditRiskRepository.findLimitStatsBySosok(sosok);
    }

    // 미수채권 통계
    public List<Object[]> getUnrecvBondStatsBySosok(Integer sosok) {
        return creditRiskRepository.findUnrecvBondStatsBySosok(sosok);
    }

    // 월별 통계
    public List<Object[]> getMonthlyStats(String startDate, String endDate) {
        return creditRiskRepository.findMonthlyStats(startDate, endDate);
    }

    // 엔티티를 DTO로 변환하면서 코드 표시명 매핑 및 계산된 필드 처리
    private CreditRiskResponse convertToResponse(CreditRisk creditRisk) {
        CreditRiskResponse.CreditRiskResponseBuilder builder = CreditRiskResponse.builder()
                .creditRiskSosok(creditRisk.getCreditRiskSosok())
                .creditRiskCust(creditRisk.getCreditRiskCust())
                .creditRiskSeq(creditRisk.getCreditRiskSeq())
                .creditRiskStau(creditRisk.getCreditRiskStau())
                .creditRiskSaleDate(creditRisk.getCreditRiskSaleDate())
                .creditRiskSdate(creditRisk.getCreditRiskSdate())
                .creditRiskEdate(creditRisk.getCreditRiskEdate())
                .creditRiskLimitLimit(creditRisk.getCreditRiskLimitLimit())
                .creditRiskLimitBond(creditRisk.getCreditRiskLimitBond())
                .creditRiskUnrecvBond(creditRisk.getCreditRiskUnrecvBond())
                .creditRiskUnrecvRecv(creditRisk.getCreditRiskUnrecvRecv())
                .creditRiskUnrecvBala(creditRisk.getCreditRiskUnrecvBala())
                .creditRiskFdate(creditRisk.getCreditRiskFdate())
                .creditRiskFuser(creditRisk.getCreditRiskFuser())
                .creditRiskLdate(creditRisk.getCreditRiskLdate())
                .creditRiskLuser(creditRisk.getCreditRiskLuser())
                .creditRiskKey(creditRisk.getCreditRiskKey())
                .displayName(creditRisk.getDisplayName());

        // 소속명 매핑
        if (creditRisk.getCreditRiskSosok() != null) {
            sosokCodeRepository.findById(creditRisk.getCreditRiskSosok())
                    .ifPresent(sosokCode -> builder.creditRiskSosokName(sosokCode.getSosokCodeName()));
        }

        // 거래처명 매핑
        if (creditRisk.getCreditRiskCust() != null) {
            customerRepository.findById(creditRisk.getCreditRiskCust())
                    .ifPresent(customer -> builder.creditRiskCustName(customer.getCustCodeName()));
        }

        // 상태코드 표시명 매핑
        if (creditRisk.getCreditRiskStau() != null) {
            commonCode3Repository.findByCommCod3Code(creditRisk.getCreditRiskStau())
                    .ifPresent(commonCode3 -> builder.creditRiskStauName(commonCode3.getCommCod3Hnam()));
        }

        // 계산된 필드들 처리
        BigDecimal totalUnrecv = creditRisk.getCreditRiskUnrecvBond()
                .add(creditRisk.getCreditRiskUnrecvRecv())
                .add(creditRisk.getCreditRiskUnrecvBala());
        builder.totalUnrecv(totalUnrecv);

        BigDecimal availableLimit = creditRisk.getCreditRiskLimitLimit()
                .subtract(creditRisk.getCreditRiskUnrecvBond());
        builder.availableLimit(availableLimit);

        // 위험도 레벨 계산
        String riskLevel = calculateRiskLevel(creditRisk);
        builder.riskLevel(riskLevel);

        return builder.build();
    }

    // 위험도 레벨 계산 로직
    private String calculateRiskLevel(CreditRisk creditRisk) {
        BigDecimal limitLimit = creditRisk.getCreditRiskLimitLimit();
        BigDecimal unrecvBond = creditRisk.getCreditRiskUnrecvBond();
        
        if (limitLimit.compareTo(BigDecimal.ZERO) == 0) {
            return "위험";
        }
        
        BigDecimal ratio = unrecvBond.divide(limitLimit, 2, BigDecimal.ROUND_HALF_UP);
        
        if (ratio.compareTo(new BigDecimal("0.8")) >= 0) {
            return "위험";
        } else if (ratio.compareTo(new BigDecimal("0.5")) >= 0) {
            return "주의";
        } else {
            return "안전";
        }
    }
} 