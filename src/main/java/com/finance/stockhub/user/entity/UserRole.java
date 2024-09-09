package com.finance.stockhub.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN("관리자"),
    USER("유저");

    private String value;
}
