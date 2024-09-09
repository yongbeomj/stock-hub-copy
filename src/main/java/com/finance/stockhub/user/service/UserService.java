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
    private static final String AUTH_PREFIX = "authcode:";

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RedisService redisService;

    // 회원가입
    @Transactional
    public UserJoinResDto join(UserJoinReqDto userJoinReqDto) {
        // 계정 중복 체크
        checkDuplicatedEmail(userJoinReqDto.getEmail());

        // 인증번호 일치 여부
        isVerifiedCode(userJoinReqDto.getEmail(), userJoinReqDto.getAuthCode());

        // password 암호화
        String encodedPassword = encodePassword(userJoinReqDto.getPassword());

        // 회원가입
        User user = userRepository.save(userJoinReqDto.toEntity(encodedPassword));

        return new UserJoinResDto(user);
    }

    public void checkDuplicatedEmail(String email) {
        userRepository.findByEmail(email).ifPresent(it -> {
            throw new GlobalException(ResponseCode.DUPLICATED_USER_EMAIL);
        });
    }

    public void isVerifiedCode(String email, String inputAuthCode) {
        String redisAuthCode = redisService.getValues(AUTH_PREFIX + email);

        if (redisAuthCode == null) {
            throw new GlobalException(ResponseCode.INVALID_AUTH_CODE);
        }

        if (!redisAuthCode.equals(inputAuthCode)) {
            throw new GlobalException(ResponseCode.INCORRECT_AUTH_CODE);
        }
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
