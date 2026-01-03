package com.itemrental.rentalService.domain.chat.repository;

import com.itemrental.rentalService.domain.chat.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByFcmToken(String fcmToken);

    List<FcmToken> findAllByUserId(String userId);

    void deleteByFcmToken(String fcmToken);
}
