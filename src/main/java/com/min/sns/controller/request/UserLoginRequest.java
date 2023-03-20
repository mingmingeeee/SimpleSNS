package com.min.sns.controller.request;

// controller에서 사용하는 request들은 controller안에

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginRequest {

    private String name;
    private String password;

}
