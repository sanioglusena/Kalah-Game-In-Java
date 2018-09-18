package com.example.java.KalahApplication.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class KalahException extends RuntimeException {

    public KalahException(String message) {
        super(message);
    }
}
