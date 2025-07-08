package com.pipebank.ordersystem.domain.web.order.service;

import com.pipebank.ordersystem.domain.web.order.dto.WebOrderTranCreateRequest;
import com.pipebank.ordersystem.domain.web.order.dto.WebOrderTranResponse;
import com.pipebank.ordersystem.domain.web.order.entity.WebOrderTran;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WebOrderTranService {

    private final WebOrderTranRepository webOrderTranRepository;

    /**
     * 주문 상세 생성
     */
    @Transactional
    public WebOrderTranResponse createOrderTran(WebOrderTranCreateRequest request) {
        log.info("주문 상세 생성 요청: {}", request);

        // SEQ 자동 생성
        Integer nextSeq = generateNextSeq(request.getOrderTranDate(), 
                                        request.getOrderTranSosok(), 
                                        request.getOrderTranUjcd(),
                                        request.getOrderTranAcno());

        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();

        WebOrderTran webOrderTran = WebOrderTran.builder()
                .orderTranDate(request.getOrderTranDate())
                .orderTranSosok(request.getOrderTranSosok())
                .orderTranUjcd(request.getOrderTranUjcd())
                .orderTranAcno(request.getOrderTranAcno())
                .orderTranSeq(nextSeq)
                .orderTranItemVer(request.getOrderTranItemVer())
                .orderTranItem(request.getOrderTranItem())
                .orderTranDeta(request.getOrderTranDeta())
                .orderTranSpec(request.getOrderTranSpec())
                .orderTranUnit(request.getOrderTranUnit())
                .orderTranCalc(request.getOrderTranCalc())
                .orderTranVdiv(request.getOrderTranVdiv())
                .orderTranAdiv(request.getOrderTranAdiv())
                .orderTranRate(request.getOrderTranRate())
                .orderTranCnt(request.getOrderTranCnt())
                .orderTranConvertWeight(request.getOrderTranConvertWeight())
                .orderTranDcPer(request.getOrderTranDcPer())
                .orderTranDcAmt(request.getOrderTranDcAmt())
                .orderTranForiAmt(request.getOrderTranForiAmt())
                .orderTranAmt(request.getOrderTranAmt())
                .orderTranNet(request.getOrderTranNet())
                .orderTranVat(request.getOrderTranVat())
                .orderTranAdv(request.getOrderTranAdv())
                .orderTranTot(request.getOrderTranTot())
                .orderTranLrate(request.getOrderTranLrate())
                .orderTranPrice(request.getOrderTranPrice())
                .orderTranPrice2(request.getOrderTranPrice2())
                .orderTranLdiv(request.getOrderTranLdiv())
                .orderTranRemark(request.getOrderTranRemark())
                .orderTranStau(request.getOrderTranStau())
                .orderTranFdate(now)
                .orderTranFuser(currentUser)
                .orderTranLdate(now)
                .orderTranLuser(currentUser)
                .orderTranWamt(request.getOrderTranWamt())
                .build();

        WebOrderTran savedOrderTran = webOrderTranRepository.save(webOrderTran);
        log.info("주문 상세 생성 완료: {}", savedOrderTran.getOrderTranKey());

        return WebOrderTranResponse.from(savedOrderTran);
    }

    /**
     * 전체 주문 상세 조회 (페이징)
     */
    public Page<WebOrderTranResponse> getAllOrderTrans(Pageable pageable) {
        log.info("전체 주문 상세 조회 요청: {}", pageable);
        
        return webOrderTranRepository.findAll(pageable)
                .map(WebOrderTranResponse::from);
    }

    /**
     * 특정 주문의 모든 상세 조회
     */
    public List<WebOrderTranResponse> getOrderTransByOrder(String orderDate, Integer sosok, String ujcd, Integer acno) {
        log.info("특정 주문 상세 조회 요청: {}-{}-{}-{}", orderDate, sosok, ujcd, acno);
        
        return webOrderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeq(
                orderDate, sosok, ujcd, acno)
                .stream()
                .map(WebOrderTranResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 주문의 모든 상세 조회 (페이징)
     */
    public Page<WebOrderTranResponse> getOrderTransByOrder(String orderDate, Integer sosok, String ujcd, Integer acno, Pageable pageable) {
        log.info("특정 주문 상세 조회 요청 (페이징): {}-{}-{}-{}, {}", orderDate, sosok, ujcd, acno, pageable);
        
        return webOrderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                orderDate, sosok, ujcd, acno, pageable)
                .map(WebOrderTranResponse::from);
    }

    /**
     * 품목코드로 조회 (페이징)
     */
    public Page<WebOrderTranResponse> getOrderTransByItem(Integer itemCode, Pageable pageable) {
        log.info("품목코드 조회 요청: {}, {}", itemCode, pageable);
        
        return webOrderTranRepository.findByOrderTranItem(itemCode, pageable)
                .map(WebOrderTranResponse::from);
    }

    /**
     * 상태코드로 조회 (페이징)
     */
    public Page<WebOrderTranResponse> getOrderTransByStatus(String statusCode, Pageable pageable) {
        log.info("상태코드 조회 요청: {}, {}", statusCode, pageable);
        
        return webOrderTranRepository.findByOrderTranStau(statusCode, pageable)
                .map(WebOrderTranResponse::from);
    }

    /**
     * 단건 조회
     */
    public Optional<WebOrderTranResponse> getOrderTran(String orderDate, Integer sosok, String ujcd, Integer acno, Integer seq) {
        log.info("단건 조회 요청: {}-{}-{}-{}-{}", orderDate, sosok, ujcd, acno, seq);
        
        return webOrderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoAndOrderTranSeq(
                orderDate, sosok, ujcd, acno, seq)
                .map(WebOrderTranResponse::from);
    }

    /**
     * SEQ 자동 생성
     */
    private Integer generateNextSeq(String orderDate, Integer sosok, String ujcd, Integer acno) {
        Integer maxSeq = webOrderTranRepository.findMaxSeqByDateAndSosokAndUjcdAndAcno(orderDate, sosok, ujcd, acno);
        return maxSeq + 1;
    }

    /**
     * 현재 사용자 정보 조회 (임시)
     */
    private String getCurrentUser() {
        // TODO: 실제 인증된 사용자 정보로 변경
        return "SYSTEM";
    }
} 