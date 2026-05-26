package com.weborder.ordersystem.domain.erp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weborder.ordersystem.domain.erp.dto.CustomerCreateRequest;
import com.weborder.ordersystem.domain.erp.dto.CustomerResponse;
import com.weborder.ordersystem.domain.erp.dto.CustomerUpdateRequest;
import com.weborder.ordersystem.domain.erp.entity.Customer;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;

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

    @Transactional(transactionManager = "erpTransactionManager")
    public CustomerResponse updateCustomer(Integer custCodeCode, CustomerUpdateRequest request, String loginId) {
        Customer customer = customerRepository.findById(custCodeCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처입니다: " + custCodeCode));

        customer.updateInfo(
                request.getCustCodeNum(), request.getCustCodeName(), request.getCustCodeSano(),
                request.getCustCodePart1(), request.getCustCodePart2(),
                request.getCustCodeUname1(), request.getCustCodeUname2(),
                request.getCustCodeUtel1(), request.getCustCodeUtel2(),
                request.getCustCodeFax(), request.getCustCodeEmail(),
                request.getCustCodePost(), request.getCustCodeAddr1(),
                request.getCustCodeAddr2(), request.getCustCodeAddr(),
                request.getCustCodeRemark(),
                request.getCustCodeUseAcc(), request.getCustCodeUsePur(), request.getCustCodeUsePos(),
                request.getCustCodeRooms(),
                loginId);

        customerRepository.save(customer);
        log.info("거래처 수정 완료 - ID: {}, 이름: {}", custCodeCode, customer.getDisplayName());

        return convertToResponse(customer);
    }

    @Transactional(transactionManager = "erpTransactionManager")
    public CustomerResponse createCustomer(CustomerCreateRequest request, String loginId) {
        Integer maxCode = customerRepository.findMaxCustCodeCode();
        int newCode = (maxCode != null ? maxCode : 0) + 1;

        LocalDateTime now = LocalDateTime.now();
        String addr = request.getCustCodeAddr() != null ? request.getCustCodeAddr() : "";
        int useAcc = request.getCustCodeUseAcc() != null ? request.getCustCodeUseAcc() : 1;
        int usePur = request.getCustCodeUsePur() != null ? request.getCustCodeUsePur() : 1;
        int usePos = request.getCustCodeUsePos() != null ? request.getCustCodeUsePos() : 1;

        Customer customer = Customer.builder()
                .custCodeCode(newCode)
                .custCodeNum(defaultStr(request.getCustCodeNum()))
                .custCodeDcod("0090010001")
                .custCodeName(defaultStr(request.getCustCodeName()))
                .custCodeWord("")
                .custCodeAnam(defaultStr(request.getCustCodeName()))
                .custCodeSano(defaultStr(request.getCustCodeSano()))
                .custCodePart1(defaultStr(request.getCustCodePart1()))
                .custCodePart2(defaultStr(request.getCustCodePart2()))
                .custCodeUname1(defaultStr(request.getCustCodeUname1()))
                .custCodeUtel1(defaultStr(request.getCustCodeUtel1()))
                .custCodeUname2(defaultStr(request.getCustCodeUname2()))
                .custCodeUtel2(defaultStr(request.getCustCodeUtel2()))
                .custCodeUname3("")
                .custCodeUtel3("")
                .custCodeFax(defaultStr(request.getCustCodeFax()))
                .custCodePost(defaultStr(request.getCustCodePost()))
                .custCodeAddr1(defaultStr(request.getCustCodeAddr1()))
                .custCodeAddr2(defaultStr(request.getCustCodeAddr2()))
                .custCodeAddr(addr)
                .custCodeEmail(defaultStr(request.getCustCodeEmail()))
                .custCodeHttp("http://")
                .custCodeSawon(0)
                .custCodeBuse(0)
                .custCodeBank("")
                .custCodeBkname("")
                .custCodeBkno("")
                .custCodeBkuname("")
                .custCodeCountry("0040010410")
                .custCodeLocal("0050010001")
                .custCodeUseAcc(useAcc)
                .custCodeUsePur(usePur)
                .custCodeUsePos(usePos)
                .custCodeBubin("")
                .custCodeOcust(0)
                .custCodeTdiv("")
                .custCodeLimit(BigDecimal.ZERO)
                .custCodeSdate("")
                .custCodeEdate("")
                .custCodePdate("")
                .custCodeMcharge(BigDecimal.ZERO)
                .custCodePtype("")
                .custCodeHidePrice(0)
                .custCodeRooms(request.getCustCodeRooms() != null ? request.getCustCodeRooms() : 0)
                .custCodeRemark(defaultStr(request.getCustCodeRemark()))
                .custCodeFdate(now)
                .custCodeFuser(loginId)
                .custCodeLdate(now)
                .custCodeLuser(loginId)
                .build();

        customerRepository.save(customer);
        log.info("거래처 생성 완료 - ID: {}, 이름: {}", newCode, customer.getDisplayName());

        return convertToResponse(customer);
    }

    private String defaultStr(String value) {
        return value != null ? value : "";
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