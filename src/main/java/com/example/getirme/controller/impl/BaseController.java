package com.example.getirme.controller.impl;

import com.example.getirme.model.RootEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public class BaseController {

    public <T> ResponseEntity<RootEntity<T>> ok(T data){
        return ResponseEntity.status(HttpStatus.OK).body(RootEntity.ok(data));
    }

    public <T> ResponseEntity<RootEntity<T>> error(String message , HttpStatus httpStatus){
        return ResponseEntity.status(httpStatus).body(RootEntity.error(message));
    }


}
