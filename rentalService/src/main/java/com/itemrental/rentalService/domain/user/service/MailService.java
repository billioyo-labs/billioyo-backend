package com.itemrental.rentalService.domain.user.service;

import com.itemrental.rentalService.domain.auth.entity.VerificationToken;
import com.itemrental.rentalService.domain.auth.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendTemporalPasswordMail(String email, String temporalPassword) {
        try {
            sendHtmlMail(email, "임시 비밀번호 안내입니다. 반드시 초기화 후 이용해 주세요.", "temporal-password",
                    Map.of("temporaryPassword", temporalPassword));

        } catch (Exception e) {
            log.error("임시 비밀번호 메일 발송 실패 - 대상: {}, 사유: {}", email, e.getMessage());
        }
    }

    @Async
    public void sendVerificationMail(String email) {
        try {
            String token = UUID.randomUUID().toString();
            verificationTokenRepository.save(new VerificationToken(token, email));

            String verificationLink = "https://www.billioyo.o-r.kr/auth/verify-email?token=" + token;

            sendHtmlMail(email, "인증을 완료하시려면 링크를 눌러주세요.", "email-template",
                    Map.of("verificationLink", verificationLink));

        } catch (Exception e) {
            log.error("인증 메일 발송 실패 - 대상: {}, 사유: {}", email, e.getMessage());
        }
    }

    private void sendHtmlMail(String to, String subject, String templateName, Map<String, Object> variables) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();
        variables.forEach(context::setVariable);

        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("메일 발송 완료: To={}, Template={}", to, templateName);
    }
}
