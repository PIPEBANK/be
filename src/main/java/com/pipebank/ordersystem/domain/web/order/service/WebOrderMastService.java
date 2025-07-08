package com.pipebank.ordersystem.domain.web.order.service;

import com.pipebank.ordersystem.domain.web.order.dto.WebOrderMastCreateRequest;
import com.pipebank.ordersystem.domain.web.order.dto.WebOrderMastResponse;
import com.pipebank.ordersystem.domain.web.order.entity.WebOrderMast;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WebOrderMastService {

    private final WebOrderMastRepository webOrderMastRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public WebOrderMastResponse createOrder(WebOrderMastCreateRequest request) {
        log.info("주문 생성 요청: {}", request);

        // ACNO 자동 생성
        Integer nextAcno = generateNextAcno(request.getOrderMastDate(), 
                                          request.getOrderMastSosok(), 
                                          request.getOrderMastUjcd());

        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();

        WebOrderMast webOrderMast = WebOrderMast.builder()
                .orderMastDate(request.getOrderMastDate())
                .orderMastSosok(request.getOrderMastSosok())
                .orderMastUjcd(request.getOrderMastUjcd())
                .orderMastAcno(nextAcno)
                .orderMastCust(request.getOrderMastCust())
                .orderMastScust(request.getOrderMastScust())
                .orderMastSawon(request.getOrderMastSawon())
                .orderMastSawonBuse(request.getOrderMastSawonBuse())
                .orderMastOdate(request.getOrderMastOdate())
                .orderMastProject(request.getOrderMastProject())
                .orderMastRemark(request.getOrderMastRemark())
                .orderMastFdate(now)
                .orderMastFuser(currentUser)
                .orderMastLdate(now)
                .orderMastLuser(currentUser)
                .orderMastComaddr1(request.getOrderMastComaddr1())
                .orderMastComaddr2(request.getOrderMastComaddr2())
                .orderMastComname(request.getOrderMastComname())
                .orderMastComuname(request.getOrderMastComuname())
                .orderMastComutel(request.getOrderMastComutel())
                .orderMastReason(request.getOrderMastReason())
                .orderMastTcomdiv(request.getOrderMastTcomdiv())
                .orderMastCurrency(request.getOrderMastCurrency())
                .orderMastCurrencyPer(request.getOrderMastCurrencyPer())
                .orderMastSdiv(request.getOrderMastSdiv())
                .orderMastDcust(request.getOrderMastDcust())
                .orderMastIntype(request.getOrderMastIntype())
                .orderMastOtime(request.getOrderMastOtime())
                .build();

        WebOrderMast savedOrder = webOrderMastRepository.save(webOrderMast);
        log.info("주문 생성 완료: {}", savedOrder.getOrderKey());

        return WebOrderMastResponse.from(savedOrder);
    }

    /**
     * 전체 주문 조회 (페이징)
     */
    public Page<WebOrderMastResponse> getAllOrders(Pageable pageable) {
        log.info("전체 주문 조회 요청: {}", pageable);
        
        return webOrderMastRepository.findAll(pageable)
                .map(WebOrderMastResponse::from);
    }

    /**
     * 주문일자로 조회 (페이징)
     */
    public Page<WebOrderMastResponse> getOrdersByDate(String orderDate, Pageable pageable) {
        log.info("주문일자 조회 요청: {}, {}", orderDate, pageable);
        
        return webOrderMastRepository.findByOrderMastDate(orderDate, pageable)
                .map(WebOrderMastResponse::from);
    }

    /**
     * 거래처로 조회 (페이징)
     */
    public Page<WebOrderMastResponse> getOrdersByCustomer(Integer customerId, Pageable pageable) {
        log.info("거래처 조회 요청: {}, {}", customerId, pageable);
        
        return webOrderMastRepository.findByOrderMastCust(customerId, pageable)
                .map(WebOrderMastResponse::from);
    }

    /**
     * 단건 조회
     */
    public Optional<WebOrderMastResponse> getOrder(String orderDate, Integer sosok, String ujcd, Integer acno) {
        log.info("단건 조회 요청: {}-{}-{}-{}", orderDate, sosok, ujcd, acno);
        
        return webOrderMastRepository.findByOrderMastDateAndOrderMastSosokAndOrderMastUjcdAndOrderMastAcno(
                orderDate, sosok, ujcd, acno)
                .map(WebOrderMastResponse::from);
    }

    /**
     * ACNO 자동 생성
     */
    private Integer generateNextAcno(String orderDate, Integer sosok, String ujcd) {
        Integer maxAcno = webOrderMastRepository.findMaxAcnoByDateAndSosokAndUjcd(orderDate, sosok, ujcd);
        return maxAcno + 1;
    }

    /**
     * 현재 사용자 정보 조회 (임시)
     */
    private String getCurrentUser() {
        // TODO: 실제 인증된 사용자 정보로 변경
        return "SYSTEM";
    }
} 