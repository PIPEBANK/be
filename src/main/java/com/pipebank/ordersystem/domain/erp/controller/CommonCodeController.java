package com.pipebank.ordersystem.domain.erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.erp.dto.CommonCode1Response;
import com.pipebank.ordersystem.domain.erp.dto.CommonCode2Response;
import com.pipebank.ordersystem.domain.erp.dto.CommonCode3Response;
import com.pipebank.ordersystem.domain.erp.entity.SosokCode;
import com.pipebank.ordersystem.domain.erp.entity.InsaMast;
import com.pipebank.ordersystem.domain.erp.entity.BuseCode;
import com.pipebank.ordersystem.domain.erp.service.CommonCodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/erp/common-codes")
@RequiredArgsConstructor
@Slf4j
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    // ========== Level1 (대분류) APIs ==========

    /**
     * 모든 대분류 코드 조회
     */
    @GetMapping("/level1")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode1Response>> getAllLevel1Codes() {
        log.info("대분류 코드 목록 조회 API 호출");
        List<CommonCode1Response> codes = commonCodeService.getAllLevel1Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 활성화된 대분류 코드 조회
     */
    @GetMapping("/level1/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode1Response>> getActiveLevel1Codes() {
        log.info("활성화된 대분류 코드 목록 조회 API 호출");
        List<CommonCode1Response> codes = commonCodeService.getActiveLevel1Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 대분류 코드 조회
     */
    @GetMapping("/level1/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CommonCode1Response> getLevel1Code(@PathVariable String code) {
        log.info("대분류 코드 조회 API 호출 - 코드: {}", code);
        CommonCode1Response commonCode = commonCodeService.getLevel1Code(code);
        return ResponseEntity.ok(commonCode);
    }

    // ========== Level2 (중분류) APIs ==========

    /**
     * 모든 중분류 코드 조회
     */
    @GetMapping("/level2")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode2Response>> getAllLevel2Codes() {
        log.info("중분류 코드 목록 조회 API 호출");
        List<CommonCode2Response> codes = commonCodeService.getAllLevel2Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 활성화된 중분류 코드 조회
     */
    @GetMapping("/level2/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode2Response>> getActiveLevel2Codes() {
        log.info("활성화된 중분류 코드 목록 조회 API 호출");
        List<CommonCode2Response> codes = commonCodeService.getActiveLevel2Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 대분류의 중분류 코드들 조회
     */
    @GetMapping("/level2/parent/{cod1}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode2Response>> getLevel2CodesByParent(@PathVariable String cod1) {
        log.info("특정 대분류의 중분류 코드 조회 API 호출 - 대분류: {}", cod1);
        List<CommonCode2Response> codes = commonCodeService.getLevel2CodesByParent(cod1);
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 대분류의 활성화된 중분류 코드들 조회
     */
    @GetMapping("/level2/parent/{cod1}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode2Response>> getActiveLevel2CodesByParent(@PathVariable String cod1) {
        log.info("특정 대분류의 활성화된 중분류 코드 조회 API 호출 - 대분류: {}", cod1);
        List<CommonCode2Response> codes = commonCodeService.getActiveLevel2CodesByParent(cod1);
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 중분류 코드 조회 (복합키)
     */
    @GetMapping("/level2/{cod1}/{cod2}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CommonCode2Response> getLevel2Code(@PathVariable String cod1, @PathVariable String cod2) {
        log.info("중분류 코드 조회 API 호출 - 대분류: {}, 중분류: {}", cod1, cod2);
        CommonCode2Response commonCode = commonCodeService.getLevel2Code(cod1, cod2);
        return ResponseEntity.ok(commonCode);
    }

    // ========== Level3 (소분류) APIs ==========

    /**
     * 모든 소분류 코드 조회
     */
    @GetMapping("/level3")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> getAllLevel3Codes() {
        log.info("소분류 코드 목록 조회 API 호출");
        List<CommonCode3Response> codes = commonCodeService.getAllLevel3Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 활성화된 소분류 코드 조회
     */
    @GetMapping("/level3/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> getActiveLevel3Codes() {
        log.info("활성화된 소분류 코드 목록 조회 API 호출");
        List<CommonCode3Response> codes = commonCodeService.getActiveLevel3Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 보기 가능한 소분류 코드 조회
     */
    @GetMapping("/level3/visible")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> getVisibleLevel3Codes() {
        log.info("보기 가능한 소분류 코드 목록 조회 API 호출");
        List<CommonCode3Response> codes = commonCodeService.getVisibleLevel3Codes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 대분류의 소분류 코드들 조회
     */
    @GetMapping("/level3/parent/{cod1}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> getLevel3CodesByParent(@PathVariable String cod1) {
        log.info("특정 대분류의 소분류 코드 조회 API 호출 - 대분류: {}", cod1);
        List<CommonCode3Response> codes = commonCodeService.getLevel3CodesByParent(cod1);
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 중분류의 소분류 코드들 조회
     */
    @GetMapping("/level3/parent/{cod1}/{cod2}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> getLevel3CodesByParent(@PathVariable String cod1, @PathVariable String cod2) {
        log.info("특정 중분류의 소분류 코드 조회 API 호출 - 대분류: {}, 중분류: {}", cod1, cod2);
        List<CommonCode3Response> codes = commonCodeService.getLevel3CodesByParent(cod1, cod2);
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 중분류의 활성화된 소분류 코드들 조회
     */
    @GetMapping("/level3/parent/{cod1}/{cod2}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> getActiveLevel3CodesByParent(@PathVariable String cod1, @PathVariable String cod2) {
        log.info("특정 중분류의 활성화된 소분류 코드 조회 API 호출 - 대분류: {}, 중분류: {}", cod1, cod2);
        List<CommonCode3Response> codes = commonCodeService.getActiveLevel3CodesByParent(cod1, cod2);
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 소분류 코드 조회 (복합키)
     */
    @GetMapping("/level3/{cod1}/{cod2}/{cod3}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CommonCode3Response> getLevel3Code(@PathVariable String cod1, @PathVariable String cod2, @PathVariable String cod3) {
        log.info("소분류 코드 조회 API 호출 - 대분류: {}, 중분류: {}, 소분류: {}", cod1, cod2, cod3);
        CommonCode3Response commonCode = commonCodeService.getLevel3Code(cod1, cod2, cod3);
        return ResponseEntity.ok(commonCode);
    }

    /**
     * 한글명으로 소분류 검색
     */
    @GetMapping("/level3/search/hangul")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> searchLevel3ByHangul(@RequestParam String name) {
        log.info("한글명으로 소분류 검색 API 호출 - 검색어: {}", name);
        List<CommonCode3Response> codes = commonCodeService.searchLevel3ByHangul(name);
        return ResponseEntity.ok(codes);
    }

    /**
     * 영어명으로 소분류 검색
     */
    @GetMapping("/level3/search/english")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommonCode3Response>> searchLevel3ByEnglish(@RequestParam String name) {
        log.info("영어명으로 소분류 검색 API 호출 - 검색어: {}", name);
        List<CommonCode3Response> codes = commonCodeService.searchLevel3ByEnglish(name);
        return ResponseEntity.ok(codes);
    }

    // ========== 유틸리티 APIs ==========

    /**
     * 코드값으로 표시명 조회 (Customer DCOD 변환용)
     */
    @GetMapping("/display-name/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> getDisplayNameByCode(@PathVariable String code) {
        log.info("코드로 표시명 조회 API 호출 - 코드: {}", code);
        String displayName = commonCodeService.getDisplayNameByCode(code);
        return ResponseEntity.ok(displayName);
    }

    // ========== 소속 코드 APIs ==========

    /**
     * 모든 소속 코드 조회
     */
    @GetMapping("/sosok")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<SosokCode>> getAllSosokCodes() {
        log.info("소속 코드 목록 조회 API 호출");
        List<SosokCode> codes = commonCodeService.getAllSosokCodes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 소속 코드 조회
     */
    @GetMapping("/sosok/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<SosokCode> getSosokCode(@PathVariable Integer code) {
        log.info("소속 코드 조회 API 호출 - 코드: {}", code);
        SosokCode sosokCode = commonCodeService.getSosokCode(code);
        return ResponseEntity.ok(sosokCode);
    }

    /**
     * 소속 코드명으로 검색
     */
    @GetMapping("/sosok/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<SosokCode>> searchSosokCodeByName(@RequestParam String name) {
        log.info("소속 코드명으로 검색 API 호출 - 검색어: {}", name);
        List<SosokCode> codes = commonCodeService.searchSosokCodeByName(name);
        return ResponseEntity.ok(codes);
    }

    /**
     * 소속 코드명 조회
     */
    @GetMapping("/sosok/{code}/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> getSosokCodeName(@PathVariable Integer code) {
        log.info("소속 코드명 조회 API 호출 - 코드: {}", code);
        String name = commonCodeService.getSosokCodeName(code);
        return ResponseEntity.ok(name);
    }

    // ========== 사원 정보 APIs ==========

    /**
     * 모든 사원 정보 조회
     */
    @GetMapping("/insa")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<InsaMast>> getAllInsaMasts() {
        log.info("사원 정보 목록 조회 API 호출");
        List<InsaMast> insaMasts = commonCodeService.getAllInsaMasts();
        return ResponseEntity.ok(insaMasts);
    }

    /**
     * 특정 사원 정보 조회
     */
    @GetMapping("/insa/{sano}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<InsaMast> getInsaMast(@PathVariable Integer sano) {
        log.info("사원 정보 조회 API 호출 - 사번: {}", sano);
        InsaMast insaMast = commonCodeService.getInsaMast(sano);
        return ResponseEntity.ok(insaMast);
    }

    /**
     * 사원명으로 검색
     */
    @GetMapping("/insa/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<InsaMast>> searchInsaMastByName(@RequestParam String name) {
        log.info("사원명으로 검색 API 호출 - 검색어: {}", name);
        List<InsaMast> insaMasts = commonCodeService.searchInsaMastByName(name);
        return ResponseEntity.ok(insaMasts);
    }

    /**
     * 사원명 조회
     */
    @GetMapping("/insa/{sano}/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> getInsaMastName(@PathVariable Integer sano) {
        log.info("사원명 조회 API 호출 - 사번: {}", sano);
        String name = commonCodeService.getInsaMastName(sano);
        return ResponseEntity.ok(name);
    }

    // ========== 부서 코드 APIs ==========

    /**
     * 모든 부서 코드 조회
     */
    @GetMapping("/buse")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<BuseCode>> getAllBuseCodes() {
        log.info("부서 코드 목록 조회 API 호출");
        List<BuseCode> codes = commonCodeService.getAllBuseCodes();
        return ResponseEntity.ok(codes);
    }

    /**
     * 특정 부서 코드 조회
     */
    @GetMapping("/buse/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<BuseCode> getBuseCode(@PathVariable Integer code) {
        log.info("부서 코드 조회 API 호출 - 코드: {}", code);
        BuseCode buseCode = commonCodeService.getBuseCode(code);
        return ResponseEntity.ok(buseCode);
    }

    /**
     * 부서명으로 검색
     */
    @GetMapping("/buse/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<BuseCode>> searchBuseCodeByName(@RequestParam String name) {
        log.info("부서명으로 검색 API 호출 - 검색어: {}", name);
        List<BuseCode> codes = commonCodeService.searchBuseCodeByName(name);
        return ResponseEntity.ok(codes);
    }

    /**
     * 부서명 조회
     */
    @GetMapping("/buse/{code}/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> getBuseCodeName(@PathVariable Integer code) {
        log.info("부서명 조회 API 호출 - 코드: {}", code);
        String name = commonCodeService.getBuseCodeName(code);
        return ResponseEntity.ok(name);
    }
} 