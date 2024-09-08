package com.finance.stockhub.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String AUTH_PREFIX = "authcode:";

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    // 인증 코드 생성
    public String createCode() {
        int randomNum = (int) (Math.random() * 899999) + 100000;
        return String.valueOf(randomNum);
    }

    // 이메일 양식 생성
    public MimeMessage createEmailForm(String email, String authCode) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            String fromEmail = "alex.yb.dev@gmail.com";
            String toEmail = email;
            String title = "StockHub 회원가입 인증번호";
            String contents = "";
            contents += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            contents += "<h1>" + authCode + "</h1>";
            contents += "<h3>" + "감사합니다." + "</h3>";

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail); // 발신자 이메일
            helper.setTo(toEmail); // 수신자 이메일
            helper.setSubject(title); // 이메일 제목
            helper.setText(contents, true); // 이메일 내용
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    // 이메일 발신 (인증코드 발신)
    public String sendEmail(String toEmail) {
        String authCode = createCode();
        MimeMessage form = createEmailForm(toEmail, authCode);
        javaMailSender.send(form);

        // 인증 코드 redis 저장
        redisService.setValues(AUTH_PREFIX + toEmail, authCode);

        return authCode;
    }
}
