package com.itemrental.rentalService.domain.user.service;

import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserSignUpRequestDto;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.*;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private final String TEST_EMAIL = "test@test.com";
    private final String ADMIN_SECRET = "secret_code";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "adminSignupSecret", ADMIN_SECRET);
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void signUp_Success() {
        UserSignUpRequestDto dto = createSignUpDto();
        User user = spy(User.builder().email(TEST_EMAIL).build());

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(userRepository.existsByNickName(dto.getNickName())).willReturn(false);
        given(passwordEncoder.encode(dto.getPassword())).willReturn("encodedPw");

        userService.signUp(dto);

        verify(user).completeRegistration(eq("encodedPw"), anyString(), anyString(), anyString(), anyString(), eq(List.of("USER")));
    }

    @Test
    @DisplayName("관리자 회원가입 성공")
    void signUpAdmin_Success() {
        UserSignUpRequestDto dto = createSignUpDto();
        User user = spy(User.builder().email(TEST_EMAIL).build());

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(userRepository.existsByNickName(dto.getNickName())).willReturn(false);
        given(passwordEncoder.encode(dto.getPassword())).willReturn("encodedPw");

        userService.signUpAdmin(dto, ADMIN_SECRET);

        verify(user).completeRegistration(any(), any(), any(), any(), any(), argThat(roles -> roles.contains("ADMIN")));
    }

    @Test
    @DisplayName("관리자 가입 실패 - 잘못된 가입 코드")
    void signUpAdmin_Fail_InvalidSecret() {
        UserSignUpRequestDto dto = createSignUpDto();
        User user = User.builder().email(TEST_EMAIL).build();
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.signUpAdmin(dto, "wrong_secret"))
                .isInstanceOf(InvalidAdminSecretException.class);

        verify(userRepository, never()).existsByNickName(anyString());
    }

    @Test
    @DisplayName("닉네임 중복 체크 실패 - 중복된 닉네임 존재")
    void duplicateCheck_Fail() {
        given(userRepository.existsByNickName("nick")).willReturn(true);

        assertThatThrownBy(() -> userService.duplicateCheck("nick"))
                .isInstanceOf(DuplicateUsernameException.class);
    }

    @Test
    @DisplayName("내 상품 목록 조회 성공")
    void getMyProducts_Success() {
        User user = User.builder().email(TEST_EMAIL).build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(postRepository.findByUserId(1L, pageable)).willReturn(new PageImpl<>(Collections.emptyList()));

        Page<RentalPostListResponseDto> result = userService.getMyProducts(TEST_EMAIL, pageable);

        assertThat(result).isNotNull();
        verify(postRepository).findByUserId(1L, pageable);
    }

    @Test
    @DisplayName("내 찜 목록 조회 성공")
    void getMyLikedPosts_Success() {
        User user = User.builder().email(TEST_EMAIL).build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(postRepository.findByLikesUserId(1L, pageable)).willReturn(new PageImpl<>(Collections.emptyList()));

        userService.getMyLikedPosts(TEST_EMAIL, pageable);

        verify(postRepository).findByLikesUserId(1L, pageable);
    }

    @Test
    @DisplayName("내 북마크 목록 조회 성공")
    void getMyBookmarkedPosts_Success() {
        User user = User.builder().email(TEST_EMAIL).build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(postRepository.findByBookmarksUserId(1L, pageable)).willReturn(new PageImpl<>(Collections.emptyList()));

        userService.getMyBookmarkedPosts(TEST_EMAIL, pageable);

        verify(postRepository).findByBookmarksUserId(1L, pageable);
    }

    @Test
    @DisplayName("계정 찾기 성공 - 휴대폰 번호로 이메일 반환")
    void findAccount_Success() {
        User user = User.builder().email(TEST_EMAIL).build();
        given(userRepository.findByPhoneNumber("01012341234")).willReturn(Optional.of(user));

        String result = userService.findAccount("01012341234");

        assertThat(result).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("프로필 업데이트 성공 - 닉네임이 변경된 경우 중복체크 수행")
    void updateUser_WithNicknameChange() {
        UserProfileUpdateRequestDto dto = new UserProfileUpdateRequestDto(TEST_EMAIL, "name", "newNick", "010-1-1", "900101");
        User user = spy(User.builder().email(TEST_EMAIL).nickName("oldNick").build());

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(userRepository.existsByNickName("newNick")).willReturn(false);

        userService.updateUser(TEST_EMAIL, dto);

        verify(userRepository).existsByNickName("newNick");
        verify(user).updateProfile(any(), any(), any(), any());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_Success() {
        User user = User.builder().email(TEST_EMAIL).build();
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));

        userService.deleteUser(TEST_EMAIL);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("비밀번호 초기화 성공")
    void resetToTemporalPassword_Success() {
        User user = User.builder().email(TEST_EMAIL).username("홍길동").build();
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(passwordEncoder.encode(anyString())).willReturn("encoded");

        String tempPw = userService.resetToTemporalPassword(TEST_EMAIL, "홍길동");

        assertThat(tempPw).hasSize(8);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("초기 등록 프로세스 - 유저가 없는 경우 새로 생성")
    void processInitial_CreateNew() {
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.empty());

        userService.processInitialRegistration(TEST_EMAIL);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("초기 등록 프로세스 실패 - 이미 가입 완료된 상태(ACTIVE)인 경우")
    void processInitial_Fail_AlreadyActive() {
        User user = User.builder().email(TEST_EMAIL).userState(User.UserState.ACTIVE).build();
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.processInitialRegistration(TEST_EMAIL))
                .isInstanceOf(PendingProfileSetupException.class);
    }

    @Test
    @DisplayName("초기 유저 생성 - 기존 유저가 있으면 새로 저장하지 않고 반환")
    void makeInitialUser_AlreadyExists() {
        User user = User.builder().email(TEST_EMAIL).build();
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));

        User result = userService.makeInitialUser(TEST_EMAIL);

        assertThat(result).isEqualTo(user);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("비밀번호 초기화 실패 - 존재하지 않는 이메일")
    void resetToTemporalPassword_Fail_UserNotFound() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resetToTemporalPassword("none@test.com", "이름"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("닉네임 중복 검증 실패 - 예외 발생 확인")
    void validateDuplicateNickname_Fail_ThrowsException() {
        UserSignUpRequestDto dto = createSignUpDto();
        User user = User.builder()
                .email(TEST_EMAIL)
                .username("홍길동")
                .nickName("길동이")
                .phoneNumber("010-1234-5678")
                .birthDate("950101")
                .build();
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(userRepository.existsByNickName(dto.getNickName())).willReturn(true);

        assertThatThrownBy(() -> userService.signUp(dto))
                .isInstanceOf(DuplicateUsernameException.class);
    }

    @Test
    @DisplayName("프로필 정보 조회(getProfile) 성공")
    void getProfile_Success() {
        User user = User.builder()
                .email(TEST_EMAIL)
                .username("홍길동")
                .nickName("길동이")
                .phoneNumber("010-1234-5678")
                .birthDate("950101")
                .build();
        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));

        UserProfileUpdateRequestDto result = userService.getProfile(TEST_EMAIL);

        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getNickName()).isEqualTo("길동이");
    }

    @Test
    @DisplayName("비밀번호 초기화 실패 - 이름 정보 불일치")
    void resetToTemporalPassword_Fail_InvalidName() {
        User user = User.builder()
                .email(TEST_EMAIL)
                .username("홍길동")
                .build();

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.resetToTemporalPassword(TEST_EMAIL, "임꺽정"))
                .isInstanceOf(UserInformationMismatchException.class);

        verify(passwordEncoder, never()).encode(anyString());
    }

    private UserSignUpRequestDto createSignUpDto() {
        return new UserSignUpRequestDto(TEST_EMAIL, "name", "nick", "pw", "010-0000-0000", "900101");
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
