package com.mysql.jwt.api.data;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserData implements ApiResult{

    private String accessToken;

}
