package com.pipebank.ordersystem.domain.erp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.erp.dto.ItemDiv1Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemDiv2Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemDiv3Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemDiv4Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemSearchResponse;
import com.pipebank.ordersystem.domain.erp.dto.ItemSelectionResponse;
import com.pipebank.ordersystem.domain.erp.entity.ItemCode;
import com.pipebank.ordersystem.domain.erp.entity.ItemDiv1;
import com.pipebank.ordersystem.domain.erp.entity.ItemDiv2;
import com.pipebank.ordersystem.domain.erp.entity.ItemDiv3;
import com.pipebank.ordersystem.domain.erp.entity.ItemDiv4;
import com.pipebank.ordersystem.domain.erp.repository.CommonCode3Repository;
import com.pipebank.ordersystem.domain.erp.repository.CustomerRepository;
import com.pipebank.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv1Repository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv2Repository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv3Repository;
import com.pipebank.ordersystem.domain.erp.repository.ItemDiv4Repository;
import com.pipebank.ordersystem.domain.erp.repository.StockDateRepository;

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
    private final StockDateRepository stockDateRepository;
    
    /**
     * 1단계: 제품종류(DIV1) 목록 조회
     */
    public List<ItemDiv1Response> getItemDiv1List() {
        List<ItemDiv1> itemDiv1List = itemDiv1Repository.findByItemDiv1UseOrderByItemDiv1Code(1);
        return itemDiv1List.stream()
                .map(div1 -> ItemDiv1Response.of(
                    div1.getItemDiv1Code(),
                    div1.getItemDiv1Name(),
                    div1.isActive()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 2단계: 제품군(DIV2) 목록 조회 (DIV1 기준)
     */
    public List<ItemDiv2Response> getItemDiv2List(String div1) {
        List<ItemDiv2> itemDiv2List = itemDiv2Repository.findByItemDiv2Div1AndItemDiv2UseOrderByItemDiv2Code(div1, 1);
        return itemDiv2List.stream()
                .map(div2 -> ItemDiv2Response.of(
                    div2.getItemDiv2Div1(),
                    div2.getItemDiv2Code(),
                    div2.getItemDiv2Name(),
                    div2.isActive()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 3단계: 제품용도(DIV3) 목록 조회 (DIV1+DIV2 기준)
     */
    public List<ItemDiv3Response> getItemDiv3List(String div1, String div2) {
        List<ItemDiv3> itemDiv3List = itemDiv3Repository.findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3UseOrderByItemDiv3Code(div1, div2, 1);
        return itemDiv3List.stream()
                .map(div3 -> ItemDiv3Response.of(
                    div3.getItemDiv3Div1(),
                    div3.getItemDiv3Div2(),
                    div3.getItemDiv3Code(),
                    div3.getItemDiv3Name(),
                    div3.isActive()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 4단계: 제품기능(DIV4) 목록 조회 (DIV1+DIV2+DIV3 기준) - 주문가능한 항목만
     */
    public List<ItemDiv4Response> getItemDiv4List(String div1, String div2, String div3) {
        List<ItemDiv4> itemDiv4List = itemDiv4Repository.findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3AndItemDiv4UseAndItemDiv4OrderOrderByItemDiv4Code(div1, div2, div3, 1, 1);
        return itemDiv4List.stream()
                .map(div4 -> ItemDiv4Response.of(
                    div4.getItemDiv4Div1(),
                    div4.getItemDiv4Div2(),
                    div4.getItemDiv4Div3(),
                    div4.getItemDiv4Code(),
                    div4.getItemDiv4Name(),
                    div4.isActive(),
                    div4.isOrderable()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 5단계: 최종 품목(ItemCode) 목록 조회 (DIV1+DIV2+DIV3+DIV4 기준) - 주문가능한 항목만
     */
    public Page<ItemSelectionResponse> getItemsByDivision(String div1, String div2, String div3, String div4,
                                                         int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.findByItemCodeDiv1AndItemCodeDiv2AndItemCodeDiv3AndItemCodeDiv4AndItemCodeUseAndItemCodeOrder(
            div1, div2, div3, div4, 1, 1, pageable);
        
        return itemPage.map(this::convertToItemSelectionResponse);
    }
    
    /**
     * 품목 검색 (제품명과 규격을 분리해서 검색)
     */
    public Page<ItemSearchResponse> searchItemsByNameAndSpec(String itemName, String spec, 
                                                           int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ItemCode> itemPage = itemCodeRepository.searchByNameAndSpec(itemName, spec, pageable);
        return itemPage.map(this::convertToItemSearchResponse);
    }
    
    /**
     * ItemCode 엔티티를 ItemSelectionResponse DTO로 변환
     */
    private ItemSelectionResponse convertToItemSelectionResponse(ItemCode item) {
        // 분류명 조회
        String div1Name = getDiv1Name(item.getItemCodeDiv1());
        String div2Name = getDiv2Name(item.getItemCodeDiv1(), item.getItemCodeDiv2());
        String div3Name = getDiv3Name(item.getItemCodeDiv1(), item.getItemCodeDiv2(), item.getItemCodeDiv3());
        String div4Name = getDiv4Name(item.getItemCodeDiv1(), item.getItemCodeDiv2(), item.getItemCodeDiv3(), item.getItemCodeDiv4());
        
        // 재고수량 조회 (부서7 기준)
        BigDecimal stockQuantity = stockDateRepository.findStockQuantityByItemCodeAndBuse7(item.getItemCodeCode())
                .orElse(BigDecimal.ZERO);
        
        return ItemSelectionResponse.of(
            item.getItemCodeCode(),
            item.getItemCodeNum(),
            item.getItemCodeHnam(),
            item.getItemCodeSpec(),
            item.getItemCodeSpec2(),
            item.getItemCodeUnit(),
            item.getItemCodeSrate(),
            item.getItemCodeBrand(),
            item.isActive(),
            item.isOrderable(),
            item.getItemCodeDiv1(),
            item.getItemCodeDiv2(),
            item.getItemCodeDiv3(),
            item.getItemCodeDiv4(),
            div1Name,
            div2Name,
            div3Name,
            div4Name,
            stockQuantity
        );
    }
    
    /**
     * ItemCode 엔티티를 ItemSearchResponse DTO로 변환 (검색용 간단한 정보)
     */
    private ItemSearchResponse convertToItemSearchResponse(ItemCode item) {
        // 재고수량 조회 (부서7 기준)
        BigDecimal stockQuantity = stockDateRepository.findStockQuantityByItemCodeAndBuse7(item.getItemCodeCode())
                .orElse(BigDecimal.ZERO);
                
        return ItemSearchResponse.of(
            item.getItemCodeCode(),
            item.getItemCodeNum(),
            item.getItemCodeHnam(),
            item.getItemCodeSpec(),
            item.getItemCodeUnit(),
            item.getItemCodeSrate(),
            item.getItemCodeBrand(),
            stockQuantity
        );
    }
    
    // 분류명 조회 헬퍼 메서드들
    private String getDiv1Name(String div1) {
        return itemDiv1Repository.findById(div1)
                .map(ItemDiv1::getItemDiv1Name)
                .orElse("");
    }
    
    private String getDiv2Name(String div1, String div2) {
        return itemDiv2Repository.findByItemDiv2Div1AndItemDiv2Code(div1, div2)
                .map(ItemDiv2::getItemDiv2Name)
                .orElse("");
    }
    
    private String getDiv3Name(String div1, String div2, String div3) {
        return itemDiv3Repository.findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3Code(div1, div2, div3)
                .map(ItemDiv3::getItemDiv3Name)
                .orElse("");
    }
    
    private String getDiv4Name(String div1, String div2, String div3, String div4) {
        return itemDiv4Repository.findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3AndItemDiv4Code(div1, div2, div3, div4)
                .map(ItemDiv4::getItemDiv4Name)
                .orElse("");
    }
} 