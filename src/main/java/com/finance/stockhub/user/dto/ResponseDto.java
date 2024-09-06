package com.finance.stockhub.user.dto;

import lombok.Getter;

@Getter
public class ResponseDto<T> {

    private String code;
    private String message;
    private T data;

    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> success() {
        return new ResponseDto<>("0000", "success", null);
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>("0000", "success", data);
    }

    public static <T> ResponseDto<T> fail(T data) {
        return new ResponseDto<>("9999", "fail", data);
    }

}
