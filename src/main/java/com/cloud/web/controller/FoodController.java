package com.cloud.web.controller;

import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.domain.*;
import com.cloud.web.domain.enums.AttachmentType;
import com.cloud.web.dto.request.FoodBoardPostDto;
import com.cloud.web.dto.request.FoodBoardPostFormDto;
import com.cloud.web.dto.request.FoodCmtDto;
import com.cloud.web.dto.response.FoodBoardShowDto;
import com.cloud.web.repository.*;
import com.cloud.web.service.FoodBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FoodCmtRepository foodCmtRepository;

    @Autowired
    private FileStore fileStore;



    /**
     * 행사 게시판 배너 클릭시 들어가는 URL controller , 맛집 게시글의 첫 페이지
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
     * 맛집 게시글 제목을 클릭하면 해당 게시글 상세 페이지로 이동
     */
    @GetMapping("/foods/{id}")
    public String show_FoodBoard_Result(@PathVariable Long id, Model model){

        FoodBoardShowDto foodBoard = foodBoardService.show(id); // id에 해당하는 게시글의 상세 정보를 보여주기 위해서 데이터 가져온다.

        List<FoodCmt> foodCmts = foodBoardRepository.findById(id).get().getFoodCmts(); // id에 해당하는 맛집 게시글의 댓글 목록 가져온다.

        model.addAttribute("foodBoard" , foodBoard); // id로 게시글 조회시 나타나는 게시글 정보를 담고 있다.
        model.addAttribute("foodCmtDto", new FoodCmtDto()); // 해당 id 게시글에서 댓글 작성하기 위한 Dto ( String 데이터 형태인 cmt만 가지고 있다. )를 제공해준다.
        model.addAttribute("foodCmts", foodCmts); // id로 게시글 조회시 하단에 나타나는 기존의 댓글 목록을 출력하기 위해 model에 제공

        return "foodBoard/detailsPage";
    }

    /**
     *  맛집 게시글 제목을 클릭하면 해당 게시글 상세 페이지로 이동 해서 댓글 작성을 원할때
     *  댓글을 작성하기 위해서 POST 방식으로 들어온다.
     *
     *  첫번째 파라미터 : 맛집 게시글의 db_id
     *  두번재 파라미터 : 앞서 상세 게시글을 조회하는 순간 댓글도 입력 받기 위한 FoodCmtDto 인스턴스 객체가 생성되었다.
     *  세번째 파라미터 : 해당 게시글에서 어떤 로그인한 사용자가 댓글을 달았는지 알기 위해서 Authentication 를 사용한다.
     */
    @Secured("ROLE_USER")
    @PostMapping("/foods/{id}")
    public String write_FoodBoard_Cmt(@PathVariable Long id, @ModelAttribute FoodCmtDto foodCmtDto , Authentication authentication){

        // 어떤 로그인한 사용자가 댓글을 작성했는지 알기 위해서 생성 (댓글 기능은 로그인한 사람만 이용 가능하다)
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        Long user_db_id = userDetails.getUser().getDb_id();

        log.info(user_db_id.toString()); // 제대로 가져왔는지 확인하기 위해서 Log 찍음

        // id에 해당하는 foodBoard 게시글에서 작성한 댓글을 저장하기 위한 메서드
        foodBoardService.saveFoodCmt(user_db_id, id, foodCmtDto);

        String aa = String.valueOf(id);

        return "redirect:/foods/" + aa;

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

        // model.addAttribute("foodBoard", new FoodBoard());
        model.addAttribute("foodBoard", new FoodBoardPostFormDto()); // 오로지 웹 정보만 받도록 하는 dto 생성 후 반환

        return "foodBoard/form";
    }

    /**
     * 작성한 Form을 save하기 위한 Method
     *
     * @return URL로 다시 접근하기 위해서는 redirect 넣어준다. 그렇게 안하면 html 찾는다.
     */
    @PostMapping("/foodForm")
    public String save_FoodForm(@ModelAttribute FoodBoardPostFormDto foodBoardDto , Authentication authentication) throws IOException {


        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        Long user_db_id = userDetails.getUser().getDb_id();

        log.info(user_db_id.toString());

        User user = userRepository.findById(user_db_id).orElse(null);

        FoodBoardPostDto boardPostDto = foodBoardDto.createBoardPostDto(user);

        //FoodBoard saved = foodBoardService.save(user_db_id, foodBoardDto);

        foodBoardService.post(boardPostDto); // 최종 등록 Db에 등록

        return "redirect:/foods";
    }





    @ResponseBody
    @GetMapping("/images/{filename}")
    public UrlResource processImg(@PathVariable String filename) throws MalformedURLException {

        return new UrlResource("file:" + fileStore.createPath(filename, AttachmentType.IMAGE));

    }




}
