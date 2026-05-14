package com.giftedlabs.prymageproduct.repository;

import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRole(Role role);
}
