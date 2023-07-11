package com.mysql.jwt.api.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class LoginUserReq {

    private String email;
    private String password;
}
