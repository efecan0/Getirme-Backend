package com.example.getirme.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exception<E> {
    private String hostName;
    private String path;
    private Date date;
    private E message;
}
