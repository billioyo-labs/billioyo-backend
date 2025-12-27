package com.itemrental.rentalService.domain.auth.service;

import com.itemrental.rentalService.domain.auth.entity.ResetToken;
import com.itemrental.rentalService.domain.auth.entity.VerificationToken;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.ResetTokenRepository;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.domain.user.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final UserRepository userRepository;

    public boolean verifyVerificationToken(String token) {
        Optional<VerificationToken> opVerificationToken = verificationTokenRepository.findById(token);
        if(opVerificationToken.isEmpty()) return false;
        VerificationToken verificationToken = opVerificationToken.orElseThrow(() -> new IllegalArgumentException("잘못된 인증 토큰"));
        Optional<User> opUser = userRepository.findByEmail(verificationToken.getEmail());
        if(opUser.isEmpty()) return false;
        User user = opUser.orElseThrow(() -> new IllegalArgumentException("없는 사용자"));
        user.setUserState(User.UserState.PENDING_PROFILE_SETUP);
        verificationTokenRepository.deleteById(token);
        userRepository.save(user);
        return true;
    }

    public boolean verifyResetToken(String token){
        Optional<ResetToken> opResetToken = resetTokenRepository.findById(token);
        if(opResetToken.isEmpty()) return false;
        ResetToken resetToken = opResetToken.orElseThrow(() -> new IllegalArgumentException("잘못된 비밀번호 초기화 토큰"));
        Optional<User> opUser = userRepository.findByEmail(resetToken.getEmail());
        if(opUser.isEmpty()){
            return false;
        }
        return true;
    }
}
