package com.itemrental.rentalService.service;

import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.entity.VerificationToken;
import com.itemrental.rentalService.repository.UserRepository;
import com.itemrental.rentalService.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public void verifyToken(String token) {
        Optional<VerificationToken> opVerificationToken = verificationTokenRepository.findById(token);
        VerificationToken verificationToken = opVerificationToken.orElseThrow(() -> new IllegalArgumentException(""));
        Optional<User> opUser = userRepository.findByEmail(verificationToken.getEmail());
        User user = opUser.orElseThrow(() -> new IllegalArgumentException("없는 사용자"));
        user.setEmailVerified(true);
        verificationTokenRepository.deleteById(token);
        userRepository.save(user);
    }
}
