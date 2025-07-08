package com.pipebank.ordersystem.domain.erp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.erp.dto.ItemDiv1Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemDiv2Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemDiv3Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemDiv4Response;
import com.pipebank.ordersystem.domain.erp.dto.ItemSearchResponse;
import com.pipebank.ordersystem.domain.erp.dto.ItemSelectionResponse;
import com.pipebank.ordersystem.domain.erp.service.ItemCodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/erp/items")
@RequiredArgsConstructor
public class ItemCodeController {
    
    private final ItemCodeService itemCodeService;
    
    /**
     * 1단계: 제품종류(DIV1) 목록 조회
     */
    @GetMapping("/div1")
    public ResponseEntity<List<ItemDiv1Response>> getItemDiv1List() {
        List<ItemDiv1Response> items = itemCodeService.getItemDiv1List();
        return ResponseEntity.ok(items);
    }
    
    /**
     * 2단계: 제품군(DIV2) 목록 조회 (DIV1 기준)
     */
    @GetMapping("/div2/{div1}")
    public ResponseEntity<List<ItemDiv2Response>> getItemDiv2List(@PathVariable String div1) {
        List<ItemDiv2Response> items = itemCodeService.getItemDiv2List(div1);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 3단계: 제품용도(DIV3) 목록 조회 (DIV1+DIV2 기준)
     */
    @GetMapping("/div3/{div1}/{div2}")
    public ResponseEntity<List<ItemDiv3Response>> getItemDiv3List(
            @PathVariable String div1, 
            @PathVariable String div2) {
        List<ItemDiv3Response> items = itemCodeService.getItemDiv3List(div1, div2);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 4단계: 제품기능(DIV4) 목록 조회 (DIV1+DIV2+DIV3 기준) - 주문가능한 항목만
     */
    @GetMapping("/div4/{div1}/{div2}/{div3}")
    public ResponseEntity<List<ItemDiv4Response>> getItemDiv4List(
            @PathVariable String div1, 
            @PathVariable String div2, 
            @PathVariable String div3) {
        List<ItemDiv4Response> items = itemCodeService.getItemDiv4List(div1, div2, div3);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 5단계: 최종 품목(ItemCode) 목록 조회 (DIV1+DIV2+DIV3+DIV4 기준) - 주문가능한 항목만
     */
    @GetMapping("/final/{div1}/{div2}/{div3}/{div4}")
    public ResponseEntity<Page<ItemSelectionResponse>> getItemsByDivision(
            @PathVariable String div1,
            @PathVariable String div2,
            @PathVariable String div3,
            @PathVariable String div4,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemSelectionResponse> items = itemCodeService.getItemsByDivision(
            div1, div2, div3, div4, page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 품목 검색 (제품명과 규격을 분리해서 검색)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ItemSearchResponse>> searchItemsByNameAndSpec(
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String spec,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCodeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ItemSearchResponse> items = itemCodeService.searchItemsByNameAndSpec(
            itemName, spec, page, size, sortBy, sortDir);
        return ResponseEntity.ok(items);
    }
} 