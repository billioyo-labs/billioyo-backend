package com.itemrental.rentalService.service;

import com.itemrental.rentalService.dto.SendResetMailDto;
import com.itemrental.rentalService.entity.ResetToken;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.entity.VerificationToken;
import com.itemrental.rentalService.exceptions.PendingProfileSetupException;
import com.itemrental.rentalService.repository.ResetTokenRepository;
import com.itemrental.rentalService.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    //private final ResetTokenRepository resetTokenRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

//    public void sendResetMail(SendResetMailDto sendResetMailDto) throws MessagingException {
//        String email = sendResetMailDto.getEmail();
//        String nickName = sendResetMailDto.getName();
//        String token = UUID.randomUUID().toString();
//        ResetToken resetToken = new ResetToken(token, email);
//        resetTokenRepository.save(resetToken);
//        String resetLink = "http://localhost:8080/auth/reset-password?token=" + resetToken.getToken();
//
//        sendResetLinkWithHtmlTemplate(email, "비밀번호 초기화를 원하시면 링크를 눌러주세요.", nickName, 5, resetLink);
//    }

    @Transactional
    public void sendTemporalPasswordMail(SendResetMailDto sendResetMailDto) throws MessagingException{
        String email = sendResetMailDto.getEmail();
        String name = sendResetMailDto.getName();
        String temporalPassword = UUID.randomUUID().toString();
        String cryptedPassowrd = passwordEncoder.encode(temporalPassword);
        Optional<User> user = userService.findByEmail(email);
        if(user.isPresent() && user.get().getUsername().equals(name)){
                user.get().setPassword(cryptedPassowrd);
        }else{
            throw new IllegalArgumentException("일치하는 사용자가 없습니다.");
        }

        sendTemporalPasswordWithHtmlTemplate(email, "임시 비밀번호입니다. 반드시 초기화 후 이용해 주세요.", temporalPassword);
    }

    public void sendVerificationMail(String email) throws MessagingException {
        String accountState = checkAccountState(email);
        if(accountState.equals("초기화되지 않은 계정")){
            throw new PendingProfileSetupException("초기화되지 않은 계정");
        }
        if(accountState.equals("처음 가입하는 email")){
            userService.makeInitialUser(email);
        }

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, email);
        verificationTokenRepository.save(verificationToken);
        String verificationLink = "http://localhost:8080/auth/verify-email?token=" + verificationToken.getToken();

        sendEmailVerificationLinkWithHtmlTemplate(email, "인증을 완료하시려면 링크를 눌러주세요.", verificationLink);
    }

//    public void sendResetLinkWithHtmlTemplate(String to, String subject, String username, int expiryMinutes, String resetLink) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//        helper.setFrom(senderEmail);
//        helper.setTo(to);
//        helper.setSubject(subject);
//
//        Context context = new Context();
//
//        context.setVariable("username", username);
//        context.setVariable("expiryMinutes", expiryMinutes);
//        context.setVariable("resetLink", resetLink);
//
//        String htmlContent = templateEngine.process("password-reset-mail", context);
//
//        helper.setText(htmlContent, true);
//
//        mailSender.send(message);
//    }

    public void sendTemporalPasswordWithHtmlTemplate(String to, String subject, String temporalPassword) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();

        context.setVariable("temporaryPassword", temporalPassword);

        String htmlContent = templateEngine.process("temporal-password", context);

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendEmailVerificationLinkWithHtmlTemplate(String to, String subject, String verificationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();

        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("email-template", context);

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public String checkAccountState(String email){
        Optional<User> user = userService.findByEmail(email);
        if(user.isPresent()){
            if(user.get().getUserState().equals(User.UserState.UNVERIFIED)){
                return "인증되지 않은 계정";
            }else{
                return "초기화되지 않은 계정";
            }
        }else{
            return "처음 가입하는 email";
        }
    }

    public boolean doesEmailAndNickNameMatch(String email, String nickname){
        Optional<User> user = userService.findByEmail(email);
        return user.map(value -> value.getNickName().equals(nickname)).orElse(false);
    }
}
