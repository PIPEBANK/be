package com.weborder.ordersystem.domain.web.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sa_guest_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ORDER_KEY", nullable = false, length = 60)
    private String orderKey;

    @Column(name = "COMPANY_NAME", nullable = false, length = 200)
    private String companyName;

    @Column(name = "MANAGER_NAME", nullable = false, length = 50)
    private String managerName;

    @Column(name = "CONTACT", nullable = false, length = 30)
    private String contact;

    @Column(name = "ADDRESS", length = 400)
    private String address;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public GuestOrder(String orderKey, String companyName, String managerName,
                      String contact, String address, LocalDateTime createdAt) {
        this.orderKey = orderKey;
        this.companyName = companyName;
        this.managerName = managerName;
        this.contact = contact;
        this.address = address;
        this.createdAt = createdAt;
    }
}
