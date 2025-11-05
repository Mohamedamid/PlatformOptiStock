package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.Carrier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Page<Carrier> findAll(Pageable pageable);
    boolean existsByCarrierName(String carrierName);
}