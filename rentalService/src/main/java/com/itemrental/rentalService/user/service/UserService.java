package com.itemrental.rentalService.user.service;


import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.rental.entity.Post;
import com.itemrental.rentalService.rental.repository.PostRepository;
import com.itemrental.rentalService.user.dto.ReportListResponseDto;
import com.itemrental.rentalService.user.dto.ReportRequestDto;
import com.itemrental.rentalService.user.dto.SignUpDto;
import com.itemrental.rentalService.user.dto.UpdateUserDto;
import com.itemrental.rentalService.user.entity.Report;
import com.itemrental.rentalService.user.entity.User;
import com.itemrental.rentalService.exceptions.DuplicateUsernameException;
import com.itemrental.rentalService.user.repository.ReportRepository;
import com.itemrental.rentalService.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    private final CommunityPostRepository communityPostRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    @Value("${admin.signup-secret}")
    private String adminSignupSecret;


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

    //관리자 가입
    public void validateAdminSignupSecret(String adminSecret){
        if(!Objects.equals(adminSecret, adminSignupSecret)){
            throw new RuntimeException("관리자 가입 코드가 올바르지 않습니다.");
        }
    }
    @Transactional
    public String signUpAdmin(SignUpDto signUpDto, String adminSecret){
        validateAdminSignupSecret(adminSecret);
        String email = signUpDto.getEmail();
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        User user = findByEmail(email).orElseThrow(() ->
            new RuntimeException("이메일에 해당하는 사용자가 없습니다."));
        User updateUser = signUpDto.toEntity(encodedPassword, roles);
        updateUser.setId(user.getId());
        userRepository.save(updateUser);
        return "관리자 정보 저장 완료";
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
    public String deleteUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();

        userRepository.delete(user);

        return "회원 탈퇴가 완료되었습니다.";
    }

    //관리자 유저 차단
    public String banUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (user.getUserState() == User.UserState.BANNED) {
            throw new IllegalStateException("이미 차단된 유저입니다.");
        }
        user.setUserState(User.UserState.BANNED);
        userRepository.save(user);
        return "유저가 차단 되었습니다.";
    }


    // 커뮤니티/렌탈 게시글 신고
    @Transactional
    public void reportPost(ReportRequestDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        if (dto.getTargetType() == Report.TargetType.COMMUNITY) {
            if (!communityPostRepository.existsById(dto.getTargetId())) {
                throw new IllegalArgumentException("CommunityPost not found: " + dto.getTargetId());
            }
        } else if (dto.getTargetType() == Report.TargetType.RENTAL) {
            if (!postRepository.existsById(dto.getTargetId())) {
                throw new IllegalArgumentException("Rental Post not found: " + dto.getTargetId());
            }
        } else {
            throw new IllegalArgumentException("Invalid targetType: " + dto.getTargetType());
        }

        Report report = Report.builder()
            .targetId(dto.getTargetId())
            .targetType(dto.getTargetType())
            .reporter(reporter)
            .reason(dto.getReason())
            .description(dto.getDescription())
            .build();

        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Page<ReportListResponseDto> getReportList(Pageable pageable) {
        Page<Report> page = reportRepository.findAll(pageable);

        return page
            .map(report -> ReportListResponseDto.builder()
                .id(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .description(report.getDescription())
                .createdAt(report.getCreatedAt())
                .build());
    }
    //관리자 게시글 삭제
    @Transactional
    public void adminDeleteCommunityPost(Long postId) {

        CommunityPost post = communityPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        ;
        communityPostRepository.delete(post);
    }
    @Transactional
    public void adminDeleteRentalPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        ;
        postRepository.delete(post);
    }



}
