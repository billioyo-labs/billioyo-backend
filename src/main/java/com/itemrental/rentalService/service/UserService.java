package com.itemrental.rentalService.service;

import com.itemrental.rentalService.dto.SignUpDto;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.exceptions.DuplicateUsernameException;
import com.itemrental.rentalService.exceptions.PasswordMismatchException;
import com.itemrental.rentalService.repository.UserRepository;
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

    public String signUp(SignUpDto signUpDto){
        String email = signUpDto.getEmail();
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            if(user.get().isEmailVerified()){
                return "이미 가입된 이메일";
            }else{
                return "이메일 인증 페이지 이동";
            }
        }else{
            duplicateCheck(signUpDto.getName());
            if(!Objects.equals(signUpDto.getPassword(), signUpDto.getPasswordConfirmation())){
                throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }

            String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
            List<String> roles = new ArrayList<>();
            roles.add("USER");
            userRepository.save(signUpDto.toEntity(encodedPassword, roles));
            return "사용자 정보 저장 완료";
        }
    }

    public void duplicateCheck(String username){
        if(userRepository.existsByUsername(username)){
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }
    }
}
