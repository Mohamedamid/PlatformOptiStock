package com.optistockplatrorm.repository;

import java.util.Optional;
import com.optistockplatrorm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
