package com.weborder.ordersystem.domain.admin.srate.service;

import com.weborder.ordersystem.domain.admin.srate.dto.ItemSrateRequest;
import com.weborder.ordersystem.domain.admin.srate.dto.ItemSrateResponse;
import com.weborder.ordersystem.domain.erp.entity.Customer;
import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemSrateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminItemSrateService {

    private final ItemSrateRepository itemSrateRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true, transactionManager = "erpTransactionManager")
    public List<ItemSrateResponse> getCustomerRates(Integer custCode) {
        List<ItemSrate> srates = itemSrateRepository.findByIdItemSrateCust(custCode);

        return srates.stream()
                .map(srate -> {
                    String itemName = itemCodeRepository.findById(srate.getItemCode())
                            .map(ItemCode::getItemCodeHnam).orElse("-");
                    BigDecimal defaultRate = itemCodeRepository.findById(srate.getItemCode())
                            .map(ItemCode::getItemCodeSrate).orElse(BigDecimal.ZERO);
                    return ItemSrateResponse.from(srate, itemName, defaultRate);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, transactionManager = "erpTransactionManager")
    public List<ItemSrateResponse> getAllItemsWithCustomerRate(Integer custCode) {
        List<ItemCode> allItems = itemCodeRepository.findAll();
        Map<Integer, ItemSrate> srateMap = itemSrateRepository.findByIdItemSrateCust(custCode)
                .stream()
                .collect(Collectors.toMap(ItemSrate::getItemCode, s -> s));

        return allItems.stream()
                .filter(item -> item.getItemCodeUse() != null && item.getItemCodeUse() == 1)
                .map(item -> {
                    ItemSrate srate = srateMap.get(item.getItemCodeCode());
                    BigDecimal customerRate = srate != null ? srate.getItemSrateRate() : null;
                    String remark = srate != null ? srate.getItemSrateRemark() : null;
                    return ItemSrateResponse.builder()
                            .itemCode(item.getItemCodeCode())
                            .custCode(custCode)
                            .itemName(item.getItemCodeHnam())
                            .defaultRate(item.getItemCodeSrate())
                            .customerRate(customerRate)
                            .remark(remark)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(transactionManager = "erpTransactionManager")
    public ItemSrateResponse saveOrUpdate(ItemSrateRequest request, String adminUser) {
        ItemSrate.ItemSrateId id = new ItemSrate.ItemSrateId(request.getItemCode(), request.getCustCode());
        LocalDateTime now = LocalDateTime.now();

        ItemSrate srate = itemSrateRepository.findById(id).orElse(null);
        if (srate != null) {
            srate.updateRate(request.getRate(), request.getRemark(), adminUser);
        } else {
            srate = ItemSrate.builder()
                    .itemSrateItem(request.getItemCode())
                    .itemSrateCust(request.getCustCode())
                    .itemSrateRate(request.getRate())
                    .itemSratePrate(BigDecimal.ZERO)
                    .itemSratePrice(BigDecimal.ZERO)
                    .itemSratePprice(BigDecimal.ZERO)
                    .itemSrateDate("")
                    .itemSrateCnt(BigDecimal.ZERO)
                    .itemSratePcnt(BigDecimal.ZERO)
                    .itemSrateRemark(request.getRemark() != null ? request.getRemark() : "")
                    .itemSrateFdate(now)
                    .itemSrateFuser(adminUser)
                    .itemSrateLdate(now)
                    .itemSrateLuser(adminUser)
                    .build();
        }
        itemSrateRepository.save(srate);
        log.info("거래처별 판매단가 저장 - item: {}, cust: {}, rate: {}", request.getItemCode(), request.getCustCode(), request.getRate());

        String itemName = itemCodeRepository.findById(request.getItemCode())
                .map(ItemCode::getItemCodeHnam).orElse("-");
        BigDecimal defaultRate = itemCodeRepository.findById(request.getItemCode())
                .map(ItemCode::getItemCodeSrate).orElse(BigDecimal.ZERO);
        return ItemSrateResponse.from(srate, itemName, defaultRate);
    }

    @Transactional(transactionManager = "erpTransactionManager")
    public void delete(Integer itemCode, Integer custCode) {
        ItemSrate.ItemSrateId id = new ItemSrate.ItemSrateId(itemCode, custCode);
        itemSrateRepository.deleteById(id);
        log.info("거래처별 판매단가 삭제 - item: {}, cust: {}", itemCode, custCode);
    }

    @Transactional(readOnly = true, transactionManager = "erpTransactionManager")
    public boolean isHidePrice(Integer custCode) {
        Customer customer = customerRepository.findById(custCode).orElse(null);
        return customer != null && customer.isHidePrice();
    }

    @Transactional(transactionManager = "erpTransactionManager")
    public void updateHidePrice(Integer custCode, boolean hidePrice) {
        Customer customer = customerRepository.findById(custCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처입니다: " + custCode));
        customer.setHidePrice(hidePrice);
        customerRepository.save(customer);
        log.info("거래처 단가숨김 설정 - cust: {}, hidePrice: {}", custCode, hidePrice);
    }
}
