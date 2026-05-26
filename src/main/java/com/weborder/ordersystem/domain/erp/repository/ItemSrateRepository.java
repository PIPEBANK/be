package com.weborder.ordersystem.domain.erp.repository;

import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemSrateRepository extends JpaRepository<ItemSrate, ItemSrate.ItemSrateId> {

    List<ItemSrate> findByIdItemSrateCust(Integer custCode);

    Optional<ItemSrate> findByIdItemSrateItemAndIdItemSrateCust(Integer item, Integer cust);

    void deleteByIdItemSrateItemAndIdItemSrateCust(Integer item, Integer cust);
}
