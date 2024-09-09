package com.finance.stockhub.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.stockhub.exception.GlobalException;
import com.finance.stockhub.user.dto.ResponseCode;
import com.finance.stockhub.user.dto.user.UserJoinReqDto;
import com.finance.stockhub.user.dto.user.UserJoinResDto;
import com.finance.stockhub.user.entity.User;
import com.finance.stockhub.user.entity.UserRole;
import com.finance.stockhub.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String AUTH_PREFIX = "authcode:";

    @InjectMocks
    private UserService userService;

    @Mock
    private RedisService redisService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Spy
    private ObjectMapper om;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @DisplayName("회원가입")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class join_test {

        @DisplayName("회원가입 실패 - 이메일 중복")
        @Order(1)
        @Test
        public void duplicate_email_test() throws Exception {
            // given
            String email = "test@example.com";

            UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
            userJoinReqDto.setEmail(email);
            userJoinReqDto.setPassword("qweva1234");
            userJoinReqDto.setName("홍길동1");
            userJoinReqDto.setAuthCode("123456");

            User mockUser = newMockUser(userJoinReqDto);
            given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));

            // when
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> userService.join(userJoinReqDto));

            String responseBody = om.writeValueAsString(exception);
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            log.info("method : {}, response : {}", methodName, responseBody);

            // then
            assertThat(exception.getResponseCode()).isEqualTo(ResponseCode.DUPLICATED_USER_EMAIL);
        }

        @DisplayName("회원가입 실패 - 인증번호 입력하지 않은 경우")
        @Order(2)
        @Test
        public void authcode_null_test() throws Exception {
            // given
            String email = "test@example.com";

            UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
            userJoinReqDto.setEmail(email);
            userJoinReqDto.setPassword("qweva1234");
            userJoinReqDto.setName("홍길동1");

            // stub
            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            // when
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> userService.join(userJoinReqDto));

            String responseBody = om.writeValueAsString(exception);
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            log.info("method : {}, response : {}", methodName, responseBody);

            // then
            assertThat(exception.getResponseCode()).isEqualTo(ResponseCode.INVALID_AUTH_CODE);
        }

        @DisplayName("회원가입 실패 - 인증번호 일치하지 않는 경우")
        @Order(3)
        @Test
        public void authcode_mismatch_test() throws Exception {
            // given
            String email = "test@example.com";
            String authCode = "123456";

            UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
            userJoinReqDto.setEmail(email);
            userJoinReqDto.setPassword("qweva1234");
            userJoinReqDto.setName("홍길동1");
            userJoinReqDto.setAuthCode(authCode);

            // stub 1
            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            // stub 2
            given(redisService.getValues(AUTH_PREFIX + email)).willReturn("999999");

            // when
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> userService.join(userJoinReqDto));

            String responseBody = om.writeValueAsString(exception);
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            log.info("method : {}, response : {}", methodName, responseBody);

            // then
            assertThat(exception.getResponseCode()).isEqualTo(ResponseCode.INCORRECT_AUTH_CODE);
        }

        @DisplayName("회원가입 성공")
        @Order(4)
        @Test
        public void join_success() throws Exception {
            // given
            String email = "test@example.com";
            String authCode = "123456";

            UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
            userJoinReqDto.setEmail(email);
            userJoinReqDto.setPassword("qweva1234");
            userJoinReqDto.setName("홍길동1");
            userJoinReqDto.setAuthCode(authCode);

            // stub 1
            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            // stub 2
            given(redisService.getValues(AUTH_PREFIX + email)).willReturn(authCode);

            // stub 3
            User mockUser = newMockUser(userJoinReqDto);
            given(userRepository.save(any(User.class))).willReturn(mockUser);

            // when
            UserJoinResDto userJoinResDto = userService.join(userJoinReqDto);

            String responseBody = om.writeValueAsString(userJoinResDto);
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            log.info("method : {}, response : {}", methodName, responseBody);

            // then
            assertThat(userJoinResDto.getEmail()).isEqualTo(email);
        }
    }

    private User newMockUser(UserJoinReqDto userJoinReqDto) {
        String encodePassword = userService.encodePassword(userJoinReqDto.getPassword());

        return User.builder()
                .email(userJoinReqDto.getEmail())
                .password(encodePassword)
                .name(userJoinReqDto.getName())
                .role(UserRole.USER)
                .build();
    }

}