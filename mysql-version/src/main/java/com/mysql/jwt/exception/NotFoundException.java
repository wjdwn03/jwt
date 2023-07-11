package com.mysql.jwt.exception;

import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(){
        super("해당 정보를 찾을 수 없습니다.");
    }

    public NotFoundException(String msg){
        super(msg);
    }
}
