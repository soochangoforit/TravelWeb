package com.cloud.web.domain.enums;

public enum RoleType {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String grantedAuthority;

    RoleType(String grantedAuthority) {
        this.grantedAuthority = grantedAuthority;
    }

    public String getAuthority() {
        return grantedAuthority;
    }
}
