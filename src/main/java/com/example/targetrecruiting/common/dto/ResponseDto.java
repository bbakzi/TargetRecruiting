package com.example.targetrecruiting.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@AllArgsConstructor(staticName = "set")
public class ResponseDto<T> implements Serializable {
    private HttpStatus httpStatus;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private transient T data;

    public static <T> ResponseDto<T> setSuccess(HttpStatus statusCode, String message, T data) {
        return ResponseDto.set(statusCode, message, data);
    }

    public static <T> ResponseDto<T> setSuccess(HttpStatus statusCode, String message) {
        return ResponseDto.set(statusCode, message, null);
    }
}
