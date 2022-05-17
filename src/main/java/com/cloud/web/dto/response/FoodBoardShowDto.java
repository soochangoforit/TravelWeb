package com.cloud.web.dto.response;

import com.cloud.web.domain.Attachment;
import com.cloud.web.domain.FoodType;
import com.cloud.web.domain.LocationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class FoodBoardShowDto {

    private Long id;
    private String locationType;
    private String foodType;
    private String title;
    private String preview;
    private String address;
    private String info;
    private double rate;

    private List<Attachment> attachedFiles;

    @Builder
    public FoodBoardShowDto(Long id, String locationType, String foodType, String title,
                            String preview, String address, String info, double rate,
                            List<Attachment> attachedFiles) {
        this.id = id;
        this.locationType = locationType;
        this.foodType = foodType;
        this.title = title;
        this.preview = preview;
        this.address = address;
        this.info = info;
        this.rate = rate;
        this.attachedFiles = attachedFiles;
    }
}
