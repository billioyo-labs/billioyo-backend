package com.itemrental.billioyo.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.billioyo.domain.user.dto.request.EmailVerificationRequestDto;
import com.itemrental.billioyo.domain.user.dto.request.PasswordResetRequestDto;
import com.itemrental.billioyo.domain.user.service.MailService;
import com.itemrental.billioyo.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    @DisplayName("인증 링크 전송 성공")
    void sendVerificationLink_Success() throws Exception {
        EmailVerificationRequestDto request = new EmailVerificationRequestDto("test@test.com");

        mockMvc.perform(post("/mail/send-verification-link")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증 링크 이메일 전송 요청 완료"));
    }

    @Test
    @WithMockUser
    @DisplayName("임시 비밀번호 전송 성공")
    void sendResetPassword_Success() throws Exception {
        PasswordResetRequestDto request = new PasswordResetRequestDto("test@test.com", "name");
        given(userService.resetToTemporalPassword(anyString(), anyString())).willReturn("temp1234");

        mockMvc.perform(post("/mail/send-reset-link")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("임시 비밀번호 이메일 전송 요청 완료"));
    }

    @Test
    @WithMockUser
    @DisplayName("이메일 형식 오류 시 400 Bad Request 발생")
    void sendVerificationLink_Fail_InvalidEmail() throws Exception {
        EmailVerificationRequestDto request = new EmailVerificationRequestDto("invalid-email-format");

        mockMvc.perform(post("/mail/send-verification-link")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // @Valid에 의해 차단됨
    }
}
