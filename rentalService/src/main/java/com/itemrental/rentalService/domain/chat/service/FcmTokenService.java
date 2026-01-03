package com.itemrental.rentalService.domain.chat.service;

import com.itemrental.rentalService.domain.chat.entity.FcmToken;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.domain.chat.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    public void saveOrUpdateToken(String email, String token, String deviceName) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("user not found with userId"));

        Optional<FcmToken> existingTokenOpt = fcmTokenRepository.findByFcmToken(token);

        if (existingTokenOpt.isPresent()) {
            FcmToken existingToken = existingTokenOpt.get();

            if (existingToken.getUser().getEmail().equals(email)) {
                existingToken.setUpdateAt(LocalDateTime.now());
                existingToken.setDeviceName(deviceName);
                fcmTokenRepository.save(existingToken);

            } else {
                existingToken.setUser(user);
                existingToken.setUpdateAt(LocalDateTime.now());
                existingToken.setDeviceName(deviceName);
                fcmTokenRepository.save(existingToken);
            }

        } else {
            FcmToken newToken = FcmToken.builder()
                    .user(user)
                    .fcmToken(token)
                    .deviceName(deviceName)
                    .build();

            user.addFcmToken(newToken);
            fcmTokenRepository.save(newToken);
        }
    }
}
