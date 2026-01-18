package com.itemrental.rentalService.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.rentalService.domain.user.dto.request.AdminSignUpRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserFindAccountRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.itemrental.rentalService.domain.user.dto.request.UserSignUpRequestDto;
import com.itemrental.rentalService.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser("test@test.com")
    @DisplayName("회원가입 성공 - 200 OK")
    void signUp_Success() throws Exception {
        UserSignUpRequestDto request = new UserSignUpRequestDto(
                "test@test.com", "홍길동", "길동이", "password123@", "010-1234-5678", "950101"
        );

        mockMvc.perform(post("/user/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("관리자 가입 성공")
    void signUpAdmin_Success() throws Exception {
        UserSignUpRequestDto userDto = new UserSignUpRequestDto("admin@test.com", "admin", "adminNick", "password123@", "010-1234-5678", "950101");
        AdminSignUpRequestDto request = new AdminSignUpRequestDto(userDto, "secret");

        mockMvc.perform(post("/user/sign-up/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("관리자 가입이 완료되었습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("닉네임 중복 체크 성공")
    void duplicateCheck_Success() throws Exception {
        mockMvc.perform(get("/user/duplicate-check")
                        .param("nickName", "uniqueNick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용 가능한 아이디입니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("계정 찾기 성공")
    void findAccount_Success() throws Exception {
        UserFindAccountRequestDto request = new UserFindAccountRequestDto("010-1234-5678");
        given(userService.findAccount(anyString())).willReturn("found@email.com");

        mockMvc.perform(post("/user/find-account")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("found@email.com"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("회원 정보 수정 성공")
    void updateUser_Success() throws Exception {
        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto(
                "test@test.com", "새이름", "새닉네임", "010-9876-5432", "990101"
        );

        mockMvc.perform(patch("/user/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보가 수정되었습니다."));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("내 상품 목록 조회 성공")
    void getMyProducts_Success() throws Exception {
        given(userService.getMyProducts(anyString(), any())).willReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/user/my-products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("내 상품 목록 조회 성공"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/user/delete")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 탈퇴가 처리되었습니다."));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("내 찜 목록 조회 성공")
    void getMyLikedPosts_Success() throws Exception {
        given(userService.getMyLikedPosts(eq("test@test.com"), any(org.springframework.data.domain.Pageable.class)))
                .willReturn(new org.springframework.data.domain.PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/user/my-likes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("내 찜 목록 조회 성공"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("내 북마크 목록 조회 성공")
    void getMyBookmarkedPosts_Success() throws Exception {
        given(userService.getMyBookmarkedPosts(eq("test@test.com"), any(org.springframework.data.domain.Pageable.class)))
                .willReturn(new org.springframework.data.domain.PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/user/my-bookmarks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("내 북마크 목록 조회 성공"));
    }
}
