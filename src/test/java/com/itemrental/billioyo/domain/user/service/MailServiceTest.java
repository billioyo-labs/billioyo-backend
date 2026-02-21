package com.itemrental.billioyo.domain.user.service;

import com.itemrental.billioyo.domain.auth.entity.VerificationToken;
import com.itemrental.billioyo.domain.auth.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private MimeMessage mimeMessage;

    private final String TEST_EMAIL = "test@test.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailService, "senderEmail", "admin@billioyo.com");
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
    }

    @Test
    @DisplayName("인증 메일 발송 성공")
    void sendVerificationMail_Success() throws MessagingException {
        // given
        given(templateEngine.process(eq("email-template"), any(Context.class)))
                .willReturn("<html>Test Content</html>");

        // when
        mailService.sendVerificationMail(TEST_EMAIL);

        // then
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("임시 비밀번호 메일 발송 성공")
    void sendTemporalPasswordMail_Success() throws MessagingException {
        // given
        String tempPw = "temp1234";
        given(templateEngine.process(eq("temporal-password"), any(Context.class)))
                .willReturn("<html>Password Content</html>");

        // when
        mailService.sendTemporalPasswordMail(TEST_EMAIL, tempPw);

        // then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("실패: 메일 발송 중 에러 발생 시 로그만 남기고 예외를 던지지 않는다 (내결함성)")
    void sendMail_Fail_HandleMessagingException() throws MessagingException {
        // given
        given(templateEngine.process(anyString(), any(Context.class)))
                .willReturn("<html>Content</html>");
        doThrow(new RuntimeException("Mail Server Down"))
                .when(mailSender).send(any(MimeMessage.class));

        // when
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                mailService.sendVerificationMail(TEST_EMAIL)
        );

        // then
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("임시 비밀번호 메일 발송 실패 - catch 블록 진입 확인")
    void sendTemporalPasswordMail_Fail_CatchBlock() throws MessagingException {
        // Given
        given(templateEngine.process(anyString(), any(Context.class))).willReturn("<html></html>");
        doThrow(new org.springframework.mail.MailSendException("SMTP Server Error"))
                .when(mailSender).send(any(jakarta.mail.internet.MimeMessage.class));

        // When & Then
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                mailService.sendTemporalPasswordMail(TEST_EMAIL, "temp1234")
        );

        verify(mailSender).send(any(jakarta.mail.internet.MimeMessage.class));
    }
}
