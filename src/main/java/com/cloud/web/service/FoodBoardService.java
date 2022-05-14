package com.cloud.web.service;

import com.cloud.web.domain.FoodBoard;
import com.cloud.web.domain.User;
import com.cloud.web.dto.request.FoodBoardSaveDto;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.repository.FoodBoardRepository;
import com.cloud.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FoodBoardService {

    @Autowired
    private FoodBoardRepository foodBoardRepository;
    @Autowired
    private UserRepository userRepository;

    public FoodBoard save(Long user_db_id , FoodBoardSaveDto foodBoardDto){

        User user = userRepository.findById(user_db_id).get();

        FoodBoard saveFoodBoard = FoodBoard.builder().user(user)
                .locationType(foodBoardDto.getLocationType())
                .foodType(foodBoardDto.getFoodType())
                .title(foodBoardDto.getTitle())
                .preview(foodBoardDto.getPreview())
                .address(foodBoardDto.getAddress())
                .info(foodBoardDto.getInfo())
                .rate(foodBoardDto.getRate()).build();

        FoodBoard saved = foodBoardRepository.save(saveFoodBoard);
        return saved;
    }

}
