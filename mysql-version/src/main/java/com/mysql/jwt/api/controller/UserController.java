package com.mysql.jwt.api.controller;

import com.mysql.jwt.api.data.ApiResult;
import com.mysql.jwt.api.request.LoginUserReq;
import com.mysql.jwt.api.response.ResponseDTO;
import com.mysql.jwt.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private static final String SUCCESS = "success";
    private final UserService userService;


    /**
     * 일반 회원 로그인 처리합니다.
     *
     * @param loginUserReq        사용자가 입력한 email과 password
     * @param httpServletRequest
     * @param httpServletResponse
     * @return 성공 시 로그인 처리 후 발급한 JWT를 {@code ResponseEntity}로 반환합니다.
     */
    @PostMapping("/guests/login/normal")
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginUserReq loginUserReq, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ApiResult loginUserResult = userService.loginUser(loginUserReq, httpServletRequest, httpServletResponse);

        ResponseDTO responseDTO = ResponseDTO.builder().status(SUCCESS).message("일반회원 로그인 성공").apiResult(loginUserResult).build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * access token을 재발급합니다.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return 성공 시 재발급한 access token을 {@code ResponseEntity}로 반환합니다.
     */
    @GetMapping("/users/access-token")
    public ResponseEntity<ResponseDTO> reissueAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ApiResult reissueAccessTokenResult = userService.reissueAccessToken(httpServletRequest, httpServletResponse);

        ResponseDTO responseDTO = ResponseDTO.builder().status(SUCCESS).message("access token 재발급 성공").apiResult(reissueAccessTokenResult).build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
