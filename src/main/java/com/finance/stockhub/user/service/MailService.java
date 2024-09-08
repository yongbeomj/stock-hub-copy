package com.finance.stockhub.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String AUTH_PREFIX = "authcode:";
    private final JavaMailSender javaMailSender;
    private static int authNumber;
    private final Map<String, Integer> checkEmailMap = new HashMap<>();
    private final RedisService redisService;

    // 인증 코드 생성
    public void createKey() {
        authNumber = (int) (Math.random() * 899999) + 100000;
    }

    // 이메일 양식 생성
    public MimeMessage createEmailForm(String email) {
        createKey();

        // 메일 형식
        String fromEmail = "alex.yb.dev@gmail.com";
        String toEmail = email;
        String title = "StockHub 회원가입 인증번호";
        String contents = "";
        contents += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
        contents += "<h1>" + authNumber + "</h1>";
        contents += "<h3>" + "감사합니다." + "</h3>";

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
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
    public int sendEmail(String toEmail) {
        MimeMessage emailForm = createEmailForm(toEmail);
        javaMailSender.send(emailForm);
        checkEmailMap.put(toEmail, authNumber);

        return authNumber;
    }

    // 인증 번호 일치 여부
    public boolean isAuthNumber(String email, int inputNumber) {
        Integer storeNumber = checkEmailMap.get(email);
        if (storeNumber == null) {
            return false;
        }
        // 인증 코드 redis 저장
        redisService.setValues(AUTH_PREFIX + toEmail, authCode);

        return storeNumber == inputNumber;
    }
}
