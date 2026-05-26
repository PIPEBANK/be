package com.weborder.ordersystem.domain.web.order.repository;

import com.weborder.ordersystem.domain.web.order.entity.GuestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {

    Optional<GuestOrder> findByOrderKey(String orderKey);

    List<GuestOrder> findByOrderKeyIn(List<String> orderKeys);

    @Query("SELECT g FROM GuestOrder g WHERE " +
           "REPLACE(g.companyName, ' ', '') = REPLACE(:companyName, ' ', '') " +
           "AND REPLACE(g.managerName, ' ', '') = REPLACE(:managerName, ' ', '') " +
           "AND REPLACE(g.contact, ' ', '') = REPLACE(:contact, ' ', '')")
    List<GuestOrder> findByCompanyInfo(
            @Param("companyName") String companyName,
            @Param("managerName") String managerName,
            @Param("contact") String contact);
}
