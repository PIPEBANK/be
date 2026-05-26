package com.weborder.ordersystem.domain.web.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.web.cart.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<CartItem> findByMemberIdAndItemCode(Long memberId, Integer itemCode);

    Optional<CartItem> findByMemberIdAndCustomItemId(Long memberId, Long customItemId);

    void deleteByMemberIdAndItemCode(Long memberId, Integer itemCode);

    void deleteByMemberId(Long memberId);

    long countByMemberId(Long memberId);
}
