package com.finance.stockhub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.stockhub.user.dto.user.UserJoinReqDto;
import com.finance.stockhub.user.entity.User;
import com.finance.stockhub.user.entity.UserRole;
import com.finance.stockhub.user.repository.UserRepository;
import com.finance.stockhub.user.service.RedisService;
import com.finance.stockhub.user.service.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("User Controller Test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class UserControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String AUTH_PREFIX = "authcode:";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        userRepository.save(newMockUser());
        em.clear();
    }

    @DisplayName("[POST] 회원가입 성공")
    @Test
    public void join_success_test() throws Exception {
        // given
        UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
        userJoinReqDto.setEmail("test@example.com");
        userJoinReqDto.setPassword("qweva1234");
        userJoinReqDto.setName("홍길동1");
        userJoinReqDto.setAuthCode("123456");

        String requestbody = om.writeValueAsString(userJoinReqDto);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.info("method : {}, request : {}", methodName, requestbody);

        redisService.setValues(AUTH_PREFIX + userJoinReqDto.getEmail(), "123456");

        // when
        ResultActions resultActions =
                mvc.perform(post("/api/v1/users/join")
                        .content(requestbody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("method : {}, response : {}", methodName, responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    private User newMockUser() {
        String encodePassword = userService.encodePassword("asdf1234");

        return User.builder()
                .email("test@example.com")
                .password(encodePassword)
                .name("홍길동")
                .role(UserRole.USER)
                .build();
    }

}