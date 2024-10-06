package com.finance.stockhub.user.controller;

import com.finance.stockhub.exception.GlobalException;
import com.finance.stockhub.user.dto.ResponseCode;
import com.finance.stockhub.user.dto.ResponseDto;
import com.finance.stockhub.user.dto.user.*;
import com.finance.stockhub.user.service.MailService;
import com.finance.stockhub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/join")
    public ResponseDto<UserJoinResDto> join(
            @RequestBody @Valid UserJoinReqDto userJoinReqDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResponseCode.INVALID_REQUEST);
        }

        UserJoinResDto userJoinResDto = userService.join(userJoinReqDto);
        return ResponseDto.success(userJoinResDto);
    }

    @PostMapping("/join/email-check")
    public ResponseDto<EmailCheckResDto> checkEmail(
            @RequestBody @Valid EmailCheckReqDto emailCheckReqDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResponseCode.INVALID_REQUEST);
        }

        String authCode = mailService.sendEmail(emailCheckReqDto.getEmail());
        return ResponseDto.success(new EmailCheckResDto(authCode));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid UserLoginReqDto userLoginReqDto,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResponseCode.INVALID_REQUEST);
        }

        UserLoginResDto userLoginResDto =  userService.login(userLoginReqDto);

        return ResponseEntity.ok().body(ResponseDto.success(userLoginResDto));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(

    ) {
        System.out.println("test controller");

        return ResponseEntity.ok().body(ResponseDto.success());
    }
}
