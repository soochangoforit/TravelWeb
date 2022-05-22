package com.cloud.web.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class FoodBoardCondition {

    // 맛집 제목으로 검색
    private String title;
    // 맛집 지역으로 검색
    private Long locationType_Id;
    // 맛집 음식 유형으로 검색
    private Long foodType_id;
    // 글쓴이의 평점 순으로 정렬
    private Integer rate;


    @Builder
    public FoodBoardCondition(String title, Long locationType_Id, Long foodType_id ,Integer rate ) {
        this.title = title;
        this.locationType_Id = locationType_Id;
        this.foodType_id = foodType_id;
        this.rate = rate;
    }

}
