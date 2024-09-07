package com.finance.stockhub.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "[ERROR] Invalid request"),

    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] Illegal Argument Exception"),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] Runtime Exception");

    private final HttpStatus httpStatus;
    private final String message;
}
