package com.Login.Email.Exception.JWT;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {//Utilizado para Token inválido/caducado/usado
    public InvalidTokenException(String message) {
        super(message);
    }
}