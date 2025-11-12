package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.Client;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ClientRepository  extends JpaRepository<Client, Long> {
    boolean existsByEmail(String email);
}