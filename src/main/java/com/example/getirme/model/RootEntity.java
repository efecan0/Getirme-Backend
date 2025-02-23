package com.example.getirme.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RootEntity<T> {
    private boolean success;
    private String errorMessage;
    private T data;

    public static <T> RootEntity<T> ok(T data){
        return new RootEntity(true , null , data);
    }

    public static <T> RootEntity<T> error(String message){
        return new RootEntity(false , message , null);
    }

}
