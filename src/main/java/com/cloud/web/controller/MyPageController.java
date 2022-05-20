package com.cloud.web.controller;


import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.domain.*;
import com.cloud.web.domain.enums.AttachmentType;
import com.cloud.web.dto.request.FoodBoardPostDto;
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
import java.util.Optional;

@Controller
public class MyPageController {

    @Autowired
    private UserService userService;
    @Autowired
    private FoodBoardService foodBoardService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FoodBoardRepository foodBoardRepository;
    @Autowired
    private FoodTypeRepository foodTypeRepository;
    @Autowired
    private LocationTypeRepository locationTypeRepository;
    @Autowired
    private AttachmentServiceImpl attachmentServiceImpl;
    @Autowired
    private AttachmentRepository attachmentRepository;



    /**
     * 로그인 한 User의 마이 페이지 가기
     * 1. 기본적인 자신의 정보를 확인할 수 있다.
     * 2. 자신이 올린 게시글의 목록을 확인 할 수 있다. (수정, 삭제를 위해서)
     * 3. 자신의 댓글이 달린 게시물을 확인 할 수 있다. (보류 , 댓글 삭제 , 수정 위해서)
     * 4. 회원 탈퇴를 할 수 있다. ( 보류 , 전체 데이터 삭제 )
     * @return
     */
    @GetMapping("/myPage")
    public String myPage(Model model , Authentication authentication){

        // 사용자의 정보를 출력한다.
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        UserResponse user = userService.findUserById(userDetails.getUser().getDb_id());

        // 1. 기본적인 자신의 정보를 확인할 수 있다. UserResponse DTO가 반환된다.
        model.addAttribute("user" , user);

        // 2. 자신이 작성한 게시글의 목록을 확인 할 수 있다.
        model.addAttribute("foodBoards", user.getFoodBoardList());

        return "myPage/info";
    }


    /**
     * 수정 폼을 보여주는 GET
     * @param id 수정하고자 하는 게시물의 id
     * @return 맛집 게시글을 수정할 수 있는 폼
     */
    @GetMapping("/myPage/foods/{id}")
    public String myFoodBoardPage(@PathVariable Long id,  Model model ){

        //todo : dto 변수이름을 바꿔야 한다 지금은 헷갈리지 않기 위해서 dto이름 그대로 사용
        // 수정시 해당 게시글의 사진을 미리 보여주기 위해서
        FoodBoardShowDto foodBoardShowDto = foodBoardService.showByFoodBoardId(id);
        List<Attachment> attachedFiles = foodBoardShowDto.getAttachedFiles();

        // 수정할 폼에 데이터를 담아서 넘겨준다.
        FoodBoardPostFormDto foodBoardPostFormDto = foodBoardService.showUpdateFormById(id);

        // 맛집 유형과 위치 정보를 가져오기 위해서
        List<FoodType> foodTypeList = foodTypeRepository.findAll();
        List<LocationType> locationTypeList = locationTypeRepository.findAll();


        // 데이터를 뿌리되,, 사진 arrayList는 Multi Type으로 null를 가지게 된다.
        model.addAttribute("foodBoardPostFormDto" , foodBoardPostFormDto);
        // 이미지 파일은 AttachMent 형식의 list로 해서 반환된다. (보여주기 위해서)
        model.addAttribute("attachedFiles", attachedFiles);
        model.addAttribute("foodTypeList", foodTypeList);
        model.addAttribute("locationTypeList", locationTypeList);
        model.addAttribute("id", id);

        return "myPage/updateFoodForm"; // 수정할 수 있는 폼 (해당 게시글의 데이터가 담긴다. + 사진이랑 )


    }

    /**
     * 수정 요청을 할 수 있는 api
     * @param id 수정하고자 하는 게시물의 db_id
     * @param foodBoardPostFormDto 수정할 값들을 받는 dto
     * @return redirect: /myPage/foods/{id} 로 가서 수정한 내역을 확인할 수 있도록 넘어간다.
     */
    //todo : 파라미터 dto 이름 변수를 바꾸자 , 지금은 쉽게 이해하기 위해서 그냥 사용
    @PostMapping("/myPage/foods/{id}")
    @Transactional
    public String updateFoodBoard(@PathVariable Long id, @ModelAttribute FoodBoardPostFormDto  foodBoardPostFormDto ,Authentication authentication) throws IOException {

        // 사용자의 정보를 출력한다.
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUser().getDb_id()).orElse(null);

        // 한번
        FoodBoard foodBoard = foodBoardRepository.findById(id).orElse(null);

        //원래 가지고 있던 사진들 삭제 처리
        attachmentRepository.deleteByFoodId(id);

        //원래 foodBoard가 가지고 있던 file list 삭제 필요
        //foodBoard.getAttachedFiles().clear();



        //todo : 건들게 많아 보인다.
        // FoodBoardPostDto 에는 사진 목록을 가지고 있긴 하지만 Map 형식으로 갖고 있다.
        // 또한 추가적으로 AttachMent list가 아닌 Multitype으로 갖고있다.
        FoodBoardPostDto boardPostDto = foodBoardPostFormDto.createBoardPostDto(user); // user 정보를 담고 있는 FoodBoardPostDto 만든다.

        List<Attachment> attachments = attachmentServiceImpl.saveAttachments(boardPostDto.getAttachmentFiles());

        attachments.stream().forEach(e-> e.setBoard(foodBoard));


        //todo : multi type에서 attachment로 바꿔야한다.
        FoodBoard changed = foodBoard.changeToEntity(id
                , user, foodBoardPostFormDto.getLocationType(), foodBoardPostFormDto.getFoodType(), foodBoardPostFormDto.getTitle()
                , foodBoardPostFormDto.getPreview(), foodBoardPostFormDto.getAddress(), foodBoardPostFormDto.getInfo()
                , foodBoardPostFormDto.getRate(), attachments);


        // foodBoardShowDto 안에는 해당 게시글의 db_id도 가지고 있다.
        // 더티 채킹이란 ?? 우선 DB에서 한번 가져와서 영속성 컨텍스트에서 관리가 되어야 한다.
        // 하지만 지금처럼 새로 객체를 만들고 저장하려는 경우, 영속성 컨텍스트에서 관리가 되지 않기 때문에
        // 기본 save로 들어가고 null이 되면 안되는 필드에 null이 들어가서 exception 터진다.
        /*FoodBoard entity = FoodBoard.builder()
                .id(foodBoardShowDto.getId())
                .user(user)
                .title(foodBoardShowDto.getTitle())
                .locationType(foodBoardShowDto.getLocationType())
                .foodType(foodBoardShowDto.getFoodType())
                .preview(foodBoardShowDto.getPreview())
                .info(foodBoardShowDto.getInfo())
                .rate(foodBoardShowDto.getRate())
                .address(foodBoardShowDto.getAddress())
                .attachedFiles(foodBoardShowDto.getAttachedFiles()).build();*/

        FoodBoard saved = foodBoardRepository.save(changed);

        // entity를 다시 foodBoardShowDto로 반환해서 ...?? no no 그냥 redirect 시키자.

        String aa = String.valueOf(id);

        return "redirect:/myPage/foods/" + aa;
    }








}
