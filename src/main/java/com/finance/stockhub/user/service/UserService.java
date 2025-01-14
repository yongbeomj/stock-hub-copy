package com.finance.stockhub.user.service;

import com.finance.stockhub.config.auth.CustomUserDetails;
import com.finance.stockhub.config.auth.CustomUserDetailsService;
import com.finance.stockhub.config.jwt.JwtTokenProvider;
import com.finance.stockhub.exception.GlobalException;
import com.finance.stockhub.user.dto.ResponseCode;
import com.finance.stockhub.user.dto.user.UserJoinReqDto;
import com.finance.stockhub.user.dto.user.UserJoinResDto;
import com.finance.stockhub.user.dto.user.UserLoginReqDto;
import com.finance.stockhub.user.dto.user.UserLoginResDto;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public UserJoinResDto join(UserJoinReqDto userJoinReqDto) {
//        checkDuplicatedEmail(userJoinReqDto.getEmail());
//        isVerifiedCode(userJoinReqDto.getEmail(), userJoinReqDto.getAuthCode());

        String encodedPassword = encodePassword(userJoinReqDto.getPassword());
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

    public UserLoginResDto login(UserLoginReqDto userLoginReqDto) {
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userLoginReqDto.getEmail());

        if (!passwordEncoder.matches(userLoginReqDto.getPassword(), userDetails.getPassword())) {
            throw new GlobalException(ResponseCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.create(userDetails);

        return UserLoginResDto.of(userLoginReqDto.getEmail(), accessToken);
    }
}
