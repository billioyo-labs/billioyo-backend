package com.itemrental.rentalService.domain.user.service;


import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.user.dto.request.UserSignUpRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.exceptions.DuplicateUsernameException;
import com.itemrental.rentalService.global.exceptions.PendingProfileSetupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Value("${admin.signup-secret}")
    private String adminSignupSecret;


    @Transactional
    public void signUp(UserSignUpRequestDto userSignUpRequestDto) {
        User user = getUserByEmail(userSignUpRequestDto.getEmail());

        validateDuplicateNickname(userSignUpRequestDto.getNickName());

        user.completeRegistration(
                passwordEncoder.encode(userSignUpRequestDto.getPassword()),
                userSignUpRequestDto.getName(),
                userSignUpRequestDto.getNickName(),
                userSignUpRequestDto.getPhoneNumber(),
                userSignUpRequestDto.getBirthDate(),
                List.of("USER")
        );
    }

    public void validateAdminSignupSecret(String adminSecret) {
        if (!Objects.equals(adminSecret, adminSignupSecret)) {
            throw new RuntimeException("관리자 가입 코드가 올바르지 않습니다.");
        }
    }

    private void validateDuplicateNickname(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new DuplicateUsernameException(nickName);
        }
    }

    @Transactional
    public void signUpAdmin(UserSignUpRequestDto userSignUpRequestDto, String adminSecret) {
        User user = getUserByEmail(userSignUpRequestDto.getEmail());
        validateAdminSignupSecret(adminSecret);

        validateDuplicateNickname(userSignUpRequestDto.getNickName());

        user.completeRegistration(
                passwordEncoder.encode(userSignUpRequestDto.getPassword()),
                userSignUpRequestDto.getName(),
                userSignUpRequestDto.getNickName(),
                userSignUpRequestDto.getPhoneNumber(),
                userSignUpRequestDto.getBirthDate(),
                List.of("ADMIN")
        );
    }

    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getMyProducts(String email, Pageable pageable) {
        User user = getUserByEmail(email);

        return postRepository.findByUserId(user.getId(), pageable)
                .map(RentalPostListResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getMyLikedPosts(String email, Pageable pageable) {
        User user = getUserByEmail(email);

        return postRepository.findByLikesUserId(user.getId(), pageable)
                .map(RentalPostListResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RentalPostListResponseDto> getMyBookmarkedPosts(String email, Pageable pageable) {
        User user = getUserByEmail(email);

        return postRepository.findByBookmarksUserId(user.getId(), pageable)
                .map(RentalPostListResponseDto::from);
    }

    public void duplicateCheck(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
    }

    public String findAccount(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + phoneNumber)).getEmail();
    }

    public User makeInitialUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .userState(User.UserState.UNVERIFIED)
                                .build()
                ));
    }

    @Transactional
    public UserProfileUpdateRequestDto getProfile(String email) {
        User user = getUserByEmail(email);
        return UserProfileUpdateRequestDto.from(user);
    }

    @Transactional
    public void updateUser(String email, UserProfileUpdateRequestDto userProfileUpdateRequestDto) {
        User user = getUserByEmail(email);

        if (StringUtils.hasText(userProfileUpdateRequestDto.getNickName()) && !user.getNickName().equals(userProfileUpdateRequestDto.getNickName())) {
            duplicateCheck(userProfileUpdateRequestDto.getNickName());
        }

        user.updateProfile(userProfileUpdateRequestDto.getName(), userProfileUpdateRequestDto.getNickName(), userProfileUpdateRequestDto.getPhoneNumber(), userProfileUpdateRequestDto.getBirthDate());
    }

    public void deleteUser(String email) {
        User user = getUserByEmail(email);

        userRepository.delete(user);
    }

    @Transactional
    public String resetToTemporalPassword(String email, String name) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!user.getUsername().equals(name)) {
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
        }

        String temporalPassword = UUID.randomUUID().toString().substring(0, 8);
        user.updatePassword(passwordEncoder.encode(temporalPassword));

        return temporalPassword;
    }

    @Transactional
    public void processInitialRegistration(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            makeInitialUser(email);
        } else if (user.get().getUserState() != User.UserState.UNVERIFIED) {
            throw new PendingProfileSetupException("이미 초기 가입 절차가 완료된 계정입니다.");
        }
    }

}
