package com.cloud.web.controller;

import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.domain.*;
import com.cloud.web.domain.enums.AttachmentType;
import com.cloud.web.dto.request.FoodBoardCondition;
import com.cloud.web.dto.request.FoodBoardPostDto;
import com.cloud.web.dto.request.FoodBoardPostFormDto;
import com.cloud.web.dto.request.FoodCmtDto;
import com.cloud.web.dto.response.FoodBoardShowDto;
import com.cloud.web.repository.*;
import com.cloud.web.service.FoodBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Controller
public class FoodController {

    private FoodBoardRepository foodBoardRepository;
    private FoodTypeRepository foodTypeRepository;
    private LocationTypeRepository locationTypeRepository;
    private FoodBoardService foodBoardService;
    private UserRepository userRepository;
    private FileStore fileStore;

    @Autowired
    public FoodController(FoodBoardRepository foodBoardRepository, FoodTypeRepository foodTypeRepository,
                          LocationTypeRepository locationTypeRepository, FoodBoardService foodBoardService,
                          UserRepository userRepository, FileStore fileStore) {
        this.foodBoardRepository = foodBoardRepository;
        this.foodTypeRepository = foodTypeRepository;
        this.locationTypeRepository = locationTypeRepository;
        this.foodBoardService = foodBoardService;
        this.userRepository = userRepository;
        this.fileStore = fileStore;
    }

    /**
     * 행사 게시판 배너 클릭시 들어가는 URL controller , 맛집 게시글의 첫 페이지로 들어가기 위한 controller
     * 페이징 처리시 해당 controller로 들어와서 처리하고 있다.
     *
     * @param  model 맛집 게시글 첫 페이지에 검색 조건에 대한 값을 넣기 위해서 condition 인스턴스 반환
     * @return FoodRepository에서 가져온 결과를 list를 통해서 model에 담는다.
     * @author LEE SOO CHAN
*/
    @GetMapping("/foods") // 로그인 -> 행사 게시판
    public String foodBoardList(Model model , @PageableDefault(size = 4) Pageable pageable){

        Page<FoodBoard> foodBoards = foodBoardRepository.findAll(pageable);

        int startPage = Math.max( 1 , foodBoards.getPageable().getPageNumber() - 4);
        int endPage = Math.min( foodBoards.getTotalPages() == 0 ? 1 : foodBoards.getTotalPages() , foodBoards.getPageable().getPageNumber() + 4);
        model.addAttribute("totalPages", foodBoards.getTotalPages() == 0 ? 1 : foodBoards.getTotalPages());

        List<FoodType> foodTypeList = foodTypeRepository.findAll();
        List<LocationType> locationTypeList = locationTypeRepository.findAll();

        model.addAttribute("foodBoards", foodBoards);
        model.addAttribute("foodTypeList", foodTypeList);
        model.addAttribute("locationTypeList", locationTypeList);
        model.addAttribute("condition" , new FoodBoardCondition());

        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);

