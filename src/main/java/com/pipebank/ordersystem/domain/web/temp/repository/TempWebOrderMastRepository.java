package com.pipebank.ordersystem.domain.web.temp.repository;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempWebOrderMastRepository extends JpaRepository<TempWebOrderMast, TempWebOrderMast.TempWebOrderMastId> {
} 