package com.cloud.web.repository;

import com.cloud.web.domain.FoodBoard;
import com.cloud.web.dto.request.FoodBoardCondition;
import com.cloud.web.dto.response.FoodBoardShowDto;

import java.util.List;

public interface FoodBoardRepositoryCustom {

    List<FoodBoard> search(FoodBoardCondition condition);

}
