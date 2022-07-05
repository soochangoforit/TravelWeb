package com.cloud.web.config.Oauth2.Provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{

    // naver에서 제공해주는 프로필의 정보는 response에서 받아온다.
    // id (sub) , email , name 같은 것들이다.
    // scope가 name,email만 받아서 그렇다.

    private final Map<String , Object> attributes; // getAttributes {id=..., email=..., name=...}

    public NaverUserInfo(Map<String , Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() { // 사용자 실제 이름 ex) 이수찬
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
