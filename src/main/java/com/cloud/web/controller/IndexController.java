package com.cloud.web.controller;

import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.dto.request.UserJoinRequest;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 프로젝트 Main Page에 대한 Controller
 */
@Slf4j
@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService ) {
        this.userService = userService;
    }

    /**
     *  Main Page 접근
     */
    @GetMapping("/")
    public  String index(Model model){
        return "main";
    }


    /**
     * 로그인 url
     * /login -> loginForm.html -> /loginProcess -> login fail -> alertmsg 에러 메시지 담는다.
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


    /**
     * Main에 있는 관리자 페이지 가기 버튼을 ADMIN이 아닌 다른 사용자가 접근 했을때
     * 접근 권한 Error가 해당 controller로 들어온다.
     * @return 행사 게시글 페이지 반환
     * @author LEE SOO CHAN
     */
    @GetMapping(value = "/access/denied")
    public String acce(){
        return "accessDenined"; // 접근 권한이 없다는 alert 창이 뜨는 html이다.
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



/*

    @GetMapping("/user")
    public @ResponseBody String aproveUser(){
        return "로그인 까지 성공후 User 권한을 가진 사용자가 / 경로가 아닌 /user 경로로 자동으로 접근되었습니다.";
    }

    // 특정 메서드에 간단하게 걸고 싶으면 이렇게 하면 된다.
    @Secured("ROLE_ADMIN") // 해당 controller 위에 선언하면, ADMIN 권한만 가진 사용자만 접근 가능하다. 비록 security config에서 설정에서 제외를 해줬지만!!
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "admin 권한 개인정보";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')") // dataPage 메소드가 실행 되기 전에 실행된다. 저런 문법(hasRole('~') 아래에서만 사용이 가능하다. 하나로만 걸고 싶으면 @Secured만 사용해도 괜찮다.
    @GetMapping("/data")
    public @ResponseBody String dataPage(){
        return "admin 권한 데이터 정보";
    }
*/


}
