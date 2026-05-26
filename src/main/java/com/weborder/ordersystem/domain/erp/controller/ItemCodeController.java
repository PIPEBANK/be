package com.weborder.ordersystem.domain.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weborder.ordersystem.domain.erp.dto.ItemSearchResponse;
import com.weborder.ordersystem.domain.erp.service.ItemCodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/erp/items")
@RequiredArgsConstructor
public class ItemCodeController {
    
    private final ItemCodeService itemCodeService;

    /**
     * 품목 검색 (제품명과 규격을 분리해서 검색) - 2중 검색 및 AND/OR 연산자 지원
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ItemSearchResponse>> searchItemsByNameAndSpec(
            @RequestParam(required = false) String itemName1,
            @RequestParam(required = false) String itemName2,
            @RequestParam(required = false) String spec1,
            @RequestParam(required = false) String spec2,
            @RequestParam(defaultValue = "AND") String itemNameOperator,
            @RequestParam(defaultValue = "AND") String specOperator,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String spec,
            @RequestParam(required = false) String itemNum,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        String finalItemName1 = itemName1 != null ? itemName1 : itemName;
        String finalSpec1 = spec1 != null ? spec1 : spec;
        
        Page<ItemSearchResponse> items = itemCodeService.searchItemsByNameAndSpec(
            finalItemName1, itemName2, finalSpec1, spec2, itemNameOperator, specOperator,
            itemNum, page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
}
