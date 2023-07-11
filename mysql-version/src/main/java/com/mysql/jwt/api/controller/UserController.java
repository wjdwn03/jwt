package com.mysql.jwt.api.controller;

import com.mysql.jwt.api.data.ApiResult;
import com.mysql.jwt.api.request.LoginUserReq;
import com.mysql.jwt.api.response.ResponseDTO;
import com.mysql.jwt.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private static final String SUCCESS = "success";
    private final UserService userService;


    @PostMapping("/guests/login/normal")
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginUserReq loginUserReq, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ApiResult loginUserResult = userService.loginUser(loginUserReq, httpServletRequest, httpServletResponse);

        ResponseDTO responseDTO = ResponseDTO.builder().status(SUCCESS).message("일반회원 로그인 성공").apiResult(loginUserResult).build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
