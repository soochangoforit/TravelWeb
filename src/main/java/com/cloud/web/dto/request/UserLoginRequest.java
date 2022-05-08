package com.cloud.web.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class UserLoginRequest {

    private String username; // 아이디
    private String password; // 비밀번호
    private String name;
    private String nickname;
    private String email;


}
