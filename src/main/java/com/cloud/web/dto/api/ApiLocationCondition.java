package com.cloud.web.dto.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ApiLocationCondition {
    private String location;

    @Builder
    public ApiLocationCondition(String location) {
        this.location = location;
    }
}
