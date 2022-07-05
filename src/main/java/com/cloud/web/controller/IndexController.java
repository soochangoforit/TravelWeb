package com.cloud.web.controller;

import com.cloud.web.config.auth.PrincipalDetails;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public  String index(Model model , @AuthenticationPrincipal PrincipalDetails principalDetails) {

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

        // security session에 있는 name 정보 가져온다.
        if(principalDetails != null) {
            model.addAttribute("name", principalDetails.getUser().getName());
        }

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


    @GetMapping("test/login")
    @ResponseBody
    public String testLogin(Authentication authentication , @AuthenticationPrincipal PrincipalDetails principalDetails){

        // DI를 통해서 받고, 다운 캐스팅을 통해서 데이터를 받을 수도 있고
        // Authentication Class 객체를 사용해서 principalDetails 정보를 가져올 수 있다.
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principal.getUser());


        // @AuthenticationPrincipal 어노테이션을 이용해서 데이터를 가져올 수 있다.
        // @AuthenticationPrincipal 어노테이션은 UserDetails 타입을 가지고 있다.
        // 우리는 또 다른 UserDetails 타입을 가지고 있기 때문에, 바꿔끼워도 상관 없다.
        // 현재 userDetails에서 UserResponse 객체가 들어가고 있고, 일부 정보만 들어있다.
        // UserResponse를 사용했던 이유는 Session DB를 사용하기 위해서는 User를 Serializable 객체로 만들 순 없기 때문이다.
        System.out.println("userDetails : " + principalDetails.getUser());
        System.out.println("userDetails : " + principalDetails.getUser().getName());

        //위처럼 일반 로그인 과정에서는 올바르게 각 상황에 맞춰서 PrincipalDetails 로 형변환이 가능했지만
        // Oauth2 로그인을 사용하는 경우는 , 형변환을 PrincipalDetails로 할  수 없다.
        // Oauth2를 해서 얻어진 details type은 org.springfamily.security.oauth2.user.DefaultOAuth2User 이다.

        return "일반 로그인 세션 정보 확인";
    }


    //위처럼 일반 로그인 과정에서는 올바르게 각 상황에 맞춰서 PrincipalDetails 로 형변환이 가능했지만
    // Oauth2 로그인을 사용하는 경우는 , 형변환을 PrincipalDetails로 할  수 없다.
    // Oauth2를 해서 얻어진 details type은 org.springfamily.security.oauth2.user.DefaultOAuth2User 이다.
    @GetMapping("test/oauth/login")
    @ResponseBody
    public String testOauthLogin(Authentication authentication , @AuthenticationPrincipal OAuth2User oauth2User){

        // 따라서, 형변환 할때는 OAuth2User로 해줘야 한다.
        // getAttributes() 메소드를 사용하면, 구글 로그인에 성공한 사용자의 프로필 정보를 확인할 수 있다.
        // 그 말은 즉, PrincipalOath2UserService에서 super.loadUser(userRequest) 를 통해서 얻어진
        // 구글로부터 로그인된 사용자의 데이터를 받은 정보가 Spring Security가 알아서 해당 모든 정보들을
        // SpringSecurityContextHolder에 저장해준다. 저장 해주었기 때문에
        // 아래와 같이 authentication으로 접근해서 데이터를 가져올 수 있었던 것이다.
        OAuth2User oauth2 = (OAuth2User) authentication.getPrincipal();
        System.out.println("oauth2User : " + oauth2.getAttributes());


        // 또 다른 방법으로는 @AuthenticationPrincipal 어노테이션을 이용해서 데이터를 가져올 수 있다.
        // 대신 일반 로그인과는 다르게 PrincipalDetails를 사용하는것이 아니라,
        // Oauth2User가 가지고 있는 타입을 활용해여 한다.
        // 위에랑 똑같이 getAttributes() 메소드를 사용하면, SpringSecutiryContextHolder에 담긴
        // 구글 로그인에 성공한 사용자의 프로필 정보를 확인할 수 있다.
        // 중요한 점**** OAuth2로 로그인을 하면 Authentication 객체에는 OAuth2User 객체가 담겨있다.
        System.out.println("oauth2User : " + oauth2User.getAttributes());

        // 문제점!!
        // 일반 로그인 같은 경우는 UserDetails가 시큐리티 세션에 담겨지고
        // OAuth2 로그인 같은 경우는 OAuth2User가 시큐리티 세션에 담겨지고 있다.
        // 혹여나, 다른 컨트롤러에서 Security Session에 접근해서 정보를 가져와야할 경우
        // UserDetails로 가져와야 할지 OAuth2User로 가져와야 할지 모르게 된다.
        // 그러기 때문에 부가적인 작업이 필요하다.
        // 특정한 Class X를 만들어서 UserDetails도 implements하고 OAuth2User도 implements 하도록 한다.
        // PrincipalDetailsService의 loadUserByUsername() 메소드의 return 값이 SecurityContextHolder에 담기게 된다.
        // 따라서, 앞서 우리가 만들어 놓았던, PrincipalDetails Class를 UserDetails와 OAuth2User를 같이 implements하도록 한다.
        // 그렇게 하면 사용자는 OAuth2로 로그인 하든, 일반 로그인을 하든 PrincipalDetails로 형변환 할 수 있다.


        return "Oauth 세션 정보 확인";
    }

    @GetMapping("/anotherlogin")
    @ResponseBody
    public String another(Authentication authentication , @AuthenticationPrincipal PrincipalDetails principalDetails ) {

        System.out.println("authentication : " + ((PrincipalDetails) authentication.getPrincipal()).getUser() );

        System.out.println("principalDetails : " + principalDetails.getUser());

        return "data";
    }



}
