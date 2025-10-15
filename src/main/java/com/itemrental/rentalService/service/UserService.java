package com.itemrental.rentalService.service;

import com.itemrental.rentalService.dto.DeleteUserDto;
import com.itemrental.rentalService.dto.SignUpDto;
import com.itemrental.rentalService.dto.UpdateUserDto;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.exceptions.DuplicateUsernameException;
import com.itemrental.rentalService.exceptions.PasswordMismatchException;
import com.itemrental.rentalService.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Transactional
    public UpdateUserDto getProfile(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();
        return new UpdateUserDto(
            user.getEmail(),
            username,
            user.getNickName(),
            user.getPhoneNumber(),
            user.getBirthDate()
        );
    }


    //회원정보 수정 기능
    @Transactional
    public String updateUser(UpdateUserDto updateUserDto){
        User user = userRepository.findByEmail(updateUserDto.getEmail()).get();

        if (StringUtils.hasText(updateUserDto.getNickName()) &&
            !Objects.equals(user.getNickName(), updateUserDto.getNickName())) {
            duplicateCheck(updateUserDto.getNickName());
            user.setNickName(updateUserDto.getNickName());
        }
        if (StringUtils.hasText(updateUserDto.getName())) {
            user.setUsername(updateUserDto.getName());
        }
        if (StringUtils.hasText(updateUserDto.getPhoneNumber())) {
            user.setPhoneNumber(updateUserDto.getPhoneNumber());
        }
        if (StringUtils.hasText(updateUserDto.getBirthDate())) {
            user.setBirthDate(updateUserDto.getBirthDate());
        }
        userRepository.save(user);

        return "사용자 정보 수정 완료";
    }

    //회원 삭제
    public String deleteUser(DeleteUserDto deleteUserDto){
        User user = userRepository.findByEmail(deleteUserDto.getEmail()).get();

        if (!passwordEncoder.matches(deleteUserDto.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        userRepository.delete(user);

        return "회원 탈퇴가 완료되었습니다.";
    }

}
