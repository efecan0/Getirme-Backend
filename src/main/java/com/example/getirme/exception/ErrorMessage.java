package com.example.getirme.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private MessageType messageType;
    private String staticMessage;


    public String prepareErrorMessage(){
        StringBuilder builder = new StringBuilder();
        builder.append(messageType.getMessage());
        if(staticMessage != null){
            builder.append(" : " + staticMessage);
        }
        return builder.toString();
    }

}
