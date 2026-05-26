package com.weborder.ordersystem.domain.web.delivery.repository;

import com.weborder.ordersystem.domain.web.delivery.entity.DeliveryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPhotoRepository extends JpaRepository<DeliveryPhoto, Long> {
    List<DeliveryPhoto> findByOrderKeyOrderByCreatedAtAsc(String orderKey);
}
