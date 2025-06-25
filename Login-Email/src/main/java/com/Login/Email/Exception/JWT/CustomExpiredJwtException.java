package com.Login.Email.Exception.JWT;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomExpiredJwtException extends RuntimeException {
    public CustomExpiredJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}