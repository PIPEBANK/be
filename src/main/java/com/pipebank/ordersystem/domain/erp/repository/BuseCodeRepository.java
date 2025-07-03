package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.BuseCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 부서 코드 Repository
 */
@Repository
public interface BuseCodeRepository extends JpaRepository<BuseCode, Integer> {

    /**
     * 모든 부서 코드 조회 (코드 순 정렬)
     */
    @Query("SELECT b FROM BuseCode b ORDER BY b.buseCodeCode")
    List<BuseCode> findAllOrderByCode();

    /**
     * 부서명으로 검색
     */
    @Query("SELECT b FROM BuseCode b WHERE b.buseCodeName LIKE %:name% ORDER BY b.buseCodeCode")
    List<BuseCode> findByNameContainingOrderByCode(@Param("name") String name);

    /**
     * 특정 코드 존재 여부 확인
     */
    boolean existsByBuseCodeCode(Integer code);
} 