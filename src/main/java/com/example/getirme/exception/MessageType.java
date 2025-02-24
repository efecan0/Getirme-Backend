package com.example.getirme.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    NO_RECORD_EXIST("404", "No record found."),
    GENERAL_ERROR("500", "An unexpected error occurred."),
    BAD_REQUEST("400", "Invalid request."),
    UNAUTHORIZED("401", "Unauthorized access."),
    FORBIDDEN("403", "Access denied.");


    private String code;
    private String message;

    MessageType(String code , String message){
        this.code = code;
        this.message = message;
    }
}
