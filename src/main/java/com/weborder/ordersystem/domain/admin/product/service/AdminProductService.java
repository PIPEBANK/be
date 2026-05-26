package com.weborder.ordersystem.domain.admin.product.service;

import com.weborder.ordersystem.domain.admin.product.dto.ProductCreateRequest;
import com.weborder.ordersystem.domain.admin.product.dto.ProductDetailResponse;
import com.weborder.ordersystem.domain.admin.product.dto.ProductListResponse;
import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.Customer;
import com.weborder.ordersystem.domain.erp.entity.CommonCode3;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.StockDateRepository;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.erp.repository.CommonCode3Repository;
import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import com.weborder.ordersystem.domain.web.image.service.ItemImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AdminProductService {

    private final ItemCodeRepository itemCodeRepository;
    private final StockDateRepository stockDateRepository;
    private final CustomerRepository customerRepository;
    private final CommonCode3Repository commonCode3Repository;
    private final ItemImageService itemImageService;
    private final TransactionTemplate erpTxTemplate;

    public AdminProductService(ItemCodeRepository itemCodeRepository,
                                StockDateRepository stockDateRepository,
                                CustomerRepository customerRepository,
                                CommonCode3Repository commonCode3Repository,
                                ItemImageService itemImageService,
                                @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager) {
        this.itemCodeRepository = itemCodeRepository;
        this.stockDateRepository = stockDateRepository;
        this.customerRepository = customerRepository;
        this.commonCode3Repository = commonCode3Repository;
        this.itemImageService = itemImageService;
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "itemCodeCode", "itemCodeHnam", "itemCodeSrate", "itemCodePrate", "itemCodeNum"
    );

    /**
     * 제품 목록 조회 (페이징 + 검색 + 정렬 + 재고 + 썸네일)
     */
    public Page<ProductListResponse> getProductList(String keyword, Integer useStatus, int page, int size,
                                                     String sort, String direction) {
        log.info("제품 목록 조회 - keyword: {}, useStatus: {}, sort: {} {}, page: {}", keyword, useStatus, sort, direction, page);

        String sortField = ALLOWED_SORT_FIELDS.contains(sort) ? sort : "itemCodeCode";
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));

        Page<ItemCode> itemPage = erpTxTemplate.execute(status ->
                itemCodeRepository.findAllWithFilter(useStatus, keyword, (java.util.List<String>) null, pageable));

        return itemPage.map(item -> {
            BigDecimal stockQty = getStockQuantity(item.getItemCodeCode());
            String thumbnailUrl = getThumbnailUrl(item.getItemCodeCode());
            return ProductListResponse.from(item, stockQty, thumbnailUrl);
        });
    }

    /**
     * 제품 상세 조회 (전체 정보 + 이미지 + 재고)
     */
    public ProductDetailResponse getProductDetail(Integer itemCode) {
        log.info("제품 상세 조회 - itemCode: {}", itemCode);

        ItemCode item = erpTxTemplate.execute(status ->
                itemCodeRepository.findById(itemCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + itemCode)));

        BigDecimal stockQty = getStockQuantity(itemCode);
        BigDecimal availStock = getAvailableStock(itemCode);
        List<ItemImageResponse> images = itemImageService.getImagesByItemCode(itemCode);
        String thumbnailUrl = getThumbnailUrl(itemCode);

        String pcustName = resolveCustomerName(item.getItemCodePcust());
        String scustName = resolveCustomerName(item.getItemCodeScust());

        String dcodName = resolveCodeName(item.getItemCodeDcod());
        String pcodName = resolveCodeName(item.getItemCodePcod());
        String scodName = resolveCodeName(item.getItemCodeScod());
        String ldivName = resolveCodeName(item.getItemCodeLdiv());
        String dsdivName = resolveCodeName(item.getItemCodeDsdiv());
        String lprocName = resolveCodeName(item.getItemCodeLproc());

        return ProductDetailResponse.from(item, stockQty, availStock, images, thumbnailUrl,
                pcustName, scustName, dcodName, pcodName, scodName, ldivName, dsdivName, lprocName);
    }

    /**
     * 제품 등록 (ERP DB에 신규 품목 생성)
     */
    public ProductDetailResponse createProduct(ProductCreateRequest request, String loginId) {
        log.info("제품 등록 - itemName: {}, user: {}", request.getItemName(), loginId);

        ItemCode savedItem = erpTxTemplate.execute(status -> {
            Integer maxCode = itemCodeRepository.findMaxItemCode();
            Integer newCode = maxCode + 1;
            LocalDateTime now = LocalDateTime.now();
            BigDecimal zero = BigDecimal.ZERO;

            ItemCode item = ItemCode.builder()
                    .itemCodeCode(newCode)
                    // 직접 입력
                    .itemCodeNum(str(request.getItemNum()))
                    .itemCodeHnam(str(request.getItemName()))
                    .itemCodeEnam(str(request.getItemEname()))
                    .itemCodeWord(str(request.getItemWord()))
                    .itemCodeSpec(str(request.getSpec()))
                    .itemCodeSpec2(request.getSpec2() != null ? request.getSpec2() : zero)
                    .itemCodeUnit(str(request.getUnit()))
                    // 거래처
                    .itemCodePcust(request.getPcust() != null ? request.getPcust() : 0)
                    .itemCodePcust2(request.getPcust2() != null ? request.getPcust2() : 0)
                    .itemCodeScust(request.getScust() != null ? request.getScust() : 0)
                    // 가격
                    .itemCodePrate(request.getPurchaseRate() != null ? request.getPurchaseRate() : zero)
                    .itemCodePlrate(request.getLastPurchaseRate() != null ? request.getLastPurchaseRate() : zero)
                    .itemCodePlrdate(str(request.getLastPurchaseDate()))
                    .itemCodeSrate(request.getSaleRate() != null ? request.getSaleRate() : zero)
                    .itemCodeSlrate(request.getLastSaleRate() != null ? request.getLastSaleRate() : zero)
                    .itemCodeSlrdate(str(request.getLastSaleDate()))
                    .itemCodeAvrate(request.getAvrate() != null ? request.getAvrate() : zero)
                    // 코드관리
                    .itemCodeDcod(str(request.getDcod()))
                    .itemCodePcod(str(request.getPcod()))
                    .itemCodeScod(str(request.getScod()))
                    .itemCodeLdiv(str(request.getLdiv()))
                    .itemCodeDsdiv(str(request.getDsdiv()))
                    .itemCodeLproc(str(request.getLproc()))
                    // 토글
                    .itemCodeUse(request.getUse() != null ? request.getUse() : 1)
                    .itemCodeStock(request.getStock() != null ? request.getStock() : zero)
                    .itemCodeRemark(str(request.getRemark()))
                    .itemCodeTags(str(request.getTags()))
                    // 고정값
                    .itemCodeCalc(1)
                    .itemCodeSdiv(0)
                    .itemCodeVdiv(1)
                    .itemCodeAdiv(0)
                    .itemCodeBrand("")
                    .itemCodePlace("")
                    .itemCodeNative("")
                    .itemCodeBitem(0)
                    .itemCodePitem(0)
                    .itemCodeChng(1)
                    .itemCodeAuto(0)
                    .itemCodeMarket(0)
                    .itemCodeKitchen("")
                    .itemCodePrint(0)
                    .itemCodeDclock(0)
                    .itemCodeUrate(0)
                    .itemCodeOption(0)
                    .itemCodeNstock(0)
                    .itemCodeSerial(0)
                    .itemCodeDiv1("")
                    .itemCodeDiv2("")
                    .itemCodeDiv3("")
                    .itemCodeMoq(0)
                    // 감사
                    .itemCodeFdate(now)
                    .itemCodeFuser(loginId)
                    .itemCodeLdate(now)
                    .itemCodeLuser(loginId)
                    .build();

            return itemCodeRepository.save(item);
        });

        log.info("제품 등록 완료 - code: {}, name: {}", savedItem.getItemCodeCode(), savedItem.getItemCodeHnam());
        return getProductDetail(savedItem.getItemCodeCode());
    }

    /**
     * 제품 수정 (ERP DB 품목 정보 업데이트)
     */
    public ProductDetailResponse updateProduct(Integer itemCode, ProductCreateRequest request, String loginId) {
        log.info("제품 수정 - itemCode: {}, user: {}", itemCode, loginId);

        erpTxTemplate.executeWithoutResult(status -> {
            ItemCode item = itemCodeRepository.findById(itemCode)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + itemCode));

            item.updateProductInfo(request, loginId);
            itemCodeRepository.save(item);
        });

        log.info("제품 수정 완료 - itemCode: {}", itemCode);
        return getProductDetail(itemCode);
    }

    /**
     * 제품 사용여부 변경 (활성/비활성)
     */
    public ProductDetailResponse updateProductUse(Integer itemCode, Integer use, String loginId) {
        log.info("제품 사용여부 변경 - itemCode: {}, use: {}", itemCode, use);

        erpTxTemplate.executeWithoutResult(status -> {
            ItemCode item = itemCodeRepository.findById(itemCode)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목: " + itemCode));

            item.updateUseStatus(use, loginId);
            itemCodeRepository.save(item);
        });

        return getProductDetail(itemCode);
    }

    // ========== Private Helper ==========

    private static String str(String v) {
        return v != null ? v : "";
    }

    private String resolveCustomerName(Integer custCode) {
        if (custCode == null || custCode == 0) return null;
        try {
            return erpTxTemplate.execute(status ->
                    customerRepository.findById(custCode)
                            .map(Customer::getCustCodeName)
                            .orElse(null));
        } catch (Exception e) {
            log.warn("거래처명 조회 실패 - custCode: {}", custCode);
            return null;
        }
    }

    private String resolveCodeName(String code) {
        if (code == null || code.isBlank()) return null;
        try {
            return erpTxTemplate.execute(status ->
                    commonCode3Repository.findByCommCod3Code(code)
                            .map(CommonCode3::getCommCod3Hnam)
                            .orElse(null));
        } catch (Exception e) {
            log.warn("코드명 조회 실패 - code: {}", code);
            return null;
        }
    }

    private BigDecimal getStockQuantity(Integer itemCode) {
        try {
            return erpTxTemplate.execute(status ->
                    stockDateRepository.findStockQuantityByItemCodeAndBuse7(itemCode)
                            .orElse(BigDecimal.ZERO));
        } catch (Exception e) {
            log.warn("재고 조회 실패 - itemCode: {}", itemCode);
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getAvailableStock(Integer itemCode) {
        try {
            BigDecimal stock = erpTxTemplate.execute(status ->
                    stockDateRepository.findAvailableStockQuantity(1, itemCode));
            return stock != null ? stock : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("가용재고 조회 실패 - itemCode: {}", itemCode);
            return BigDecimal.ZERO;
        }
    }

    private String getThumbnailUrl(Integer itemCode) {
        try {
            ItemImageResponse thumbnail = itemImageService.getThumbnail(itemCode);
            return thumbnail != null ? thumbnail.getImageUrl() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
