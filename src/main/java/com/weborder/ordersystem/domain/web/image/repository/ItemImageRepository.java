package com.weborder.ordersystem.domain.web.image.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.web.image.entity.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

    List<ItemImage> findByItemCodeOrderBySortOrderAsc(Integer itemCode);

    Optional<ItemImage> findByItemCodeAndIsThumbnailTrue(Integer itemCode);

    long countByItemCode(Integer itemCode);

    void deleteByItemCode(Integer itemCode);
}
