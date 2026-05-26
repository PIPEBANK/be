package com.weborder.ordersystem.domain.erp.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weborder.ordersystem.domain.erp.dto.ItemSearchResponse;
import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.repository.CommonCode3Repository;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.StockDateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCodeService {
    
    private final ItemCodeRepository itemCodeRepository;
    private final CommonCode3Repository commonCode3Repository;
    private final CustomerRepository customerRepository;
    private final StockDateRepository stockDateRepository;

    /**
     * 품목 검색 (제품명과 규격을 분리해서 검색) - 2중 검색 및 AND/OR 연산자 지원
     */
    public Page<ItemSearchResponse> searchItemsByNameAndSpec(String itemName1, String itemName2, 
                                                           String spec1, String spec2,
                                                           String itemNameOperator, String specOperator,
                                                           String itemNum,
                                                           int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ItemCode> itemPage;
        
        boolean itemNameIsOr = "OR".equalsIgnoreCase(itemNameOperator);
        boolean specIsOr = "OR".equalsIgnoreCase(specOperator);
        
        if (!itemNameIsOr && !specIsOr) {
            itemPage = itemCodeRepository.searchByNameAndSpecWithAndAnd(itemName1, itemName2, spec1, spec2, itemNum, pageable);
        } else if (itemNameIsOr && !specIsOr) {
            itemPage = itemCodeRepository.searchByNameAndSpecWithOrAnd(itemName1, itemName2, spec1, spec2, itemNum, pageable);
        } else if (!itemNameIsOr && specIsOr) {
            itemPage = itemCodeRepository.searchByNameAndSpecWithAndOr(itemName1, itemName2, spec1, spec2, itemNum, pageable);
        } else {
            itemPage = itemCodeRepository.searchByNameAndSpecWithOrOr(itemName1, itemName2, spec1, spec2, itemNum, pageable);
        }
        
        return itemPage.map(this::convertToItemSearchResponse);
    }
    
    private ItemSearchResponse convertToItemSearchResponse(ItemCode item) {
        BigDecimal stockQuantity = stockDateRepository.findStockQuantityByItemCodeAndBuse7(item.getItemCodeCode())
                .orElse(BigDecimal.ZERO);
        
        BigDecimal availableStock = stockDateRepository.findAvailableStockQuantity(2, item.getItemCodeCode());
        if (availableStock == null) {
            availableStock = BigDecimal.ZERO;
        }
        
        BigDecimal finalAvailableStock = stockQuantity.add(availableStock);
                
        return ItemSearchResponse.of(
            item.getItemCodeCode(),
            item.getItemCodeNum(),
            item.getItemCodeHnam(),
            item.getItemCodeSpec(),
            item.getItemCodeSpec2(),
            item.getItemCodeUnit(),
            item.getItemCodeSrate(),
            item.getItemCodeBrand(),
            stockQuantity,
            finalAvailableStock
        );
    }
}
