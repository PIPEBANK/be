package com.weborder.ordersystem.domain.web.customitem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.web.customitem.entity.CustomItemImage;

@Repository
public interface CustomItemImageRepository extends JpaRepository<CustomItemImage, Long> {

    List<CustomItemImage> findByCustomItemIdOrderBySortOrderAsc(Long customItemId);

    Optional<CustomItemImage> findByCustomItemIdAndIsThumbnailTrue(Long customItemId);

    long countByCustomItemId(Long customItemId);

    void deleteByCustomItemId(Long customItemId);
}
