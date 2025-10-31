package com.optistockplatrorm.repository;

import java.util.Optional;
import com.optistockplatrorm.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
