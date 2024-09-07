package com.finance.stockhub.user.service;

import com.finance.stockhub.exception.GlobalException;
import com.finance.stockhub.user.dto.ResponseCode;
import com.finance.stockhub.user.dto.user.UserJoinReqDto;
import com.finance.stockhub.user.dto.user.UserJoinResDto;
import com.finance.stockhub.user.entity.User;
import com.finance.stockhub.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;

    // 회원가입
    @Transactional
    public UserJoinResDto join(UserJoinReqDto userJoinReqDto) {
        // 계정 중복 체크
        userRepository.findByEmail(userJoinReqDto.getEmail()).ifPresent(it -> {
            throw new GlobalException(ResponseCode.DUPLICATED_USER_EMAIL);
        });

        // 인증번호 일치 여부
        boolean isAuthCode = mailService.isAuthNumber(userJoinReqDto.getEmail(), userJoinReqDto.getAuthCode());
        if (!isAuthCode) {
            throw new GlobalException(ResponseCode.INVALID_AUTH_CODE);
        }

        // password 암호화
        String encodedPassword = passwordEncoder.encode(userJoinReqDto.getPassword());

        // 회원가입
        User user = userRepository.save(userJoinReqDto.toEntity(encodedPassword));

        return new UserJoinResDto(user);
    }
}
