package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.CommonCode2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonCode2Repository extends JpaRepository<CommonCode2, CommonCode2.CommonCode2Id> {

    // 전체 목록 (정렬)
    List<CommonCode2> findAllByOrderByCommCod2Cod1AscCommCod2SortAsc();

    // 활성화된 것만
    @Query("SELECT c FROM CommonCode2 c WHERE c.commCod2Use = 1 ORDER BY c.commCod2Cod1 ASC, c.commCod2Sort ASC")
    List<CommonCode2> findActiveCommonCodes();

    // 보기 가능한 것만
    @Query("SELECT c FROM CommonCode2 c WHERE c.commCod2View = 1 ORDER BY c.commCod2Cod1 ASC, c.commCod2Sort ASC")
    List<CommonCode2> findVisibleCommonCodes();

    // 특정 대분류의 중분류들
    List<CommonCode2> findByCommCod2Cod1OrderByCommCod2SortAsc(String commCod2Cod1);

    // 특정 대분류의 활성화된 중분류들
    @Query("SELECT c FROM CommonCode2 c WHERE c.commCod2Cod1 = :cod1 AND c.commCod2Use = 1 ORDER BY c.commCod2Sort ASC")
    List<CommonCode2> findActiveByCommCod2Cod1(@Param("cod1") String commCod2Cod1);

    // CODE로 조회
    Optional<CommonCode2> findByCommCod2Code(String commCod2Code);

    // 복합키로 조회
    Optional<CommonCode2> findByCommCod2Cod1AndCommCod2Cod2(String commCod2Cod1, String commCod2Cod2);

    // 이름으로 검색
    @Query("SELECT c FROM CommonCode2 c WHERE c.commCod2Name LIKE %:name% OR c.commCod2Word LIKE %:name% ORDER BY c.commCod2Cod1 ASC, c.commCod2Sort ASC")
    List<CommonCode2> findByNameContaining(@Param("name") String name);

    // 특정 대분류 내에서 이름으로 검색
    @Query("SELECT c FROM CommonCode2 c WHERE c.commCod2Cod1 = :cod1 AND (c.commCod2Name LIKE %:name% OR c.commCod2Word LIKE %:name%) ORDER BY c.commCod2Sort ASC")
    List<CommonCode2> findByCommCod2Cod1AndNameContaining(@Param("cod1") String commCod2Cod1, @Param("name") String name);
} 