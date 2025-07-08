package com.pipebank.ordersystem.domain.web.temp.repository;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempWebOrderTranRepository extends JpaRepository<TempWebOrderTran, TempWebOrderTran.TempWebOrderTranId> {
} 