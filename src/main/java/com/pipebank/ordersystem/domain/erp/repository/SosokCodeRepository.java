package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.SosokCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 소속 코드 Repository
 */
@Repository
public interface SosokCodeRepository extends JpaRepository<SosokCode, Integer> {

    /**
     * 모든 소속 코드 조회 (코드 순 정렬)
     */
    @Query("SELECT s FROM SosokCode s ORDER BY s.sosokCodeCode")
    List<SosokCode> findAllOrderByCode();

    /**
     * 코드명으로 검색
     */
    @Query("SELECT s FROM SosokCode s WHERE s.sosokCodeName LIKE %:name% ORDER BY s.sosokCodeCode")
    List<SosokCode> findByNameContainingOrderByCode(@Param("name") String name);

    /**
     * 특정 코드 존재 여부 확인
     */
    boolean existsBySosokCodeCode(Integer code);
} 