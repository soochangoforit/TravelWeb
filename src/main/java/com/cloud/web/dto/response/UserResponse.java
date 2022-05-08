package com.cloud.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {

    private Long db_id;
    private String name;
    private String nickname;
    private String email;

    @Builder
    public UserResponse(Long db_id, String name, String nickname, String email) {
        this.db_id = db_id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }
}
