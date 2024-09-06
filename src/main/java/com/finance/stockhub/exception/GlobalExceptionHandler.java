package com.finance.stockhub.exception;

import com.finance.stockhub.user.dto.ResponseCode;
import com.finance.stockhub.user.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(GlobalException.class)
    public ResponseDto<ErrorResponse> handleGlobalException(GlobalException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseDto.fail(ErrorResponse.of(e.getResponseCode()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseDto<ErrorResponse> handleGlobalException(RuntimeException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseDto.fail(ErrorResponse.of(ResponseCode.SYSTEM_ERROR));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDto<ErrorResponse> handleGlobalException(IllegalArgumentException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseDto.fail(ErrorResponse.of(ResponseCode.DATABASE_ERROR));
    }
}
