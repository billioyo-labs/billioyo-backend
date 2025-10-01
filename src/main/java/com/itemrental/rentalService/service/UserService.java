package com.itemrental.rentalService.service;

import com.itemrental.rentalService.dto.SignUpDto;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.exceptions.DuplicateUsernameException;
import com.itemrental.rentalService.exceptions.PasswordMismatchException;
import com.itemrental.rentalService.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public String signUp(SignUpDto signUpDto){
        String email = signUpDto.getEmail();
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        User user = findByEmail(email).orElseThrow(() ->
                new RuntimeException("이메일에 해당하는 사용자가 없습니다."));
        User updateUser = signUpDto.toEntity(encodedPassword, roles);
        updateUser.setId(user.getId());
        userRepository.save(updateUser);
        return "사용자 정보 저장 완료";
    }

    public void duplicateCheck(String nickName){
        if(userRepository.existsByNickName(nickName)){
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }
    }

    public String findAccount(String phoneNumber){
        Optional<User> opUser = userRepository.findByPhoneNumber(phoneNumber);
        if(opUser.isPresent()){
            return opUser.get().getEmail();
        }else{
            return "해당하는 사용자가 없습니다.";
        }
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User makeInitialUser(String email){
        User user = User.builder().email(email).build();
        return userRepository.save(user);
    }
}
