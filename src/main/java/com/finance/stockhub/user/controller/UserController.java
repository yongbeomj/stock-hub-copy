package com.finance.stockhub.user.controller;

import com.finance.stockhub.exception.GlobalException;
import com.finance.stockhub.user.dto.ResponseCode;
import com.finance.stockhub.user.dto.ResponseDto;
import com.finance.stockhub.user.dto.user.EmailCheckReqDto;
import com.finance.stockhub.user.dto.user.EmailCheckResDto;
import com.finance.stockhub.user.dto.user.UserJoinReqDto;
import com.finance.stockhub.user.dto.user.UserJoinResDto;
import com.finance.stockhub.user.service.MailService;
import com.finance.stockhub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
