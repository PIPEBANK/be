package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.CreditRisk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRiskRepository extends JpaRepository<CreditRisk, CreditRisk.CreditRiskId> {

    // 소속별 조회
    List<CreditRisk> findByCreditRiskSosok(Integer sosok);
    Page<CreditRisk> findByCreditRiskSosok(Integer sosok, Pageable pageable);

    // 거래처별 조회
    List<CreditRisk> findByCreditRiskCust(Integer cust);
    Page<CreditRisk> findByCreditRiskCust(Integer cust, Pageable pageable);

    // 소속+거래처 조회
    List<CreditRisk> findByCreditRiskSosokAndCreditRiskCust(Integer sosok, Integer cust);
    Page<CreditRisk> findByCreditRiskSosokAndCreditRiskCust(Integer sosok, Integer cust, Pageable pageable);

    // 소속+거래처+순번 조회
    Optional<CreditRisk> findByCreditRiskSosokAndCreditRiskCustAndCreditRiskSeq(Integer sosok, Integer cust, Integer seq);

    // 상태별 조회
    List<CreditRisk> findByCreditRiskStau(String stau);
    Page<CreditRisk> findByCreditRiskStau(String stau, Pageable pageable);

    // 판매일자별 조회
    List<CreditRisk> findByCreditRiskSaleDate(String saleDate);
    Page<CreditRisk> findByCreditRiskSaleDate(String saleDate, Pageable pageable);

    // 기간별 조회
    @Query("SELECT c FROM CreditRisk c WHERE c.creditRiskSdate >= :startDate AND c.creditRiskEdate <= :endDate")
    List<CreditRisk> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT c FROM CreditRisk c WHERE c.creditRiskSdate >= :startDate AND c.creditRiskEdate <= :endDate")
    Page<CreditRisk> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // 신용한도 범위별 조회
    List<CreditRisk> findByCreditRiskLimitLimitGreaterThanEqual(BigDecimal minLimit);
    Page<CreditRisk> findByCreditRiskLimitLimitGreaterThanEqual(BigDecimal minLimit, Pageable pageable);

    List<CreditRisk> findByCreditRiskLimitLimitBetween(BigDecimal minLimit, BigDecimal maxLimit);
    Page<CreditRisk> findByCreditRiskLimitLimitBetween(BigDecimal minLimit, BigDecimal maxLimit, Pageable pageable);

    // 미수채권 범위별 조회
    List<CreditRisk> findByCreditRiskUnrecvBondGreaterThan(BigDecimal minBond);
    Page<CreditRisk> findByCreditRiskUnrecvBondGreaterThan(BigDecimal minBond, Pageable pageable);

    // 복합 조건 검색
    @Query("SELECT c FROM CreditRisk c WHERE " +
           "(:sosok IS NULL OR c.creditRiskSosok = :sosok) AND " +
           "(:cust IS NULL OR c.creditRiskCust = :cust) AND " +
           "(:stau IS NULL OR c.creditRiskStau = :stau) AND " +
           "(:saleDate IS NULL OR c.creditRiskSaleDate = :saleDate) AND " +
           "(:startDate IS NULL OR c.creditRiskSdate >= :startDate) AND " +
           "(:endDate IS NULL OR c.creditRiskEdate <= :endDate) AND " +
           "(:minLimit IS NULL OR c.creditRiskLimitLimit >= :minLimit) AND " +
           "(:maxLimit IS NULL OR c.creditRiskLimitLimit <= :maxLimit)")
    List<CreditRisk> findBySearchConditions(@Param("sosok") Integer sosok,
                                           @Param("cust") Integer cust,
                                           @Param("stau") String stau,
                                           @Param("saleDate") String saleDate,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate,
                                           @Param("minLimit") BigDecimal minLimit,
                                           @Param("maxLimit") BigDecimal maxLimit);

    @Query("SELECT c FROM CreditRisk c WHERE " +
           "(:sosok IS NULL OR c.creditRiskSosok = :sosok) AND " +
           "(:cust IS NULL OR c.creditRiskCust = :cust) AND " +
           "(:stau IS NULL OR c.creditRiskStau = :stau) AND " +
           "(:saleDate IS NULL OR c.creditRiskSaleDate = :saleDate) AND " +
           "(:startDate IS NULL OR c.creditRiskSdate >= :startDate) AND " +
           "(:endDate IS NULL OR c.creditRiskEdate <= :endDate) AND " +
           "(:minLimit IS NULL OR c.creditRiskLimitLimit >= :minLimit) AND " +
           "(:maxLimit IS NULL OR c.creditRiskLimitLimit <= :maxLimit)")
    Page<CreditRisk> findBySearchConditions(@Param("sosok") Integer sosok,
                                           @Param("cust") Integer cust,
                                           @Param("stau") String stau,
                                           @Param("saleDate") String saleDate,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate,
                                           @Param("minLimit") BigDecimal minLimit,
                                           @Param("maxLimit") BigDecimal maxLimit,
                                           Pageable pageable);

    // 키워드 통합 검색
    @Query("SELECT c FROM CreditRisk c WHERE " +
           "c.creditRiskStau LIKE %:keyword% OR " +
           "c.creditRiskSaleDate LIKE %:keyword% OR " +
           "c.creditRiskSdate LIKE %:keyword% OR " +
           "c.creditRiskEdate LIKE %:keyword%")
    List<CreditRisk> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM CreditRisk c WHERE " +
           "c.creditRiskStau LIKE %:keyword% OR " +
           "c.creditRiskSaleDate LIKE %:keyword% OR " +
           "c.creditRiskSdate LIKE %:keyword% OR " +
           "c.creditRiskEdate LIKE %:keyword%")
    Page<CreditRisk> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 전체 조회 (페이징)
    Page<CreditRisk> findAll(Pageable pageable);

    // 통계 정보
    long countByCreditRiskSosok(Integer sosok);
    long countByCreditRiskCust(Integer cust);
    long countByCreditRiskStau(String stau);

    // 소속별 상태 통계
    @Query("SELECT c.creditRiskStau, COUNT(c) FROM CreditRisk c WHERE c.creditRiskSosok = :sosok GROUP BY c.creditRiskStau")
    List<Object[]> findStatusStatsBySosok(@Param("sosok") Integer sosok);

    // 거래처별 상태 통계
    @Query("SELECT c.creditRiskStau, COUNT(c) FROM CreditRisk c WHERE c.creditRiskCust = :cust GROUP BY c.creditRiskStau")
    List<Object[]> findStatusStatsByCust(@Param("cust") Integer cust);

    // 전체 상태별 통계
    @Query("SELECT c.creditRiskStau, COUNT(c) FROM CreditRisk c GROUP BY c.creditRiskStau")
    List<Object[]> findStatusStats();

    // 신용한도 통계
    @Query("SELECT SUM(c.creditRiskLimitLimit), AVG(c.creditRiskLimitLimit), MAX(c.creditRiskLimitLimit), MIN(c.creditRiskLimitLimit) FROM CreditRisk c WHERE c.creditRiskSosok = :sosok")
    List<Object[]> findLimitStatsBySosok(@Param("sosok") Integer sosok);

    // 미수채권 통계
    @Query("SELECT SUM(c.creditRiskUnrecvBond), AVG(c.creditRiskUnrecvBond), MAX(c.creditRiskUnrecvBond), MIN(c.creditRiskUnrecvBond) FROM CreditRisk c WHERE c.creditRiskSosok = :sosok")
    List<Object[]> findUnrecvBondStatsBySosok(@Param("sosok") Integer sosok);

    // 월별 통계
    @Query("SELECT SUBSTRING(c.creditRiskSdate, 1, 6), COUNT(c) FROM CreditRisk c WHERE c.creditRiskSdate >= :startDate AND c.creditRiskSdate <= :endDate GROUP BY SUBSTRING(c.creditRiskSdate, 1, 6)")
    List<Object[]> findMonthlyStats(@Param("startDate") String startDate, @Param("endDate") String endDate);
} 