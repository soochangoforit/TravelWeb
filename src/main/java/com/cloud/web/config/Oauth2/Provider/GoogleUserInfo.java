package com.cloud.web.config.Oauth2.Provider;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo{

    private Map<String , Object> attributes; // getAttributes

    public GoogleUserInfo(Map<String , Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "google";
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
