package com.finance.stockhub.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "[ERROR] Invalid request"),

    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "[ERROR] Invalid authentication code"),
    INCORRECT_AUTH_CODE(HttpStatus.UNAUTHORIZED, "[ERROR] Incorrect authentication code"),
    DUPLICATED_USER_EMAIL(HttpStatus.BAD_REQUEST, "[ERROR] The email already exists"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "[ERROR] User not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password"),

    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] Illegal Argument Exception"),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] Runtime Exception");

    private final HttpStatus httpStatus;
    private final String message;
}