        return "foodBoard/list"; // 맛집 게시글의 전체 리스트를 보여주는 페이지로 이동
    }


    /**
     *  맨 처음 /foods로 들어오면 해당 검색 조건에 값을 입력할 수 있도록 인스턴스를 넘겨준다.
     *  넘겨 받은 인스턴스에 값을 할당하고 "검색"시 해당 controller로 들어온다.
     *  검색한 결과에서 상세 보기를 클릭하고 , 뒤로 돌아올시 해당 controller condition에 앞서 검색한 조건이 그대로 담겨진다.
     *
     * @param condition 웹에서 온 검색 조건이 바인딩 된다.
     * @return 검색 조건이 반영된, 맛집 게시글 리스트 목록으로 이동한다
     * @author LEE SOO CHAN
     */
    @PostMapping("/foods")
    public String foodBoardConditionList(FoodBoardCondition condition , Model model , @PageableDefault(size = 4) Pageable pageable){

        Page<FoodBoard> foodBoards = foodBoardRepository.searchPageSimple(condition,pageable);

        int startPage = Math.max( 1 , foodBoards.getPageable().getPageNumber() - 4);
        int endPage = Math.min( foodBoards.getTotalPages() == 0 ? 1 : foodBoards.getTotalPages() , foodBoards.getPageable().getPageNumber() + 4);
        model.addAttribute("totalPages", foodBoards.getTotalPages() == 0 ? 1 : foodBoards.getTotalPages());

        List<FoodType> foodTypeList = foodTypeRepository.findAll();
        List<LocationType> locationTypeList = locationTypeRepository.findAll();

        model.addAttribute("foodTypeList", foodTypeList); // 검색 조건으로 사용하기 위해서
        model.addAttribute("locationTypeList", locationTypeList); // 검색 조건으로 사용하기 위해서
        model.addAttribute("foodBoards", foodBoards);   // 검색된 결과가 들어간다.
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        model.addAttribute("condition", condition); //  앞전에 검색한 조건을 그대로 model에 넣어준다.

        return "foodBoard/list";
    }


    /**
     * 맛집 게시글 제목을 클릭하면 해당 게시글 상세 페이지로 이동
     * url에 검색 조건이 다 달려서 넘어온다.
     * @param id 특정 맛집 게시글의 고유 db_id
     * @param model 상세 페이지에서 댓글을 입력하기 위한 빈 인스턴스를 제공.
     * @return 특정 맛집 게시글의 상세 페이지로 이동한다.
     * @author LEE SOO CHAN
     */
    @GetMapping("/foods/{id}")
    public String show_FoodBoard_Result(@PathVariable Long id,
                                        @PageableDefault(size = 4) Pageable pageable,
                                        @RequestParam(value = "title" , required = false) String title,
                                        @RequestParam(value = "locationType_Id" , required = false) Long locationType_Id,
                                        @RequestParam(value = "foodType_id" , required = false) Long foodType_id,
                                        @RequestParam(value = "rate" , required = false) Integer rate,
                                        Model model  ){

        model.addAttribute("page", pageable.getPageNumber()); // 몇번쨰 페이지인지 알려줘라 details한테 숫자로 알려줘라!!!

        FoodBoardCondition condition = FoodBoardCondition.builder()
                .title(title)
                .locationType_Id(locationType_Id)
                .foodType_id(foodType_id)
                .rate(rate).build();

        FoodBoardShowDto foodBoard = foodBoardService.showByFoodBoardId(id); // id에 해당하는 게시글의 상세 정보를 보여주기 위해서 데이터 가져온다.

        // id에 해당하는 맛집 게시글의 댓글 목록 가져온다.
        // FoodBoard 안에 List로 Cmts를 가지고 있어서 getFoodCmts로 굳이 cmtsRepository를 가져오지 않더라도 가능했다.
        // 일대다 양방향 연관관계에 의해서 가능했다.
        List<FoodCmt> foodCmts = foodBoardService.showFoodCmts(id);

        model.addAttribute("foodBoard" , foodBoard); // id로 게시글 조회시 나타나는 게시글 정보를 담고 있다.
        model.addAttribute("foodCmtDto", new FoodCmtDto()); // 해당 id 게시글에서 댓글 작성하기 위한 Dto ( String 데이터 형태인 cmt만 가지고 있다. )
        model.addAttribute("foodCmts", foodCmts); // id로 게시글 조회시 하단에 나타나는 기존의 댓글 목록을 출력하기 위해 model에 제공
        model.addAttribute("condition", condition); // 상세 페이지에서 뒤로가기 누를시 검색 조건을 그대로 가져가기 위해서

        return "foodBoard/detailsPage";
    }


    /**
     *  맛집 게시글 제목을 클릭하면 해당 게시글 상세 페이지로 이동 해서 댓글 작성을 원할때
     *  댓글을 작성하기 위해서 POST 방식으로 들어온다.
     *  USER 랑 ADMIN 모두 댓글 작성 가능하다.
     *
     *  @param id  : 맛집 게시글의 db_id
     *  @param foodCmtDto: 앞서 상세 게시글을 조회하는 순간 댓글도 입력 받기 위한 FoodCmtDto 인스턴스 객체가 생성되었다.
     *                   댓글 내용이 담기고 나서 해당 파라미터로 들어온다.
     *  @param authentication  : 해당 게시글에서 어떤 로그인한 사용자가 댓글을 달았는지 알기 위해서 Authentication 를 사용한다.
     *  @return 댓글을 확인하기 위해 상세 food 게시글로 이동한다.
     *  @author LEE SOO CHAN
     */
    @Secured({"ROLE_USER" , "ROLE_ADMIN"})
    @PostMapping("/foods/{id}")
    public String write_FoodBoard_Cmt(@PathVariable Long id, @ModelAttribute FoodCmtDto foodCmtDto ,
                                      Authentication authentication){

        // 어떤 로그인한 사용자가 댓글을 작성했는지 알기 위해서 생성 (댓글 기능은 로그인한 사람만 이용 가능하다)
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        Long user_db_id = userDetails.getUser().getDb_id();

        // id에 해당하는 foodBoard 게시글에서 작성한 댓글을 저장하기 위한 메서드
        foodBoardService.saveFoodCmt(user_db_id, id, foodCmtDto);

        String boardId = String.valueOf(id);

        return "redirect:/foods/" + boardId; //redirect는 get 방식으로 접근한다.
    }



    /**
     * 맛집 게시글을 등록하기 위한 form을 가져온다.
     *
     * @param model 하나의 새로운 게시글을 작성할 필요가 있기 때문에 foodBoard를 작성할 수 있는 새로운 인스턴스를 반환
     * @return 맛집 게시글 등록 폼으로 이동한다.
     * @author LEE SOO CHAN
     */
    @GetMapping("/foodForm")
    public String show_FoodForm(Model model){

        List<FoodType> foodTypeList = foodTypeRepository.findAll();
        List<LocationType> locationTypeList = locationTypeRepository.findAll();

        model.addAttribute("foodTypeList", foodTypeList);
        model.addAttribute("locationTypeList", locationTypeList);

        //model.addAttribute("foodBoard", new FoodBoardPostFormDto()); // 오로지 웹 정보만 받도록 하는 dto 생성 후 반환

        return "foodBoard/form";
    }


    /**
     * 새롭게 작성한 맛집 게시글 폼을 등록한다.
     *
     * @param foodBoardFormDto 작성하고자 하는 맛집 게시글의 상세 정보를 담고 있다.
     * @param authentication 맛집 게시글을 작성한 사용자의 정보를 session에서 가져온다.
     * @return URL controller로 다시 접근하기 위해서는 redirect 넣어준다. redirect를 넣지 않으면 해당 String으로 된 html 파일을 찾는다.
     * @author LEE SOO CHAN
     */
    @PostMapping("/foodForm")
    public String save_FoodForm(@ModelAttribute FoodBoardPostFormDto foodBoardFormDto , Authentication authentication) throws IOException {

        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        Long user_db_id = userDetails.getUser().getDb_id();
        User user = userRepository.findById(user_db_id).orElse(null); // 로그인한 사용자의 entity 찾아준다.

        FoodBoardPostDto boardPostDto = foodBoardFormDto.createBoardPostDto(user); // user 정보를 담고 있는 FoodBoardPostDto 만든다.

        foodBoardService.post(boardPostDto); // 최종 등록 Db에 등록

        return "redirect:/foods";
    }


    /**
     * 맛집 게시글 혹은 맛집 게시글 상세 페이지에서 사진을 html에 보여주기 위해서 필요
     * @param filename /images/{파일이름}
     * @return 사진을 찾아주는 url
     * @throws MalformedURLException
     * @author LEE SOO CHAN
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public UrlResource processImg(@PathVariable String filename) throws MalformedURLException {

        return new UrlResource("file:" + fileStore.createPath(filename, AttachmentType.IMAGE));
    }

}
