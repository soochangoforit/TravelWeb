package com.cloud.web.config.Oauth2.Provider;

import java.util.Map;

public class FacebookUserInfo implements OAuth2UserInfo{

    // facebook에서 제공해주는건 id (구글 sub이랑 같은 역할) , name , email 같은 것들이다.

    private Map<String , Object> attributes; // getAttributes

    public FacebookUserInfo(Map<String , Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "facebook";
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
