package com.finance.stockhub.exception;

import com.finance.stockhub.user.dto.ResponseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private ResponseCode responseCode;
    private String message;

    @Builder
    public GlobalException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.message = responseCode.getMessage();
    }
}
