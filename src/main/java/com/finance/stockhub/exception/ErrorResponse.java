package com.finance.stockhub.exception;

import com.finance.stockhub.user.dto.ResponseCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final HttpStatus httpStatus;
    private final String message;
    private final LocalDateTime timestamp;

    @Builder
    public ErrorResponse(HttpStatus httpStatus, String message, LocalDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static ErrorResponse of(ResponseCode responseCode) {
        return ErrorResponse.builder()
                .httpStatus(responseCode.getHttpStatus())
                .message(responseCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
