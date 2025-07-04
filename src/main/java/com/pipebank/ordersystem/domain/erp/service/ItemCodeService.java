package com.pipebank.ordersystem.domain.erp.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.erp.dto.ItemCodeResponse;
import com.pipebank.ordersystem.domain.erp.dto.ItemCodeSearchRequest;
import com.pipebank.ordersystem.domain.erp.entity.ItemCode;
import com.pipebank.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.pipebank.ordersystem.domain.erp.repository.CommonCode3Repository;
import com.pipebank.ordersystem.domain.erp.repository.CustomerRepository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv1Repository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv2Repository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv3Repository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv4Repository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCodeService {
    
    private final ItemCodeRepository itemCodeRepository;
    private final CommonCode3Repository commonCode3Repository;
    private final CustomerRepository customerRepository;
    private final ItemDiv1Repository itemDiv1Repository;
    private final ItemDiv2Repository itemDiv2Repository;
    private final ItemDiv3Repository itemDiv3Repository;
    private final ItemDiv4Repository itemDiv4Repository;
    
    /**
     * 모든 품목 조회
     */
    public List<ItemCodeResponse> getAllItems() {
        List<ItemCode> items = itemCodeRepository.findAll();
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 품목 코드로 조회
     */
    public Optional<ItemCodeResponse> getItemByCode(Integer itemCode) {
        return itemCodeRepository.findById(itemCode)
                .map(this::convertToResponse);
    }
    
    /**
     * 품목번호로 조회
     */
    public Optional<ItemCodeResponse> getItemByNum(String itemNum) {
        return itemCodeRepository.findByItemCodeNum(itemNum)
                .map(this::convertToResponse);
    }
    
    /**
     * 사용중인 품목만 조회
     */
    public List<ItemCodeResponse> getActiveItems() {
        List<ItemCode> items = itemCodeRepository.findByItemCodeUse(1);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 오더센터 사용 품목 조회
     */
    public List<ItemCodeResponse> getOrderableItems() {
        List<ItemCode> items = itemCodeRepository.findOrderableItems();
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 재고 관리 품목 조회
     */
    public List<ItemCodeResponse> getStockManagedItems() {
        List<ItemCode> items = itemCodeRepository.findStockManagedItems();
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 키워드로 품목 검색
     */
    public List<ItemCodeResponse> searchItems(String keyword) {
        List<ItemCode> items = itemCodeRepository.searchByKeyword(keyword);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용중인 품목 키워드 검색
     */
    public List<ItemCodeResponse> searchActiveItems(String keyword) {
        List<ItemCode> items = itemCodeRepository.searchActiveByKeyword(keyword);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 제품 분류별 품목 조회
     */
    public List<ItemCodeResponse> getItemsByProductDivision(String div1, String div2, String div3, String div4) {
        List<ItemCode> items = itemCodeRepository.findByProductDivision(div1, div2, div3, div4);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 브랜드별 품목 조회
     */
    public List<ItemCodeResponse> getItemsByBrand(String brand) {
        List<ItemCode> items = itemCodeRepository.findByItemCodeBrand(brand);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 매입처별 품목 조회
     */
    public List<ItemCodeResponse> getItemsByPurchaseCustomer(Integer custCode) {
        List<ItemCode> items = itemCodeRepository.findByItemCodePcust(custCode);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 매출처별 품목 조회
     */
    public List<ItemCodeResponse> getItemsBySalesCustomer(Integer custCode) {
        List<ItemCode> items = itemCodeRepository.findByItemCodeScust(custCode);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 품목 코드 범위 조회
     */
    public List<ItemCodeResponse> getItemsByCodeRange(Integer startCode, Integer endCode) {
        List<ItemCode> items = itemCodeRepository.findByItemCodeCodeBetween(startCode, endCode);
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 복합 검색 조건으로 품목 조회
     */
    public List<ItemCodeResponse> searchItemsWithConditions(ItemCodeSearchRequest request) {
        // 기본적으로 키워드 검색 사용
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            if (request.getActiveOnly() != null && request.getActiveOnly()) {
                return searchActiveItems(request.getKeyword());
            } else {
                return searchItems(request.getKeyword());
            }
        }
        
        // 제품 분류별 검색
        if (request.getItemCodeDiv1() != null || request.getItemCodeDiv2() != null || 
            request.getItemCodeDiv3() != null || request.getItemCodeDiv4() != null) {
            return getItemsByProductDivision(
                request.getItemCodeDiv1(), 
                request.getItemCodeDiv2(), 
                request.getItemCodeDiv3(), 
                request.getItemCodeDiv4()
            );
        }
        
        // 브랜드별 검색
        if (request.getItemCodeBrand() != null) {
            return getItemsByBrand(request.getItemCodeBrand());
        }
        
        // 매입처별 검색
        if (request.getItemCodePcust() != null) {
            return getItemsByPurchaseCustomer(request.getItemCodePcust());
        }
        
        // 매출처별 검색
        if (request.getItemCodeScust() != null) {
            return getItemsBySalesCustomer(request.getItemCodeScust());
        }
        
        // 코드 범위 검색
        if (request.getStartCode() != null && request.getEndCode() != null) {
            return getItemsByCodeRange(request.getStartCode(), request.getEndCode());
        }
        
        // 기본: 활성 품목만 조회
        if (request.getActiveOnly() != null && request.getActiveOnly()) {
            return getActiveItems();
        }
        
        // 조건이 없으면 전체 조회
        return getAllItems();
    }
    
    // ===== 페이징 메서드들 =====
    
    /**
     * 모든 품목 페이징 조회
     */
    public Page<ItemCodeResponse> getAllItemsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findAll(pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 사용중인 품목 페이징 조회
     */
    public Page<ItemCodeResponse> getActiveItemsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findByItemCodeUse(1, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 키워드로 품목 검색 (페이징)
     */
    public Page<ItemCodeResponse> searchItemsPaged(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.searchByKeyword(keyword, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 사용중인 품목 키워드 검색 (페이징)
     */
    public Page<ItemCodeResponse> searchActiveItemsPaged(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.searchActiveByKeyword(keyword, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 제품 분류별 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> getItemsByProductDivisionPaged(String div1, String div2, String div3, String div4, 
                                                                int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findByProductDivision(div1, div2, div3, div4, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 브랜드별 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> getItemsByBrandPaged(String brand, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findByItemCodeBrand(brand, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 매입처별 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> getItemsByPurchaseCustomerPaged(Integer custCode, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findByItemCodePcust(custCode, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 매출처별 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> getItemsBySalesCustomerPaged(Integer custCode, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findByItemCodeScust(custCode, pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 오더센터 사용 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> getOrderableItemsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findOrderableItems(pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 재고 관리 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> getStockManagedItemsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findStockManagedItems(pageable);
        return itemPage.map(this::convertToResponse);
    }
    
    /**
     * 복합 검색 조건으로 품목 조회 (페이징)
     */
    public Page<ItemCodeResponse> searchItemsWithConditionsPaged(ItemCodeSearchRequest request, 
                                                               int page, int size, String sortBy, String sortDir) {
        // 기본적으로 키워드 검색 사용
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            if (request.getActiveOnly() != null && request.getActiveOnly()) {
                return searchActiveItemsPaged(request.getKeyword(), page, size, sortBy, sortDir);
            } else {
                return searchItemsPaged(request.getKeyword(), page, size, sortBy, sortDir);
            }
        }
        
        // 제품 분류별 검색
        if (request.getItemCodeDiv1() != null || request.getItemCodeDiv2() != null || 
            request.getItemCodeDiv3() != null || request.getItemCodeDiv4() != null) {
            return getItemsByProductDivisionPaged(
                request.getItemCodeDiv1(), 
                request.getItemCodeDiv2(), 
                request.getItemCodeDiv3(), 
                request.getItemCodeDiv4(),
                page, size, sortBy, sortDir
            );
        }
        
        // 브랜드별 검색
        if (request.getItemCodeBrand() != null) {
            return getItemsByBrandPaged(request.getItemCodeBrand(), page, size, sortBy, sortDir);
        }
        
        // 매입처별 검색
        if (request.getItemCodePcust() != null) {
            return getItemsByPurchaseCustomerPaged(request.getItemCodePcust(), page, size, sortBy, sortDir);
        }
        
        // 매출처별 검색
        if (request.getItemCodeScust() != null) {
            return getItemsBySalesCustomerPaged(request.getItemCodeScust(), page, size, sortBy, sortDir);
        }
        
        // 기본: 활성 품목만 조회
        if (request.getActiveOnly() != null && request.getActiveOnly()) {
            return getActiveItemsPaged(page, size, sortBy, sortDir);
        }
        
        // 조건이 없으면 전체 조회
        return getAllItemsPaged(page, size, sortBy, sortDir);
    }
    
    /**
     * ItemCode 엔티티를 ItemCodeResponse DTO로 변환
     */
    private ItemCodeResponse convertToResponse(ItemCode item) {
        ItemCodeResponse.ItemCodeResponseBuilder builder = ItemCodeResponse.builder()
                .itemCodeCode(item.getItemCodeCode())
                .itemCodeNum(item.getItemCodeNum())
                .itemCodeDcod(item.getItemCodeDcod())
                .itemCodePcod(item.getItemCodePcod())
                .itemCodeScod(item.getItemCodeScod())
                .itemCodeHnam(item.getItemCodeHnam())
                .itemCodeEnam(item.getItemCodeEnam())
                .itemCodeWord(item.getItemCodeWord())
                .itemCodeSpec(item.getItemCodeSpec())
                .itemCodeSpec2(item.getItemCodeSpec2())
                .itemCodeUnit(item.getItemCodeUnit())
                .itemCodePcust(item.getItemCodePcust())
                .itemCodePcust2(item.getItemCodePcust2())
                .itemCodeScust(item.getItemCodeScust())
                .itemCodeCalc(item.getItemCodeCalc())
                .isCalculated(item.isCalculated())
                .itemCodeSdiv(item.getItemCodeSdiv())
                .itemCodeVdiv(item.getItemCodeVdiv())
                .hasVat(item.hasVat())
                .itemCodeAdiv(item.getItemCodeAdiv())
                .hasAdvance(item.hasAdvance())
                .itemCodePrate(item.getItemCodePrate())
                .itemCodePlrate(item.getItemCodePlrate())
                .itemCodePlrdate(item.getItemCodePlrdate())
                .itemCodeSrate(item.getItemCodeSrate())
                .itemCodeSlrate(item.getItemCodeSlrate())
                .itemCodeSlrdate(item.getItemCodeSlrdate())
                .itemCodeLdiv(item.getItemCodeLdiv())
                .itemCodeUse(item.getItemCodeUse())
                .isActive(item.isActive())
                .itemCodeAvrate(item.getItemCodeAvrate())
                .itemCodeDsdiv(item.getItemCodeDsdiv())
                .itemCodeBrand(item.getItemCodeBrand())
                .itemCodePlace(item.getItemCodePlace())
                .itemCodeNative(item.getItemCodeNative())
                .itemCodeStock(item.getItemCodeStock())
                .itemCodeBitem(item.getItemCodeBitem())
                .itemCodePitem(item.getItemCodePitem())
                .itemCodeChng(item.getItemCodeChng())
                .itemCodeAuto(item.getItemCodeAuto())
                .isAutoProcessed(item.isAutoProcessed())
                .itemCodeMarket(item.getItemCodeMarket())
                .isMarketItem(item.isMarketItem())
                .itemCodeKitchen(item.getItemCodeKitchen())
                .itemCodePrint(item.getItemCodePrint())
                .isPrintable(item.isPrintable())
                .itemCodeDclock(item.getItemCodeDclock())
                .isDcLocked(item.isDcLocked())
                .itemCodeLproc(item.getItemCodeLproc())
                .itemCodeUrate(item.getItemCodeUrate())
                .itemCodeOption(item.getItemCodeOption())
                .hasOption(item.hasOption())
                .itemCodeNstock(item.getItemCodeNstock())
                .isNoStock(item.isNoStock())
                .itemCodeSerial(item.getItemCodeSerial())
                .itemCodeDiv1(item.getItemCodeDiv1())
                .itemCodeDiv2(item.getItemCodeDiv2())
                .itemCodeDiv3(item.getItemCodeDiv3())
                .itemCodeDiv4(item.getItemCodeDiv4())
                .itemCodeRemark(item.getItemCodeRemark())
                .itemCodeFdate(item.getItemCodeFdate())
                .itemCodeFuser(item.getItemCodeFuser())
                .itemCodeLdate(item.getItemCodeLdate())
                .itemCodeLuser(item.getItemCodeLuser())
                .itemCodeMoq(item.getItemCodeMoq())
                .itemCodeMweight(item.getItemCodeMweight())
                .itemCodeEaweight(item.getItemCodeEaweight())
                .itemCodeClass(item.getItemCodeClass())
                .itemCodeUnitsrate(item.getItemCodeUnitsrate())
                .itemCodeOrder(item.getItemCodeOrder())
                .isOrderable(item.isOrderable())
                .itemCodeWamt(item.getItemCodeWamt())
                .displayName(item.getDisplayName())
                .shortName(item.getShortName())
                .fullSpec(item.getFullSpec())
                .brandInfo(item.getBrandInfo())
                .origin(item.getOrigin());
        
        // 코드 표시명 설정
        setCodeDisplayNames(builder, item);
        
        return builder.build();
    }
    
    /**
     * 코드 필드들의 표시명을 설정
     */
    private void setCodeDisplayNames(ItemCodeResponse.ItemCodeResponseBuilder builder, ItemCode item) {
        // 공통코드 표시명 설정
        if (item.getItemCodeDcod() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeDcod())
                .ifPresent(code -> builder.itemCodeDcodName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodePcod() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodePcod())
                .ifPresent(code -> builder.itemCodePcodName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodeScod() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeScod())
                .ifPresent(code -> builder.itemCodeScodName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodeLdiv() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeLdiv())
                .ifPresent(code -> builder.itemCodeLdivName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodeDsdiv() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeDsdiv())
                .ifPresent(code -> builder.itemCodeDsdivName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodeKitchen() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeKitchen())
                .ifPresent(code -> builder.itemCodeKitchenName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodeLproc() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeLproc())
                .ifPresent(code -> builder.itemCodeLprocName(code.getCommCod3Hnam()));
        }
        
        if (item.getItemCodeClass() != null) {
            commonCode3Repository.findByCommCod3Code(item.getItemCodeClass())
                .ifPresent(code -> builder.itemCodeClassName(code.getCommCod3Hnam()));
        }
        
        // 거래처명 설정
        if (item.getItemCodePcust() != null) {
            customerRepository.findById(item.getItemCodePcust())
                .ifPresent(customer -> builder.itemCodePcustName(customer.getCustCodeName()));
        }
        
        if (item.getItemCodePcust2() != null) {
            customerRepository.findById(item.getItemCodePcust2())
                .ifPresent(customer -> builder.itemCodePcust2Name(customer.getCustCodeName()));
        }
        
        if (item.getItemCodeScust() != null) {
            customerRepository.findById(item.getItemCodeScust())
                .ifPresent(customer -> builder.itemCodeScustName(customer.getCustCodeName()));
        }
        
        // 제품 분류명 설정
        if (item.getItemCodeDiv1() != null) {
            itemDiv1Repository.findById(item.getItemCodeDiv1())
                .ifPresent(div -> builder.itemCodeDiv1Name(div.getItemDiv1Name()));
        }
        
        if (item.getItemCodeDiv1() != null && item.getItemCodeDiv2() != null) {
            itemDiv2Repository.findByItemDiv2Div1AndItemDiv2Code(item.getItemCodeDiv1(), item.getItemCodeDiv2())
                .ifPresent(div -> builder.itemCodeDiv2Name(div.getItemDiv2Name()));
        }
        
        if (item.getItemCodeDiv1() != null && item.getItemCodeDiv2() != null && item.getItemCodeDiv3() != null) {
            itemDiv3Repository.findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3Code(
                item.getItemCodeDiv1(), item.getItemCodeDiv2(), item.getItemCodeDiv3())
                .ifPresent(div -> builder.itemCodeDiv3Name(div.getItemDiv3Name()));
        }
        
        if (item.getItemCodeDiv1() != null && item.getItemCodeDiv2() != null && 
            item.getItemCodeDiv3() != null && item.getItemCodeDiv4() != null) {
            itemDiv4Repository.findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3AndItemDiv4Code(
                item.getItemCodeDiv1(), item.getItemCodeDiv2(), item.getItemCodeDiv3(), item.getItemCodeDiv4())
                .ifPresent(div -> builder.itemCodeDiv4Name(div.getItemDiv4Name()));
        }
    }
} 