package com.itemrental.rentalService.global.utils;

import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
        if (user.getUserState() != User.UserState.ACTIVE) {
            throw new DisabledException("계정이 활성화 상태가 아닙니다.");
        }
        return createUserDetail(user);
    }

    private UserDetails createUserDetail(User user) {
        return User.builder()
            .username(user.getEmail())
            .email(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRoles())
            .build();
    }
}