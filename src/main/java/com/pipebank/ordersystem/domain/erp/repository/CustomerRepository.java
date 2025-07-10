package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // 거래처 번호로 조회
    Optional<Customer> findByCustCodeNum(String custCodeNum);
    
    // 사업자번호로 조회
    Optional<Customer> findByCustCodeSano(String custCodeSano);
    
    // 거래처 이름으로 조회
    List<Customer> findByCustCodeNameContaining(String custCodeName);
    
    // 활성 거래처만 조회
    @Query("SELECT c FROM Customer c WHERE c.custCodeUseAcc = 1")
    List<Customer> findActiveCustomers();
    
    @Query("SELECT c FROM Customer c WHERE c.custCodeUseAcc = 1")
    Page<Customer> findActiveCustomers(Pageable pageable);
    
    // 활성 거래처 통합검색 (거래처명 또는 사업자등록번호)
    @Query("SELECT c FROM Customer c WHERE c.custCodeUseAcc = 1 AND " +
           "(c.custCodeName LIKE %:search% OR c.custCodeSano LIKE %:search%)")
    Page<Customer> findActiveCustomersWithSearch(@Param("search") String search, Pageable pageable);
    
    // 구매 가능한 거래처 조회
    @Query("SELECT c FROM Customer c WHERE c.custCodeUsePur = 1")
    List<Customer> findPurchaseableCustomers();
    
    // POS 사용 가능한 거래처 조회
    @Query("SELECT c FROM Customer c WHERE c.custCodeUsePos = 1")
    List<Customer> findPosAvailableCustomers();
    
    // 복합 조건 검색
    @Query("SELECT c FROM Customer c WHERE " +
           "(:custCodeNum IS NULL OR c.custCodeNum LIKE %:custCodeNum%) AND " +
           "(:custCodeName IS NULL OR c.custCodeName LIKE %:custCodeName%) AND " +
           "(:custCodeDcod IS NULL OR c.custCodeDcod = :custCodeDcod) AND " +
           "(:useAcc IS NULL OR c.custCodeUseAcc = :useAcc) AND " +
           "(:usePur IS NULL OR c.custCodeUsePur = :usePur) AND " +
           "(:usePos IS NULL OR c.custCodeUsePos = :usePos)")
    Page<Customer> findCustomersWithConditions(
            @Param("custCodeNum") String custCodeNum,
            @Param("custCodeName") String custCodeName,
            @Param("custCodeDcod") String custCodeDcod,
            @Param("useAcc") Integer useAcc,
            @Param("usePur") Integer usePur,
            @Param("usePos") Integer usePos,
            Pageable pageable);
    
    // 담당자별 거래처 조회
    List<Customer> findByCustCodeSawon(Integer custCodeSawon);
    
    // 사업부별 거래처 조회
    List<Customer> findByCustCodeBuse(Integer custCodeBuse);
    
    // 지역별 거래처 조회
    List<Customer> findByCustCodeLocal(String custCodeLocal);
    
    // 통계 쿼리
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.custCodeUseAcc = 1")
    long countActiveCustomers();
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.custCodeUsePur = 1")
    long countPurchaseableCustomers();
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.custCodeUsePos = 1")
    long countPosAvailableCustomers();
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.custCodeSawon = :sawonId")
    long countCustomersBySawon(@Param("sawonId") Integer sawonId);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.custCodeBuse = :buseId")
    long countCustomersByBuse(@Param("buseId") Integer buseId);
} 