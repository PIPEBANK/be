package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.CommonCode3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonCode3Repository extends JpaRepository<CommonCode3, CommonCode3.CommonCode3Id> {

    // 전체 목록 (정렬)
    List<CommonCode3> findAllByOrderByCommCod3Cod1AscCommCod3Cod2AscCommCod3Cod3Asc();

    // 활성화된 것만
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Use = 1 ORDER BY c.commCod3Cod1 ASC, c.commCod3Cod2 ASC, c.commCod3Cod3 ASC")
    List<CommonCode3> findActiveCommonCodes();

    // 특정 대분류의 소분류들
    List<CommonCode3> findByCommCod3Cod1OrderByCommCod3Cod2AscCommCod3Cod3Asc(String commCod3Cod1);

    // 특정 중분류의 소분류들
    List<CommonCode3> findByCommCod3Cod1AndCommCod3Cod2OrderByCommCod3Cod3Asc(String commCod3Cod1, String commCod3Cod2);

    // 특정 대분류의 활성화된 소분류들
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Cod1 = :cod1 AND c.commCod3Use = 1 ORDER BY c.commCod3Cod2 ASC, c.commCod3Cod3 ASC")
    List<CommonCode3> findActiveByCommCod3Cod1(@Param("cod1") String commCod3Cod1);

    // 특정 중분류의 활성화된 소분류들
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Cod1 = :cod1 AND c.commCod3Cod2 = :cod2 AND c.commCod3Use = 1 ORDER BY c.commCod3Cod3 ASC")
    List<CommonCode3> findActiveByCommCod3Cod1AndCommCod3Cod2(@Param("cod1") String commCod3Cod1, @Param("cod2") String commCod3Cod2);

    // CODE로 조회
    Optional<CommonCode3> findByCommCod3Code(String commCod3Code);

    // 복합키로 조회
    Optional<CommonCode3> findByCommCod3Cod1AndCommCod3Cod2AndCommCod3Cod3(String commCod3Cod1, String commCod3Cod2, String commCod3Cod3);

    // 이름으로 검색
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Word LIKE %:name% ORDER BY c.commCod3Cod1 ASC, c.commCod3Cod2 ASC, c.commCod3Cod3 ASC")
    List<CommonCode3> findByNameContaining(@Param("name") String name);

    // 특정 대분류 내에서 이름으로 검색
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Cod1 = :cod1 AND c.commCod3Word LIKE %:name% ORDER BY c.commCod3Cod2 ASC, c.commCod3Cod3 ASC")
    List<CommonCode3> findByCommCod3Cod1AndNameContaining(@Param("cod1") String commCod3Cod1, @Param("name") String name);

    // 특정 중분류 내에서 이름으로 검색
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Cod1 = :cod1 AND c.commCod3Cod2 = :cod2 AND c.commCod3Word LIKE %:name% ORDER BY c.commCod3Cod3 ASC")
    List<CommonCode3> findByCommCod3Cod1AndCommCod3Cod2AndNameContaining(@Param("cod1") String commCod3Cod1, @Param("cod2") String commCod3Cod2, @Param("name") String name);

    // 보기 가능한 것만
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3View = 1 ORDER BY c.commCod3Cod1 ASC, c.commCod3Cod2 ASC, c.commCod3Sort ASC")
    List<CommonCode3> findVisibleCommonCodes();

    // 한글명으로 검색
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Hnam LIKE %:name% ORDER BY c.commCod3Cod1 ASC, c.commCod3Cod2 ASC, c.commCod3Sort ASC")
    List<CommonCode3> findByHangulNameContaining(@Param("name") String name);

    // 영어명으로 검색
    @Query("SELECT c FROM CommonCode3 c WHERE c.commCod3Enam LIKE %:name% ORDER BY c.commCod3Cod1 ASC, c.commCod3Cod2 ASC, c.commCod3Sort ASC")
    List<CommonCode3> findByEnglishNameContaining(@Param("name") String name);

    // 정렬 순서로 조회 (특정 중분류)
    List<CommonCode3> findByCommCod3Cod1AndCommCod3Cod2OrderByCommCod3SortAsc(String commCod3Cod1, String commCod3Cod2);
} 