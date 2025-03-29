package com.example.getirme.handler;

import com.example.getirme.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<ApiError> baseExceptionHandler(BaseException exception , WebRequest request){
        ApiError apiError = createApiError(exception.getMessage() , exception.getStatus() , request);
        return ResponseEntity.status(Integer.parseInt(exception.getStatus())).body(apiError);
    }


    private <E> ApiError<E> createApiError(E message , String status , WebRequest request){
        ApiError<E> apiError = new ApiError();
        apiError.setStatus(status);
        Exception<E> exception = new Exception();
        exception.setMessage(message);
        exception.setDate(new Date());
        exception.setPath(request.getDescription(false).substring(4));
        exception.setHostName(getHostname());
        apiError.setException(exception);
        return apiError;
    }


    private String getHostname(){
        try{
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
