package com.pipebank.ordersystem.domain.erp.service;

import com.pipebank.ordersystem.domain.erp.dto.CommonCode1Response;
import com.pipebank.ordersystem.domain.erp.dto.CommonCode2Response;
import com.pipebank.ordersystem.domain.erp.dto.CommonCode3Response;
import com.pipebank.ordersystem.domain.erp.entity.CommonCode1;
import com.pipebank.ordersystem.domain.erp.entity.CommonCode2;
import com.pipebank.ordersystem.domain.erp.entity.CommonCode3;
import com.pipebank.ordersystem.domain.erp.repository.CommonCode1Repository;
import com.pipebank.ordersystem.domain.erp.repository.CommonCode2Repository;
import com.pipebank.ordersystem.domain.erp.repository.CommonCode3Repository;
import com.pipebank.ordersystem.domain.erp.repository.SosokCodeRepository;
import com.pipebank.ordersystem.domain.erp.entity.SosokCode;
import com.pipebank.ordersystem.domain.erp.repository.InsaMastRepository;
import com.pipebank.ordersystem.domain.erp.entity.InsaMast;
import com.pipebank.ordersystem.domain.erp.repository.BuseCodeRepository;
import com.pipebank.ordersystem.domain.erp.entity.BuseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommonCodeService {

    private final CommonCode1Repository commonCode1Repository;
    private final CommonCode2Repository commonCode2Repository;
    private final CommonCode3Repository commonCode3Repository;
    private final SosokCodeRepository sosokCodeRepository;
    private final InsaMastRepository insaMastRepository;
    private final BuseCodeRepository buseCodeRepository;

    // ========== Level1 (대분류) 서비스 메서드들 ==========

    public List<CommonCode1Response> getAllLevel1Codes() {
        log.debug("모든 대분류 코드 조회");
        List<CommonCode1> codes = commonCode1Repository.findAll();
        return codes.stream()
                .map(CommonCode1Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode1Response> getActiveLevel1Codes() {
        log.debug("활성화된 대분류 코드 조회");
        List<CommonCode1> codes = commonCode1Repository.findActiveCodesOrderBySort();
        return codes.stream()
                .map(CommonCode1Response::from)
                .collect(Collectors.toList());
    }

    public CommonCode1Response getLevel1Code(String code) {
        log.debug("대분류 코드 조회: {}", code);
        CommonCode1 commonCode = commonCode1Repository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다: " + code));
        return CommonCode1Response.from(commonCode);
    }

    // ========== Level2 (중분류) 서비스 메서드들 ==========

    public List<CommonCode2Response> getAllLevel2Codes() {
        log.debug("모든 중분류 코드 조회");
        List<CommonCode2> codes = commonCode2Repository.findAllByOrderByCommCod2Cod1AscCommCod2SortAsc();
        return codes.stream()
                .map(CommonCode2Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode2Response> getActiveLevel2Codes() {
        log.debug("활성화된 중분류 코드 조회");
        List<CommonCode2> codes = commonCode2Repository.findActiveCommonCodes();
        return codes.stream()
                .map(CommonCode2Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode2Response> getLevel2CodesByParent(String cod1) {
        log.debug("특정 대분류의 중분류 코드 조회: {}", cod1);
        List<CommonCode2> codes = commonCode2Repository.findByCommCod2Cod1OrderByCommCod2SortAsc(cod1);
        return codes.stream()
                .map(CommonCode2Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode2Response> getActiveLevel2CodesByParent(String cod1) {
        log.debug("특정 대분류의 활성화된 중분류 코드 조회: {}", cod1);
        List<CommonCode2> codes = commonCode2Repository.findActiveByCommCod2Cod1(cod1);
        return codes.stream()
                .map(CommonCode2Response::from)
                .collect(Collectors.toList());
    }

    public CommonCode2Response getLevel2Code(String cod1, String cod2) {
        log.debug("중분류 코드 조회: {}-{}", cod1, cod2);
        CommonCode2 commonCode = commonCode2Repository.findByCommCod2Cod1AndCommCod2Cod2(cod1, cod2)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다: " + cod1 + "-" + cod2));
        return CommonCode2Response.from(commonCode);
    }

    // ========== Level3 (소분류) 서비스 메서드들 ==========

    public List<CommonCode3Response> getAllLevel3Codes() {
        log.debug("모든 소분류 코드 조회");
        List<CommonCode3> codes = commonCode3Repository.findAllByOrderByCommCod3Cod1AscCommCod3Cod2AscCommCod3Cod3Asc();
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode3Response> getActiveLevel3Codes() {
        log.debug("활성화된 소분류 코드 조회");
        List<CommonCode3> codes = commonCode3Repository.findActiveCommonCodes();
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode3Response> getVisibleLevel3Codes() {
        log.debug("보기 가능한 소분류 코드 조회");
        List<CommonCode3> codes = commonCode3Repository.findVisibleCommonCodes();
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode3Response> getLevel3CodesByParent(String cod1) {
        log.debug("특정 대분류의 소분류 코드 조회: {}", cod1);
        List<CommonCode3> codes = commonCode3Repository.findByCommCod3Cod1OrderByCommCod3Cod2AscCommCod3Cod3Asc(cod1);
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode3Response> getLevel3CodesByParent(String cod1, String cod2) {
        log.debug("특정 중분류의 소분류 코드 조회: {}-{}", cod1, cod2);
        List<CommonCode3> codes = commonCode3Repository.findByCommCod3Cod1AndCommCod3Cod2OrderByCommCod3Cod3Asc(cod1, cod2);
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode3Response> getActiveLevel3CodesByParent(String cod1, String cod2) {
        log.debug("특정 중분류의 활성화된 소분류 코드 조회: {}-{}", cod1, cod2);
        List<CommonCode3> codes = commonCode3Repository.findActiveByCommCod3Cod1AndCommCod3Cod2(cod1, cod2);
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public CommonCode3Response getLevel3Code(String cod1, String cod2, String cod3) {
        log.debug("소분류 코드 조회: {}-{}-{}", cod1, cod2, cod3);
        CommonCode3 commonCode = commonCode3Repository.findByCommCod3Cod1AndCommCod3Cod2AndCommCod3Cod3(cod1, cod2, cod3)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다: " + cod1 + "-" + cod2 + "-" + cod3));
        return CommonCode3Response.from(commonCode);
    }

    // ========== 검색 메서드들 ==========

    public List<CommonCode3Response> searchLevel3ByHangul(String name) {
        log.debug("한글명으로 소분류 검색: {}", name);
        List<CommonCode3> codes = commonCode3Repository.findByHangulNameContaining(name);
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    public List<CommonCode3Response> searchLevel3ByEnglish(String name) {
        log.debug("영어명으로 소분류 검색: {}", name);
        List<CommonCode3> codes = commonCode3Repository.findByEnglishNameContaining(name);
        return codes.stream()
                .map(CommonCode3Response::from)
                .collect(Collectors.toList());
    }

    // ========== 소속 코드 서비스 메서드들 ==========

    public List<SosokCode> getAllSosokCodes() {
        log.debug("모든 소속 코드 조회");
        return sosokCodeRepository.findAllOrderByCode();
    }

    public SosokCode getSosokCode(Integer code) {
        log.debug("소속 코드 조회: {}", code);
        return sosokCodeRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소속 코드입니다: " + code));
    }

    public String getSosokCodeName(Integer code) {
        log.debug("소속 코드명 조회: {}", code);
        return sosokCodeRepository.findById(code)
                .map(SosokCode::getSosokCodeName)
                .orElse("");
    }

    public List<SosokCode> searchSosokCodeByName(String name) {
        log.debug("소속 코드명으로 검색: {}", name);
        return sosokCodeRepository.findByNameContainingOrderByCode(name);
    }

    // ========== 사원 정보 서비스 메서드들 ==========

    public List<InsaMast> getAllInsaMasts() {
        log.debug("모든 사원 정보 조회");
        return insaMastRepository.findAllOrderBySano();
    }

    public InsaMast getInsaMast(Integer sano) {
        log.debug("사원 정보 조회: {}", sano);
        return insaMastRepository.findById(sano)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다: " + sano));
    }

    public String getInsaMastName(Integer sano) {
        log.debug("사원명 조회: {}", sano);
        return insaMastRepository.findById(sano)
                .map(InsaMast::getInsaMastKnam)
                .orElse("");
    }

    public List<InsaMast> searchInsaMastByName(String name) {
        log.debug("사원명으로 검색: {}", name);
        return insaMastRepository.findByNameContainingOrderBySano(name);
    }

    // ========== 부서 코드 서비스 메서드들 ==========

    public List<BuseCode> getAllBuseCodes() {
        log.debug("모든 부서 코드 조회");
        return buseCodeRepository.findAllOrderByCode();
    }

    public BuseCode getBuseCode(Integer code) {
        log.debug("부서 코드 조회: {}", code);
        return buseCodeRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서 코드입니다: " + code));
    }

    public String getBuseCodeName(Integer code) {
        log.debug("부서 코드명 조회: {}", code);
        return buseCodeRepository.findById(code)
                .map(BuseCode::getBuseCodeName)
                .orElse("");
    }

    public List<BuseCode> searchBuseCodeByName(String name) {
        log.debug("부서 코드명으로 검색: {}", name);
        return buseCodeRepository.findByNameContainingOrderByCode(name);
    }

    // ========== 유틸리티 메서드들 ==========

    /**
     * 코드값으로 표시명 조회 (Customer의 DCOD 변환용)
     */
    public String getDisplayNameByCode(String fullCode) {
   
        // fullCode 길이에 따라 적절한 테이블에서 검색
        if (fullCode.length() == 3) {
            // Level1 검색
            return commonCode1Repository.findById(fullCode)
                    .map(CommonCode1::getDisplayName)
                    .orElse("알 수 없는 코드");
        } else if (fullCode.length() == 6) {
            // Level2 검색
            return commonCode2Repository.findByCommCod2Code(fullCode)
                    .map(CommonCode2::getDisplayName)
                    .orElse("알 수 없는 코드");
        } else if (fullCode.length() == 10) {
            // Level3 검색 - 한글명 사용
            return commonCode3Repository.findByCommCod3Code(fullCode)
                    .map(CommonCode3::getHangulName)
                    .orElse("알 수 없는 코드");
        }
        
        return "잘못된 코드 형식";
    }
} 