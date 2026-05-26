package com.weborder.ordersystem.domain.web.product.service;

import com.weborder.ordersystem.domain.erp.entity.Customer;
import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemSrateRepository;
import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import com.weborder.ordersystem.domain.web.image.service.ItemImageService;
import com.weborder.ordersystem.domain.web.product.dto.UserProductListResponse;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserProductService {

    private final ItemCodeRepository itemCodeRepository;
    private final ItemSrateRepository itemSrateRepository;
    private final CustomerRepository customerRepository;
    private final ItemImageService itemImageService;
    private final TransactionTemplate erpTxTemplate;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "itemCodeCode", "itemCodeHnam", "itemCodeSrate"
    );

    public UserProductService(ItemCodeRepository itemCodeRepository,
                               ItemSrateRepository itemSrateRepository,
                               CustomerRepository customerRepository,
                               ItemImageService itemImageService,
                               @Qualifier("erpTransactionManager") PlatformTransactionManager erpTxManager) {
        this.itemCodeRepository = itemCodeRepository;
        this.itemSrateRepository = itemSrateRepository;
        this.customerRepository = customerRepository;
        this.itemImageService = itemImageService;
        this.erpTxTemplate = new TransactionTemplate(erpTxManager);
    }

    public Page<UserProductListResponse> getProductList(String keyword, List<String> scodList, int page, int size,
                                                         String sort, String direction, Integer custCode) {
        log.info("사용자 상품 목록 조회 - keyword: {}, scod: {}, sort: {} {}, page: {}, custCode: {}",
                keyword, scodList, sort, direction, page, custCode);

        String sortField = ALLOWED_SORT_FIELDS.contains(sort) ? sort : "itemCodeHnam";
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));

        boolean hidePrice = false;
        Map<Integer, BigDecimal> custRateMap = Map.of();

        if (custCode != null && custCode > 0) {
            final Integer cc = custCode;
            hidePrice = Boolean.TRUE.equals(erpTxTemplate.execute(status -> {
                Customer cust = customerRepository.findById(cc).orElse(null);
                return cust != null && cust.isHidePrice();
            }));

            final boolean hp = hidePrice;
            if (!hp) {
                custRateMap = erpTxTemplate.execute(status ->
                        itemSrateRepository.findByIdItemSrateCust(cc).stream()
                                .collect(Collectors.toMap(ItemSrate::getItemCode, ItemSrate::getItemSrateRate))
                );
                if (custRateMap == null) custRateMap = Map.of();
            }
        }

        final boolean finalHidePrice = hidePrice;
        final Map<Integer, BigDecimal> finalCustRateMap = custRateMap;

        Page<ItemCode> itemPage = erpTxTemplate.execute(status ->
                itemCodeRepository.findAllWithFilter(1, keyword, scodList, pageable));

        return itemPage.map(item -> {
            String thumbnailUrl = getThumbnailUrl(item.getItemCodeCode());
            BigDecimal custRate = finalCustRateMap.get(item.getItemCodeCode());
            return UserProductListResponse.from(item, thumbnailUrl, custRate, finalHidePrice);
        });
    }

    public Page<UserProductListResponse> getProductList(String keyword, List<String> scodList, int page, int size,
                                                         String sort, String direction) {
        return getProductList(keyword, scodList, page, size, sort, direction, null);
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
