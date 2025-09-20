package com.itemrental.rentalService.service;

import com.itemrental.rentalService.entity.VerificationToken;
import com.itemrental.rentalService.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendVerificationMail(String email, String username) throws MessagingException {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, email);
        verificationTokenRepository.save(verificationToken);
        String verificationLink = "http://localhost:8080/auth/verify-email?token=" + verificationToken.getToken();

        sendEmailWithHtmlTemplate(email, "인증을 완료하시려면 링크를 눌러주세요.", username, verificationLink);
    }

    public void sendEmailWithHtmlTemplate(String to, String subject, String username, String verificationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        log.info("{}", senderEmail);
        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        // 1. Thymeleaf Context 생성
        Context context = new Context();
        // 2. 템플릿에 전달할 변수 설정
        context.setVariable("username", username);
        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("email-template", context);

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
