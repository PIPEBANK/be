package com.weborder.ordersystem.domain.web.customitem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.web.customitem.entity.CustomItem;

@Repository
public interface CustomItemRepository extends JpaRepository<CustomItem, Long> {

    List<CustomItem> findByCustomItemCustAndCustomItemUseOrderByCustomItemFdateDesc(Integer custCode, Integer use);

    default List<CustomItem> findActiveByCust(Integer custCode) {
        return findByCustomItemCustAndCustomItemUseOrderByCustomItemFdateDesc(custCode, 1);
    }

    long countByCustomItemCustAndCustomItemUse(Integer custCode, Integer use);
}
