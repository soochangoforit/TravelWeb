package com.cloud.web.controller;

import com.cloud.web.dto.api.ApiBoard;
import com.cloud.web.dto.api.ApiLocationCondition;
import com.cloud.web.service.AttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class AttractionController {

    private AttractionService attractionService;

    @Autowired
    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    /**
     * 맨처음 /attractions으로 들어오면 전체 부산 명소 list가 보여지게 된다. (114개 중 100개 조회)
     * @param model 전체 명소 게시글 목록, 명소 위치별 값, 검색 하기 위한 dto
     * @return 부산 명소 첫 페이지 이동, 전체 목록이 보여진다.
     * @author LEE SOO CHAN
     */
    @GetMapping("/attractions")
    public String attractionsPage(Model model ) {

        if(AttractionService.apiBoards.size() == 0) {
            attractionService.callApiWithJson("100", "1");
        }

        ArrayList<ApiBoard> apiBoards = AttractionService.apiBoards;
        ArrayList<String> locationKeys = AttractionService.keys;

        // 특정한 지역으로 찾고자 할때
        model.addAttribute("apiBoards", apiBoards);
        model.addAttribute("locationKeys", locationKeys);
        model.addAttribute("ApiLocationCondition", new ApiLocationCondition());

        return "attraction/list";

    }

    /**
     * 검색 조건을 가지고 검색할때 들어오는 controller , 기본값은 전체 조회이며
     * 맨 처음 GET /attractions으로 들어오고 나서는 , 검색 조건 유무에 상관없이
     * 해당 POST /attractions으로 들어온다.
     *
     * @param condition 검색 조건이 바인딩 되어 값이 할당 된다.
     * @return 검색 조건이 반영된 부산 명소 전체 목록 페이지로 이동한다.
     * @author LEE SOO CHAN
     */
    @PostMapping("/attractions")
    public String locationPage(ApiLocationCondition condition , Model model ) {


        if(condition.getLocation().equals("")){
            ArrayList<ApiBoard> apiBoards = AttractionService.apiBoards;
            model.addAttribute("apiBoards", apiBoards); // 전체 게시글 보여준다.
            model.addAttribute("ApiLocationCondition", new ApiLocationCondition());

        }else{
            String location = condition.getLocation();
            List<ApiBoard> apiBoards = AttractionService.map.get(location);
            model.addAttribute("apiBoards", apiBoards);
            model.addAttribute("ApiLocationCondition", condition);

        }

        ArrayList<String> locationKeys = AttractionService.keys;
        model.addAttribute("locationKeys", locationKeys);

        return "attraction/list";

    }


    /**
     * 특정 부산 명소 게시글의 상세 정보를 보기 위해서 이동한다.
     *
     * @param id 해당 명소 게시글의 고유한 id (un_seq)
     * @param conditionResult 검색 조건
     * @param model 상세 페이지 들어갈때도 검색 조건을 가지고 있는 상태로 들어가기 위해서 값을 넣어준다.
     * @return 부산 명소 게시글의 상세 페이지
     * @author LEE SOO CHAN
     */
    @GetMapping("/attractions/{id}")
    public String detailPage(@PathVariable("id") String id ,
            @RequestParam(value = "condition" , required = false) String conditionResult, Model model) throws IOException {

        ApiBoard apiBoard = attractionService.callDetailPage(id); // 상세 게시글을 요청할때는 직접 un_seq를 가지고 서버로 호출

        ApiLocationCondition condition = new ApiLocationCondition(conditionResult);

        model.addAttribute("apiBoard" , apiBoard);
        model.addAttribute("ApiLocationCondition", condition); //없으면 ""으로 담긴다.

        return "attraction/detailPage";
    }

}
