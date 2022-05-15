package com.cloud.web.controller.test;

import com.cloud.web.dto.request.UserJoinRequest;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Controller
public class testController {

    private final UserService userService;

    @Autowired
    public testController(UserService userService ) {
        this.userService = userService;
    }




    @GetMapping("/")
    public  String index(Model model){
        return "main";
    }


    // 로그인에 실패 했을 경우 , 서버(loginFailureHandler)로부터 날라오는 error를 controller에 변수로 담는다. //
    @GetMapping("/login")
    public  String loginForm(@RequestParam(value = "alertmsg", required = false) String alertMessage,
                             Model model){

        model.addAttribute("alertmsg", alertMessage);

        return "loginForm";
    }

    @GetMapping("/join") //<a href로 들어온다. get방식으로>
    public  String joinForm(){
        return "joinForm";
    }


    //@ResponseBody  반환값을 .html이 아닌 데이터(JSON)으로 반환하겠다는 의미.
    @PostMapping(value = "/join" ) // 실제 회원가입을 담당하는 곳
    public String join(UserJoinRequest user){

        UserResponse userDto = userService.join(user);// 시큐리티 설정을 하는 순간 비밀번호 암호화가 반드시 필요하다.

        // 확인하고 싶어서 출력 (의미 X)
        log.info(userDto.getDb_id().toString());
        log.info(userDto.getName());
        log.info(userDto.getNickname());
        log.info(userDto.getEmail());

        return "redirect:/login";  //   @GetMapping("/loginForm") 여기로 돌아간다.

    }


    // 행사 게시글 첫 페잊;
    @GetMapping(value = "/festivals")
    public String accesied(){
        return "festivals";
    }

    // 행사 등록하기 - admin만 접근 가능
    @GetMapping(value = "/admin/festival/save")
    public @ResponseBody String post(){
        return "행사 등록을 위한 페이지 입니다.";
    }





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

    @PreAuthorize("hasRole('ROLE_ADMIN')") // dataPage 메소드가 실행 되기 전에 실행된다. 저런 문법 아래에서만 사용이 가능하다. 하나로만 걸고 싶으면 @Secured만 사용해도 괜찮다.
    @GetMapping("/data")
    public @ResponseBody String dataPage(){
        return "admin 권한 데이터 정보";
    }





}
