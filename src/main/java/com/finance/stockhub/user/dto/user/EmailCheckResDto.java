package com.finance.stockhub.user.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailCheckResDto {

    private int authCode;

    public static EmailCheckResDto of(int authCode) {
        return new EmailCheckResDto(authCode);
    }
}
