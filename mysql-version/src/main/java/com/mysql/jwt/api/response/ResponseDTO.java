package com.mysql.jwt.api.response;

import com.mysql.jwt.api.data.ApiResult;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseDTO {

    private String status;
    private String message;
    private ApiResult apiResult;
}
