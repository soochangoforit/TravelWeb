package com.cloud.web.controller;

import com.cloud.web.domain.FoodBoard;
import com.cloud.web.dto.api.ApiBoard;
import com.cloud.web.dto.request.UserJoinRequest;
import com.cloud.web.dto.response.DuplicateMessageDto;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.service.AttractionService;
import com.cloud.web.service.FoodBoardService;
import com.cloud.web.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 프로젝트 Main Page에 대한 Controller
 */
@Slf4j
@Controller
public class IndexController {

    private final UserService userService;
    private final FoodBoardService foodBoardService;
    private final AttractionService attractionService;

    @Autowired
    public IndexController(UserService userService , FoodBoardService foodBoardService , AttractionService attractionService) {
        this.userService = userService;
        this.foodBoardService = foodBoardService;
        this.attractionService = attractionService;

    }

    /**
     *  Main Page 접근
     */
    @GetMapping("/")
    public  String index(Model model){

        if(AttractionService.apiBoards.size() == 0) {
            attractionService.callApiWithJson("100", "1");
        }

        List<FoodBoard> foodBoards = foodBoardService.findByRateDescLimit(6);

        // 맛집 게시글 5개
        model.addAttribute("foodBoards", foodBoards); // map ( key - value )

        ArrayList<ApiBoard> attractions = new ArrayList<>();

        for(int i = 0; i < 5 ; i++){
            attractions.add(AttractionService.apiBoards.get(i));
        }

        // 명소 게시글 목록
        model.addAttribute("attractions" , attractions);

        return "main"; //  main.html (빈 페이지)
    }

    /**
     *  main 페이지에서 map 접근
     */
    @GetMapping("/map")
    public  String map(Model model){

        model.addAttribute("attractions", AttractionService.apiBoards);

        return "map/map";
    }


    /**
     * 로그인 url
     * /login -> loginForm.html -> /loginProcess -> if login fail -> alertmsg 에러 메시지 담는다.
     * -> /login으로 해당 error 메시지 alterMessage 변수에 넣어준다.
     *
     * @param alertMessage 로그인에 실패 했을 경우 , 서버(loginFailureHandler)로부터 날라오는 error를 controller에 변수로 담는다.
     * @param model 에러 메시지를 담기 위한 model.
     * @return 로그인 화면 반환
     * @author LEE SOO CHAN
     */
    @GetMapping("/login")
    public  String loginForm(@RequestParam(value = "alertmsg", required = false) String alertMessage,
                             Model model){

        model.addAttribute("alertmsg", alertMessage);
        return "loginForm";
    }

    /**
     * 회원가입 url
     * @return 회원가입 폼
     * @author LEE SOO CHAN
     */
    @GetMapping("/join")
    public  String joinForm(){
        return "joinForm";
    }



    /**
     * 회원가입을 처리하기 위한 url
     * 회원가입 Form에서 사용자가 데이터를 입력하고 해당 데이터를 처리 하기 위한 url
     * @param user 사용자의 회원가입 정보가 담겨 있다.
     * @return 다시 로그인 하기 위한 form으로 redirect
     * @author LEE SOO CHAN
     */
    @PostMapping(value = "/join" )
    public String join(UserJoinRequest user){
        UserResponse userDto = userService.join(user); // 시큐리티 설정을 하는 순간 비밀번호 암호화가 반드시 필요하다.
        return "redirect:/login";  //   @GetMapping("/loginForm") 여기로 돌아간다.
    }

    // check duplicate login id
    @PostMapping(value = "/checkDuplicateLoginId")
    @ResponseBody
    public DuplicateMessageDto checkDuplicateLoginId(@RequestParam("loginId") String loginId){
        Boolean isDuplicate = userService.checkDuplicateLoginId(loginId);
        return DuplicateMessageDto.builder().isDuplicate(isDuplicate).build();
    }

    //check duplicate nickname
    @PostMapping(value = "/checkDuplicateNickname")
    @ResponseBody
    public DuplicateMessageDto checkDuplicateNickname(@RequestParam("nickname") String nickname){
        Boolean isDuplicate = userService.checkDuplicateNickname(nickname);
        return DuplicateMessageDto.builder().isDuplicate(isDuplicate).build();
    }


    /**
     * main 페이지에서 관리자 페이지 버튼 클릭시 들어가는 URL
     * @return 다시 /myPage로 redirect 한다.
     * @author LEE SOO CHAN
     */
    @GetMapping("/adminPage")
    public String adminPage(){

        return "redirect:/myPage";
    }


}
