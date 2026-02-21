package com.itemrental.billioyo.domain.user.repository;

import com.itemrental.billioyo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickname);

    boolean existsByUsername(String username);
}