package com.finance.stockhub.user.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLoginResDto {

    private String email;
    private String token;

    public static UserLoginResDto of(String email, String token) {
        return new UserLoginResDto(email, token);
    }
}
