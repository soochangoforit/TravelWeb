package com.cloud.web.dto.response;

import com.cloud.web.domain.FoodBoard;
import com.cloud.web.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserResponse implements Serializable {

    private Long db_id;

    private String loginId; // 사용자 로그인 아이디

    private String password;
    private String name;
    private String nickname;
    private String email;
    private Role role;

    private String provider; // 소셜로그인 제공자 이름

    private String providerId; // 소셜로그인에서 제공 받은 sub(여러 자리의 숫자)
    private List<FoodBoard> foodBoardList;


    @Builder
    public UserResponse(Long db_id, String loginId, String password, String name, String nickname,
                        String email, Role role,String provider, String providerId , List<FoodBoard> foodBoardList) {
        this.db_id = db_id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.foodBoardList = foodBoardList;
    }
}
