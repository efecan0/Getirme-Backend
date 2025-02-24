package com.example.getirme.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private String status;

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
        this.status = errorMessage.getMessageType().getCode();
    }
}
