package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.InsaMast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사원 정보 Repository
 */
@Repository
public interface InsaMastRepository extends JpaRepository<InsaMast, Integer> {

    /**
     * 모든 사원 조회 (사번 순 정렬)
     */
    @Query("SELECT i FROM InsaMast i ORDER BY i.insaMastSano")
    List<InsaMast> findAllOrderBySano();

    /**
     * 사원명으로 검색
     */
    @Query("SELECT i FROM InsaMast i WHERE i.insaMastKnam LIKE %:name% ORDER BY i.insaMastSano")
    List<InsaMast> findByNameContainingOrderBySano(@Param("name") String name);

    /**
     * 특정 사번 존재 여부 확인
     */
    boolean existsByInsaMastSano(Integer sano);
} 