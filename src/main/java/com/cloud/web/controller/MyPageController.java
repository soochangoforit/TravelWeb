package com.cloud.web.controller;


import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.domain.*;
import com.cloud.web.domain.enums.Role;
import com.cloud.web.dto.request.FoodBoardPostFormDto;
import com.cloud.web.dto.response.FoodBoardShowDto;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.repository.*;
import com.cloud.web.service.AttachmentServiceImpl;
import com.cloud.web.service.FoodBoardService;
import com.cloud.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class MyPageController {


    private UserService userService;
    private FoodBoardService foodBoardService;
    private UserRepository userRepository;
    private FoodBoardRepository foodBoardRepository;
    private FoodTypeRepository foodTypeRepository;
    private LocationTypeRepository locationTypeRepository;
    private FoodCmtRepository foodCmtRepository;

    @Autowired
    public MyPageController(UserService userService, FoodBoardService foodBoardService,
                            UserRepository userRepository, FoodBoardRepository foodBoardRepository,
                            FoodTypeRepository foodTypeRepository, LocationTypeRepository locationTypeRepository,
                            FoodCmtRepository foodCmtRepository) {
        this.userService = userService;
        this.foodBoardService = foodBoardService;
        this.userRepository = userRepository;
        this.foodBoardRepository = foodBoardRepository;
        this.foodTypeRepository = foodTypeRepository;
        this.locationTypeRepository = locationTypeRepository;
        this.foodCmtRepository = foodCmtRepository;
    }

    /**
     * 로그인 한 User의 마이 페이지 가기
     * 마이 페이지 버튼을 눌렀을때 , 어떤 사용자가 로그인 했는지에 따라서 다른 화면으로 넘어간다.
     *
     * @return myPage/info : User의 마이 페이지 이동 , myPage/adminPage : ADMIN의 마이 페이지 이동
     * @author LEE SOO CHAN
     */
    @GetMapping("/myPage")
    public String myPage(Model model , Authentication authentication){

        // 사용자의 정보를 출력한다.
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        UserResponse user = userService.findUserById(userDetails.getUser().getDb_id());

        // if 유저의 권한을 가지고 있을 경우
        if(user.getRole()== Role.ROLE_USER){
            model.addAttribute("user" , user);
            model.addAttribute("foodBoards", user.getFoodBoardList());
            return "myPage/info"; //user 전용 마이 페이지 이동
        }
        // else 관리자 권한을 가지고 있는 경우
        else{
            model.addAttribute("user" , user); // user에 관리자 정보가 들어간다.
            List<FoodBoard> foodBoards = foodBoardRepository.findAll(); // 모든 게시글 정보 보여준다.
            model.addAttribute("foodBoards", foodBoards);
            return "myPage/adminPage"; //admin 전용 마이페이지 이동
        }
    }


    /**
     * 로그인 사람에 따라서 다른 마이 페이지 화면이 나오고
     * 특정 맛집 게시물을 클릭하면 해당 게시글을 수정할 수 있는 수정 폼으로 간다.
     * USER , ADMIN 같은 html를 공유하게 된다. 다만 html에서 해당 사용자에 따라 다른 버튼과 구성으로 이루어진다.
     *
     * @param id 수정하고자 하는 맛집 게시물의 db_id
     * @return 맛집 게시글을 수정할 수 있는 폼 (해당 게시글의 데이터가 담긴다. + 사진이랑 + USER랑 ADMIN 모두 같은 html 파일로 이동)
     * @author LEE SOO CHAN
     */
    @GetMapping("/myPage/foods/{id}")
    public String myFoodBoardPage(@PathVariable Long id, Model model){

        // 수정 폼에서 해당 게시글의 이미 등록된 사진을 보여준다.
        FoodBoardShowDto foodBoardShowDto = foodBoardService.showByFoodBoardId(id);
        List<Attachment> attachedFiles = foodBoardShowDto.getAttachedFiles();

        // 수정할 폼에 해당 게시글이 가지고 있던 원래 데이터를 담아서 넘겨준다. + 대신 사진 수정을 위해 수정될 사진을 담을 수 있는 dto 사용.
        FoodBoardPostFormDto foodBoardPostFormDto = foodBoardService.showUpdateFormById(id);

        // 수정 폼에서도 User는 댓글의 목록을 확인할 수 있다.
        List<FoodCmt> foodCmts = foodBoardRepository.findById(id).get().getFoodCmts();

        // 맛집 유형과 위치 정보를 가져온다.
        List<FoodType> foodTypeList = foodTypeRepository.findAll();
        List<LocationType> locationTypeList = locationTypeRepository.findAll();

        // 수정 하고자 하는 게시글에 데이터를 뿌려준다. (게시글 데이터 + 수정될 사진을 담을 수 있는 MultipartFile Type의 필드 들어간다.
        model.addAttribute("foodBoardPostFormDto" , foodBoardPostFormDto);

        // 수정하고자 하는 폼에서도 foodCmts를 확인할 수 있다.
        model.addAttribute("foodCmts" , foodCmts);

        // 등록된 게시물이 가지는 사진을 볼 수 있다.
        model.addAttribute("attachedFiles", attachedFiles);
        model.addAttribute("foodTypeList", foodTypeList);
        model.addAttribute("locationTypeList", locationTypeList);

        // foodBoardPostFormDto 에는 id값을 가지고 있지 않음으로 따로 넘겨준다.
        model.addAttribute("id", id);

        return "myPage/updateFoodForm";
    }


    /**
     * 해당 게시글의 정보를 수정 요청을 할 수 있는 api
     * @param id 수정하고자 하는 게시물의 db_id
     * @param foodBoardPostFormDto 수정할 값들을 받는 dto
     * @return redirect: /myPage/foods/{id} 로 가서 수정한 내역을 확인할 수 있도록 해당 게시글의 상세 페이지로 넘어간다.
     * @author LEE SOO CHAN
     */
    @PutMapping("/myPage/foods/{id}")
    public String updateFoodBoard(@PathVariable Long id, @ModelAttribute FoodBoardPostFormDto  foodBoardPostFormDto ,
                                  Authentication authentication) throws IOException {

        // 사용자의 정보를 출력한다.
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUser().getDb_id()).orElse(null);

        foodBoardService.update(id, user, foodBoardPostFormDto);

        String boardId = String.valueOf(id);

        return "redirect:/myPage/foods/" + boardId;
    }


    /**
     * 게시물 삭제 api
     * deleteById 대해 따로 service에서 만들지 않았기 때문에 Transactional을 붙여줘야 한다.
     * @param id 삭제하고자 하는 게시물의 id
     * @return 로그인 한 사용자의 myPage 반환
     * @author LEE SOO CHAN
     */
    @DeleteMapping("/myPage/foods/{id}")
    @Transactional //
    public String deleteBoard(@PathVariable("id") Long id){

        foodBoardRepository.deleteById(id);

        return "redirect:/myPage";
    }


    /**
     * 회원 탈퇴 api
     * @return 로그 아웃된 main page 반환
     * @author LEE SOO CHAN
     */
    //@DeleteMapping("/myPage")
    @GetMapping("/myPage/delete")
    @Transactional
    public String leave(Authentication authentication){

        // 사용자의 모든 정보 삭제 (관련 게시글 , 작성한 댓글)
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        userRepository.deleteById(userDetails.getUser().getDb_id());

        return "redirect:/logout";
    }


    /**
     * 관리자가 특정 게시물에 접근하고, 해당 게시글의 특정 댓글을 삭제하고자 할때 들어오는 URL
     * @param id 삭제하고자 하는 댓글의 고유 id
     * @param boardId 댓글 삭제후 맛집 게시글 상세 페이지로 다시 이동하기 위해, 맛집 게시글의 고유 id
     * @author LEE SOO CHAN
     */
    @DeleteMapping("/myPage/foods/cmt/{id}")
    @Transactional
    public String deleteCmt(@PathVariable Long id , @RequestParam("board") Long boardId){

        foodCmtRepository.deleteById(id);

        String bid = String.valueOf(boardId);

        return "redirect:/myPage/foods/" + bid;
    }




}
