package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.CreditMast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditMastRepository extends JpaRepository<CreditMast, CreditMast.CreditMastId> {

    // 소속별 조회
    List<CreditMast> findByCreditMastSosok(Integer sosok);
    Page<CreditMast> findByCreditMastSosok(Integer sosok, Pageable pageable);

    // 거래처별 조회
    List<CreditMast> findByCreditMastCust(Integer cust);
    Page<CreditMast> findByCreditMastCust(Integer cust, Pageable pageable);

    // 소속+거래처 조회
    Optional<CreditMast> findByCreditMastSosokAndCreditMastCust(Integer sosok, Integer cust);

    // 신용등급별 조회
    List<CreditMast> findByCreditMastCreditRank(String creditRank);
    Page<CreditMast> findByCreditMastCreditRank(String creditRank, Pageable pageable);

    // 신용점수별 조회
    List<CreditMast> findByCreditMastCreditScore(String creditScore);
    Page<CreditMast> findByCreditMastCreditScore(String creditScore, Pageable pageable);

    // 채권코드별 조회
    List<CreditMast> findByCreditMastBondDcod(String bondDcod);
    Page<CreditMast> findByCreditMastBondDcod(String bondDcod, Pageable pageable);

    // 복합 조건 검색
    @Query("SELECT c FROM CreditMast c WHERE " +
           "(:sosok IS NULL OR c.creditMastSosok = :sosok) AND " +
           "(:cust IS NULL OR c.creditMastCust = :cust) AND " +
           "(:creditRank IS NULL OR c.creditMastCreditRank LIKE %:creditRank%) AND " +
           "(:creditScore IS NULL OR c.creditMastCreditScore LIKE %:creditScore%) AND " +
           "(:bondDcod IS NULL OR c.creditMastBondDcod = :bondDcod)")
    List<CreditMast> findBySearchConditions(@Param("sosok") Integer sosok,
                                           @Param("cust") Integer cust,
                                           @Param("creditRank") String creditRank,
                                           @Param("creditScore") String creditScore,
                                           @Param("bondDcod") String bondDcod);

    @Query("SELECT c FROM CreditMast c WHERE " +
           "(:sosok IS NULL OR c.creditMastSosok = :sosok) AND " +
           "(:cust IS NULL OR c.creditMastCust = :cust) AND " +
           "(:creditRank IS NULL OR c.creditMastCreditRank LIKE %:creditRank%) AND " +
           "(:creditScore IS NULL OR c.creditMastCreditScore LIKE %:creditScore%) AND " +
           "(:bondDcod IS NULL OR c.creditMastBondDcod = :bondDcod)")
    Page<CreditMast> findBySearchConditions(@Param("sosok") Integer sosok,
                                           @Param("cust") Integer cust,
                                           @Param("creditRank") String creditRank,
                                           @Param("creditScore") String creditScore,
                                           @Param("bondDcod") String bondDcod,
                                           Pageable pageable);

    // 키워드 통합 검색
    @Query("SELECT c FROM CreditMast c WHERE " +
           "c.creditMastCreditRank LIKE %:keyword% OR " +
           "c.creditMastCreditScore LIKE %:keyword% OR " +
           "c.creditMastBondDcod LIKE %:keyword%")
    List<CreditMast> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM CreditMast c WHERE " +
           "c.creditMastCreditRank LIKE %:keyword% OR " +
           "c.creditMastCreditScore LIKE %:keyword% OR " +
           "c.creditMastBondDcod LIKE %:keyword%")
    Page<CreditMast> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 전체 조회 (페이징)
    Page<CreditMast> findAll(Pageable pageable);

    // 통계 정보
    long countByCreditMastSosok(Integer sosok);
    long countByCreditMastCreditRank(String creditRank);
    long countByCreditMastBondDcod(String bondDcod);

    // 소속별 신용등급 통계
    @Query("SELECT c.creditMastCreditRank, COUNT(c) FROM CreditMast c WHERE c.creditMastSosok = :sosok GROUP BY c.creditMastCreditRank")
    List<Object[]> findCreditRankStatsBySosok(@Param("sosok") Integer sosok);

    // 채권코드별 통계
    @Query("SELECT c.creditMastBondDcod, COUNT(c) FROM CreditMast c GROUP BY c.creditMastBondDcod")
    List<Object[]> findBondDcodStats();
} 