package com.cloud.web.controller;

import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.domain.FoodBoard;
import com.cloud.web.domain.FoodType;
import com.cloud.web.domain.LocationType;
import com.cloud.web.dto.request.FoodBoardSaveDto;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.repository.FoodBoardRepository;
import com.cloud.web.repository.FoodTypeRepository;
import com.cloud.web.repository.LocationTypeRepository;
import com.cloud.web.service.FoodBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class FoodController {

    @Autowired
    private FoodBoardRepository foodBoardRepository;
    @Autowired
    private FoodTypeRepository foodTypeRepository;
    @Autowired
    private LocationTypeRepository locationTypeRepository;
    @Autowired
    private FoodBoardService foodBoardService;



    /**
     * 행사 게시판 배너 클릭시 들어가는 URL controller
     * @param  model
     * @return FoodRepository에서 가져온 결과를 list를 통해서 model에 담는다.
     */
    @GetMapping("/foods")
    public String foodBoardList(Model model){
        List<FoodBoard> foodBoards = foodBoardRepository.findAll();
        model.addAttribute("foodBoards", foodBoards);

        return "foodBoard/list";
    }

    /**
     * 맛집 게시글을 등록하기 위한 Form을 가져오는 method
     * 해당 form에 앞서 DB에 저장했던 음식Type, 위치Type을 표시하기 위해 Model에 담음
     * 하나의 게시글을 작성할 필요가 있다면 새로운 객체를 직접 전달하자
     * @param model
     * @return
     */
    @GetMapping("/foodForm")
    public String show_FoodForm(Model model){

        List<FoodType> foodTypeList = foodTypeRepository.findAll();
        List<LocationType> locationTypeList = locationTypeRepository.findAll();

        model.addAttribute("foodTypeList", foodTypeList);
        model.addAttribute("locationTypeList", locationTypeList);

        model.addAttribute("foodBoard", new FoodBoard());

        return "foodBoard/form";
    }

    /**
     * 작성한 Form을 save하기 위한 Method
     *
     * @return URL로 다시 접근하기 위해서는 redirect 넣어준다. 그렇게 안하면 html 찾는다.
     */
    @PostMapping("/foodForm")
    public String save_FoodForm(@ModelAttribute FoodBoardSaveDto foodBoardDto , Authentication authentication){


        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();
        Long user_db_id = user.getUser().getDb_id();

        log.info(user_db_id.toString());

        FoodBoard saved = foodBoardService.save(user_db_id, foodBoardDto);

        return "redirect:/foods";
    }




}
