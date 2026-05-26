package com.weborder.ordersystem.domain.admin.stock.service;

import com.weborder.ordersystem.domain.admin.stock.dto.StockResponse;
import com.weborder.ordersystem.domain.admin.stock.dto.StockUpdateRequest;
import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.StockDate;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.StockDateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminStockService {

    private final StockDateRepository stockDateRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final TransactionTemplate erpTxTemplate;
    private final TransactionTemplate erpReadOnlyTxTemplate;

    private static final Integer DEFAULT_SOSOK = 1;
    private static final Integer DEFAULT_BUSE = 7;

    public AdminStockService(StockDateRepository stockDateRepository,
                              ItemCodeRepository itemCodeRepository,
                              @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager) {
        this.stockDateRepository = stockDateRepository;
        this.itemCodeRepository = itemCodeRepository;
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
        this.erpReadOnlyTxTemplate = new TransactionTemplate(erpTxManager);
        this.erpReadOnlyTxTemplate.setReadOnly(true);
    }

    /**
     * 전체 재고 현황 조회 (부서7 기준, 재고 0 제외)
     */
    public List<StockResponse> getStockList(String keyword) {
        log.info("재고 현황 조회 - keyword: {}", keyword);

        List<StockDate> stocks = erpReadOnlyTxTemplate.execute(status ->
                stockDateRepository.findAllStockBuse7());

        if (stocks == null || stocks.isEmpty()) {
            return List.of();
        }

        return stocks.stream()
                .map(stock -> {
                    ItemCode item = erpReadOnlyTxTemplate.execute(s ->
                            itemCodeRepository.findById(stock.getStockDateItem()).orElse(null));
                    String itemName = item != null ? item.getItemCodeHnam() : "";
                    String itemSpec = item != null ? item.getItemCodeSpec() : "";
                    String itemUnit = item != null ? item.getItemCodeUnit() : "";
                    BigDecimal availStock = safeGetAvailableStock(stock.getStockDateItem());

                    return StockResponse.from(stock, itemName, itemSpec, itemUnit, availStock);
                })
                .filter(resp -> keyword == null || keyword.isEmpty()
                        || resp.getItemName().contains(keyword)
                        || String.valueOf(resp.getItemCode()).contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 특정 품목의 재고 조회
     */
    public StockResponse getStockByItem(Integer itemCode) {
        log.info("품목별 재고 조회 - itemCode: {}", itemCode);

        StockDate stock = erpReadOnlyTxTemplate.execute(status ->
                stockDateRepository.findByStockDateItemAndStockDateBuse(itemCode, DEFAULT_BUSE)
                        .orElse(null));

        ItemCode item = erpReadOnlyTxTemplate.execute(status ->
                itemCodeRepository.findById(itemCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + itemCode)));

        BigDecimal availStock = safeGetAvailableStock(itemCode);

        if (stock == null) {
            return StockResponse.builder()
                    .itemCode(itemCode)
                    .itemName(item.getItemCodeHnam())
                    .itemSpec(item.getItemCodeSpec())
                    .itemUnit(item.getItemCodeUnit())
                    .cnt(BigDecimal.ZERO)
                    .availableStock(availStock)
                    .sosok(DEFAULT_SOSOK)
                    .buse(DEFAULT_BUSE)
                    .build();
        }

        return StockResponse.from(stock, item.getItemCodeHnam(), item.getItemCodeSpec(),
                item.getItemCodeUnit(), availStock);
    }

    /**
     * 재고 수정 (직접 설정 또는 증감)
     */
    public StockResponse updateStock(Integer itemCode, StockUpdateRequest request, String loginId) {
        log.info("재고 수정 - itemCode: {}, cnt: {}, adjustment: {}", itemCode, request.getCnt(), request.getAdjustment());

        erpTxTemplate.executeWithoutResult(status -> {
            StockDate stock = stockDateRepository
                    .findByStockDateItemAndStockDateBuse(itemCode, DEFAULT_BUSE)
                    .orElse(null);

            if (stock == null) {
                String dcod = request.getDcod() != null ? request.getDcod() : "";
                Integer buse = request.getBuse() != null ? request.getBuse() : DEFAULT_BUSE;
                LocalDateTime now = LocalDateTime.now();

                BigDecimal cnt = request.getCnt() != null ? request.getCnt()
                        : (request.getAdjustment() != null ? request.getAdjustment() : BigDecimal.ZERO);

                StockDate newStock = StockDate.builder()
                        .stockDateDcod(dcod)
                        .stockDateItem(itemCode)
                        .stockDateSosok(DEFAULT_SOSOK)
                        .stockDateBuse(buse)
                        .stockDateCnt(cnt)
                        .stockDateFdate(now)
                        .stockDateFuser(loginId)
                        .stockDateLdate(now)
                        .stockDateLuser(loginId)
                        .build();

                stockDateRepository.save(newStock);
                log.info("재고 신규 등록 - itemCode: {}, cnt: {}", itemCode, cnt);
            } else {
                if (request.getCnt() != null) {
                    stock.updateStock(request.getCnt(), loginId);
                    log.info("재고 직접 설정 - itemCode: {}, cnt: {}", itemCode, request.getCnt());
                } else if (request.getAdjustment() != null) {
                    stock.adjustStock(request.getAdjustment(), loginId);
                    log.info("재고 증감 - itemCode: {}, adjustment: {}, newCnt: {}",
                            itemCode, request.getAdjustment(), stock.getStockDateCnt());
                }
                stockDateRepository.save(stock);
            }
        });

        return getStockByItem(itemCode);
    }

    // ========== Private Helpers ==========

    /**
     * 가용재고 안전 조회 (별도 트랜잭션으로 분리하여 실패해도 메인 로직에 영향 없음)
     */
    private BigDecimal safeGetAvailableStock(Integer itemCode) {
        try {
            return erpReadOnlyTxTemplate.execute(status -> {
                BigDecimal expectStock = stockDateRepository.findAvailableStockQuantity(DEFAULT_SOSOK, itemCode);
                BigDecimal currentStock = stockDateRepository
                        .findStockQuantityByItemCodeAndBuse7(itemCode)
                        .orElse(BigDecimal.ZERO);
                return currentStock.add(expectStock != null ? expectStock : BigDecimal.ZERO);
            });
        } catch (Exception e) {
            log.warn("가용재고 계산 실패 - itemCode: {}", itemCode, e);
            return BigDecimal.ZERO;
        }
    }
}
