package com.weborder.ordersystem.domain.web.cart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weborder.ordersystem.domain.erp.entity.Customer;
import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemCodeRepository;
import com.weborder.ordersystem.domain.erp.repository.ItemSrateRepository;
import com.weborder.ordersystem.domain.web.cart.dto.CartItemRequest;
import com.weborder.ordersystem.domain.web.cart.dto.CartItemResponse;
import com.weborder.ordersystem.domain.web.cart.entity.CartItem;
import com.weborder.ordersystem.domain.web.cart.repository.CartItemRepository;
import com.weborder.ordersystem.domain.web.customitem.entity.CustomItem;
import com.weborder.ordersystem.domain.web.customitem.entity.CustomItemImage;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemImageRepository;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemRepository;
import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import com.weborder.ordersystem.domain.web.image.service.ItemImageService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "webTransactionManager")
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ItemCodeRepository itemCodeRepository;
    private final ItemSrateRepository itemSrateRepository;
    private final CustomerRepository customerRepository;
    private final ItemImageService itemImageService;
    private final CustomItemRepository customItemRepository;
    private final CustomItemImageRepository customItemImageRepository;

    private static final String CUSTOM_IMG_BASE = "/api/custom-items/images/files/";

    public CartItemResponse addToCart(String memberId, CartItemRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        if (request.getCustomItemId() != null) {
            return addCustomItemToCart(member, request);
        }

        if (request.getItemCode() == null) {
            throw new IllegalArgumentException("itemCode 또는 customItemId가 필요합니다");
        }

        log.info("장바구니 담기 - memberId: {}, itemCode: {}, qty: {}",
                memberId, request.getItemCode(), request.getQuantity());

        CartItem cartItem = cartItemRepository
                .findByMemberIdAndItemCode(member.getId(), request.getItemCode())
                .map(existing -> {
                    existing.addQuantity(request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .memberId(member.getId())
                        .itemCode(request.getItemCode())
                        .quantity(request.getQuantity())
                        .build());

        CartItem saved = cartItemRepository.save(cartItem);
        return enrichWithItemInfo(saved);
    }

    private CartItemResponse addCustomItemToCart(Member member, CartItemRequest request) {
        log.info("커스텀 아이템 장바구니 담기 - memberId: {}, customItemId: {}, qty: {}",
                member.getMemberId(), request.getCustomItemId(), request.getQuantity());

        CartItem cartItem = cartItemRepository
                .findByMemberIdAndCustomItemId(member.getId(), request.getCustomItemId())
                .map(existing -> {
                    existing.addQuantity(request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .memberId(member.getId())
                        .customItemId(request.getCustomItemId())
                        .quantity(request.getQuantity())
                        .build());

        CartItem saved = cartItemRepository.save(cartItem);
        return enrichCustomItemInfo(saved);
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<CartItemResponse> getCartItems(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        Integer custCode = parseCustCode(member.getCustCode());
        Map<Integer, BigDecimal> custRateMap = Map.of();
        boolean hidePrice = false;

        if (custCode != null && custCode > 0) {
            try {
                Customer cust = customerRepository.findById(custCode).orElse(null);
                hidePrice = cust != null && cust.isHidePrice();
                if (!hidePrice) {
                    custRateMap = itemSrateRepository.findByIdItemSrateCust(custCode).stream()
                            .collect(Collectors.toMap(ItemSrate::getItemCode, ItemSrate::getItemSrateRate));
                }
            } catch (Exception e) {
                log.warn("거래처별 단가 조회 실패 - custCode: {}", custCode, e);
            }
        }

        List<CartItem> cartItems = cartItemRepository.findByMemberIdOrderByCreatedAtDesc(member.getId());

        final Map<Integer, BigDecimal> finalCustRateMap = custRateMap;
        final boolean finalHidePrice = hidePrice;
        return cartItems.stream()
                .map(ci -> {
                    if (ci.isCustomItem()) {
                        return enrichCustomItemInfo(ci);
                    }
                    return enrichWithItemInfoAndCustRate(ci, finalCustRateMap, finalHidePrice);
                })
                .collect(Collectors.toList());
    }

    public CartItemResponse updateQuantity(String memberId, Long cartItemId, Integer quantity) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 항목입니다: " + cartItemId));

        if (!cartItem.getMemberId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 장바구니만 수정할 수 있습니다");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }

        cartItem.updateQuantity(quantity);
        CartItem saved = cartItemRepository.save(cartItem);

        if (saved.isCustomItem()) {
            return enrichCustomItemInfo(saved);
        }
        return enrichWithItemInfo(saved);
    }

    public void removeFromCart(String memberId, Long cartItemId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 항목입니다: " + cartItemId));

        if (!cartItem.getMemberId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 장바구니만 삭제할 수 있습니다");
        }

        cartItemRepository.delete(cartItem);
    }

    public void clearCart(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        cartItemRepository.deleteByMemberId(member.getId());
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public long getCartItemCount(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        return cartItemRepository.countByMemberId(member.getId());
    }

    private CartItemResponse enrichWithItemInfo(CartItem cartItem) {
        return enrichWithItemInfoAndCustRate(cartItem, Map.of(), false);
    }

    private CartItemResponse enrichWithItemInfoAndCustRate(CartItem cartItem,
                                                           Map<Integer, BigDecimal> custRateMap,
                                                           boolean hidePrice) {
        try {
            return itemCodeRepository.findById(cartItem.getItemCode())
                    .map(item -> {
                        String thumbnailUrl = null;
                        try {
                            ItemImageResponse thumbnail = itemImageService.getThumbnail(cartItem.getItemCode());
                            if (thumbnail != null) thumbnailUrl = thumbnail.getImageUrl();
                        } catch (Exception ignored) {}

                        BigDecimal unitPrice = item.getItemCodeSrate();
                        BigDecimal custRate = custRateMap.get(item.getItemCodeCode());
                        if (custRate != null && custRate.compareTo(BigDecimal.ZERO) > 0) {
                            unitPrice = custRate;
                        }

                        return CartItemResponse.from(
                                cartItem,
                                item.getItemCodeHnam(),
                                item.getItemCodeSpec(),
                                item.getItemCodeUnit(),
                                item.getItemCodeNum(),
                                unitPrice,
                                item.getItemCodeVdiv(),
                                thumbnailUrl,
                                hidePrice
                        );
                    })
                    .orElse(CartItemResponse.from(cartItem));
        } catch (Exception e) {
            log.warn("ERP 품목정보 조회 실패 - itemCode: {}", cartItem.getItemCode(), e);
            return CartItemResponse.from(cartItem);
        }
    }

    private CartItemResponse enrichCustomItemInfo(CartItem cartItem) {
        try {
            CustomItem customItem = customItemRepository.findById(cartItem.getCustomItemId())
                    .orElse(null);
            if (customItem == null) {
                return CartItemResponse.from(cartItem);
            }

            String thumbnailUrl = customItemImageRepository
                    .findByCustomItemIdAndIsThumbnailTrue(customItem.getCustomItemCode())
                    .map(img -> CUSTOM_IMG_BASE + img.getId())
                    .orElseGet(() -> {
                        List<CustomItemImage> imgs = customItemImageRepository
                                .findByCustomItemIdOrderBySortOrderAsc(customItem.getCustomItemCode());
                        return imgs.isEmpty() ? null : CUSTOM_IMG_BASE + imgs.get(0).getId();
                    });

            return CartItemResponse.fromCustomItem(
                    cartItem,
                    customItem.getCustomItemHnam(),
                    customItem.getCustomItemDesc(),
                    thumbnailUrl
            );
        } catch (Exception e) {
            log.warn("커스텀 아이템 정보 조회 실패 - customItemId: {}", cartItem.getCustomItemId(), e);
            return CartItemResponse.from(cartItem);
        }
    }

    private Integer parseCustCode(String custCodeStr) {
        if (custCodeStr == null || custCodeStr.isBlank()) return null;
        try { return Integer.parseInt(custCodeStr.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
