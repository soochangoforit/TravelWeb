package com.cloud.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class DuplicateMessageDto {

    Boolean isDuplicate;

    public DuplicateMessageDto(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }
}
