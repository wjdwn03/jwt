package com.mysql.jwt.exception;

import com.mysql.jwt.api.response.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final String FAIL = "fail";

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseDTO handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());

        return ResponseDTO.builder().status(FAIL).message(e.getMessage()).build();
    }

    @ExceptionHandler(JwtExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseDTO handleJwtExpiredException(JwtExpiredException e) {
        log.error(e.getMessage());

        return ResponseDTO.builder()
                .status(FAIL)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(TokenValidFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDTO handleTokenValidFailedException(TokenValidFailedException e) {
        log.error(e.getMessage());
        return ResponseDTO.builder()
                .status(FAIL)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseDTO handleUnAuthorizedException(UnAuthorizedException e) {
        log.error(e.getMessage());
        return ResponseDTO.builder()
                .status(FAIL)
                .message(e.getMessage())
                .build();
    }
}
