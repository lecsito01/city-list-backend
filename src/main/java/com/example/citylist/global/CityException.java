package com.example.citylist.global;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityException extends RuntimeException {

    private final String message;
    private Throwable cause;
    private String detailedMessage;

    public CityException(String message, String detailedMessage, Throwable cause) {
        super(message, cause);
        this.detailedMessage = detailedMessage;
        this.cause = cause;
        this.message = message;
    }

    public CityException(String message) {
        super(message);
        this.message = message;
    }
}
