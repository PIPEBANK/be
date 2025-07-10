package com.pipebank.ordersystem.domain.erp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.erp.dto.CustomerResponse;
import com.pipebank.ordersystem.domain.erp.entity.Customer;
import com.pipebank.ordersystem.domain.erp.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, transactionManager = "erpTransactionManager")
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CommonCodeService commonCodeService;

    /**
     * 거래처 조회 (ID)
     */
    public CustomerResponse getCustomer(Integer custCodeCode) {
        log.info("거래처 조회 요청 - ID: {}", custCodeCode);
        
        Customer customer = customerRepository.findById(custCodeCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처입니다: " + custCodeCode));
        
        // DCOD 코드를 실제 표시명으로 변환
        String dcodDisplayName = commonCodeService.getDisplayNameByCode(customer.getCustCodeDcod());
        
        log.info("거래처 조회 완료 - ID: {}, 이름: {}, DCOD: {} ({})", 
                customer.getCustCodeCode(), customer.getDisplayName(), 
                customer.getCustCodeDcod(), dcodDisplayName);
        
        return CustomerResponse.from(customer, dcodDisplayName);
    }

    /**
     * 거래처 조회 (거래처 번호)
     */
    public CustomerResponse getCustomerByNum(String custCodeNum) {
        log.info("거래처 조회 요청 - 거래처번호: {}", custCodeNum);
        
        Customer customer = customerRepository.findByCustCodeNum(custCodeNum)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처 번호입니다: " + custCodeNum));
        
        // DCOD 코드를 실제 표시명으로 변환
        String dcodDisplayName = commonCodeService.getDisplayNameByCode(customer.getCustCodeDcod());
        
        log.info("거래처 조회 완료 - 번호: {}, 이름: {}, DCOD: {} ({})", 
                customer.getCustCodeNum(), customer.getDisplayName(), 
                customer.getCustCodeDcod(), dcodDisplayName);
        
        return CustomerResponse.from(customer, dcodDisplayName);
    }

    /**
     * 거래처 조회 (사업자번호)
     */
    public String getCustCodeBySano(String custCodeSano) {
        log.info("거래처 조회 요청 - 사업자번호: {}", custCodeSano);
        
        Customer customer = customerRepository.findByCustCodeSano(custCodeSano)
                .orElse(null);
        
        if (customer == null) {
            log.warn("존재하지 않는 사업자번호입니다: {}", custCodeSano);
            return null;
        }
        
        log.info("거래처 조회 완료 - 사업자번호: {}, 거래처코드: {}, 이름: {}", 
                custCodeSano, customer.getCustCodeCode(), customer.getDisplayName());
        
        return customer.getCustCodeCode().toString();
    }

    /**
     * Customer Entity를 CustomerResponse로 변환 (DCOD 표시명 포함)
     */
    private CustomerResponse convertToResponse(Customer customer) {
        String dcodDisplayName = commonCodeService.getDisplayNameByCode(customer.getCustCodeDcod());
        return CustomerResponse.from(customer, dcodDisplayName);
    }

    /**
     * 모든 거래처 목록 조회 (페이징)
     */
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        log.info("전체 거래처 목록 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Customer> customers = customerRepository.findAll(pageable);
        
        log.info("전체 거래처 목록 조회 완료 - 총 {}건", customers.getTotalElements());
        return customers.map(this::convertToResponse);
    }

    /**
     * 활성 거래처 목록 조회 (페이징)
     */
    public Page<CustomerResponse> getActiveCustomers(Pageable pageable) {
        log.info("활성 거래처 목록 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Customer> customers = customerRepository.findActiveCustomers(pageable);
        
        log.info("활성 거래처 목록 조회 완료 - 총 {}건", customers.getTotalElements());
        return customers.map(this::convertToResponse);
    }

    /**
     * 활성 거래처 목록 조회 (페이징 + 통합검색)
     * 검색어로 거래처명 또는 사업자등록번호 검색
     */
    public Page<CustomerResponse> getActiveCustomersWithSearch(String search, Pageable pageable) {
        log.info("활성 거래처 통합검색 요청 - 검색어: {}, 페이지: {}, 크기: {}", 
                search, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Customer> customers;
        
        if (search == null || search.trim().isEmpty()) {
            // 검색어가 없으면 전체 활성 거래처 조회
            customers = customerRepository.findActiveCustomers(pageable);
        } else {
            // 검색어가 있으면 거래처명 또는 사업자등록번호로 검색
            customers = customerRepository.findActiveCustomersWithSearch(search.trim(), pageable);
        }
        
        log.info("활성 거래처 통합검색 완료 - 검색어: {}, 총 {}건", search, customers.getTotalElements());
        return customers.map(this::convertToResponse);
    }

    /**
     * 구매 가능한 거래처 목록 조회
     */
    public List<CustomerResponse> getPurchaseableCustomers() {
        log.info("구매 가능한 거래처 목록 조회 요청");
        
        List<Customer> customers = customerRepository.findPurchaseableCustomers();
        
        log.info("구매 가능한 거래처 목록 조회 완료 - 총 {}건", customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * POS 사용 가능한 거래처 목록 조회
     */
    public List<CustomerResponse> getPosAvailableCustomers() {
        log.info("POS 사용 가능한 거래처 목록 조회 요청");
        
        List<Customer> customers = customerRepository.findPosAvailableCustomers();
        
        log.info("POS 사용 가능한 거래처 목록 조회 완료 - 총 {}건", customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * 거래처 이름으로 검색
     */
    public List<CustomerResponse> searchCustomersByName(String custCodeName) {
        log.info("거래처 이름 검색 요청 - 검색어: {}", custCodeName);
        
        List<Customer> customers = customerRepository.findByCustCodeNameContaining(custCodeName);
        
        log.info("거래처 이름 검색 완료 - 검색어: {}, 결과: {}건", custCodeName, customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * 조건별 거래처 검색
     */
    public Page<CustomerResponse> searchCustomers(String custCodeNum, String custCodeName, String custCodeDcod,
                                                 Integer useAcc, Integer usePur, Integer usePos, Pageable pageable) {
        log.info("조건별 거래처 검색 요청 - 번호: {}, 이름: {}, 구분: {}, 회계: {}, 구매: {}, POS: {}",
                custCodeNum, custCodeName, custCodeDcod, useAcc, usePur, usePos);
        
        Page<Customer> customers = customerRepository.findCustomersWithConditions(
                custCodeNum, custCodeName, custCodeDcod, useAcc, usePur, usePos, pageable);
        
        log.info("조건별 거래처 검색 완료 - 총 {}건", customers.getTotalElements());
        return customers.map(this::convertToResponse);
    }

    /**
     * 담당자별 거래처 조회
     */
    public List<CustomerResponse> getCustomersBySawon(Integer custCodeSawon) {
        log.info("담당자별 거래처 조회 요청 - 담당자 ID: {}", custCodeSawon);
        
        List<Customer> customers = customerRepository.findByCustCodeSawon(custCodeSawon);
        
        log.info("담당자별 거래처 조회 완료 - 담당자 ID: {}, 결과: {}건", custCodeSawon, customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * 사업부별 거래처 조회
     */
    public List<CustomerResponse> getCustomersByBuse(Integer custCodeBuse) {
        log.info("사업부별 거래처 조회 요청 - 사업부 ID: {}", custCodeBuse);
        
        List<Customer> customers = customerRepository.findByCustCodeBuse(custCodeBuse);
        
        log.info("사업부별 거래처 조회 완료 - 사업부 ID: {}, 결과: {}건", custCodeBuse, customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * 지역별 거래처 조회
     */
    public List<CustomerResponse> getCustomersByLocal(String custCodeLocal) {
        log.info("지역별 거래처 조회 요청 - 지역: {}", custCodeLocal);
        
        List<Customer> customers = customerRepository.findByCustCodeLocal(custCodeLocal);
        
        log.info("지역별 거래처 조회 완료 - 지역: {}, 결과: {}건", custCodeLocal, customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * 거래처 통계 정보
     */
    public CustomerStatistics getCustomerStatistics() {
        log.info("거래처 통계 정보 조회 요청");
        
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countActiveCustomers();
        long purchaseableCustomers = customerRepository.countPurchaseableCustomers();
        long posAvailableCustomers = customerRepository.countPosAvailableCustomers();
        
        CustomerStatistics statistics = CustomerStatistics.builder()
                .totalCustomers(totalCustomers)
                .activeCustomers(activeCustomers)
                .purchaseableCustomers(purchaseableCustomers)
                .posAvailableCustomers(posAvailableCustomers)
                .build();
        
        log.info("거래처 통계 정보 조회 완료 - 전체: {}, 활성: {}, 구매가능: {}, POS가능: {}",
                totalCustomers, activeCustomers, purchaseableCustomers, posAvailableCustomers);
        
        return statistics;
    }

    /**
     * 담당자별 거래처 수 조회
     */
    public long getCustomerCountBySawon(Integer sawonId) {
        return customerRepository.countCustomersBySawon(sawonId);
    }

    /**
     * 사업부별 거래처 수 조회
     */
    public long getCustomerCountByBuse(Integer buseId) {
        return customerRepository.countCustomersByBuse(buseId);
    }

    /**
     * 거래처 통계 내부 클래스
     */
    @lombok.Builder
    @lombok.Getter
    public static class CustomerStatistics {
        private final long totalCustomers;
        private final long activeCustomers;
        private final long purchaseableCustomers;
        private final long posAvailableCustomers;
    }
} 