package com.cloud.web.service;

import com.cloud.web.domain.Attachment;
import com.cloud.web.domain.FoodBoard;
import com.cloud.web.domain.FoodCmt;
import com.cloud.web.domain.User;
import com.cloud.web.dto.request.FoodBoardPostDto;
import com.cloud.web.dto.request.FoodCmtDto;
import com.cloud.web.dto.response.FoodBoardShowDto;
import com.cloud.web.repository.FoodBoardRepository;
import com.cloud.web.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FoodBoardService {


    @Autowired
    private FoodBoardRepository foodBoardRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentServiceImpl attachmentServiceImpl;

/*
    public FoodBoard save(Long user_db_id , FoodBoardSaveDto foodBoardDto){

        User user = userRepository.findById(user_db_id).get();

        FoodBoard saveFoodBoard = FoodBoard.builder().user(user)
                .locationType(foodBoardDto.getLocationType())
                .foodType(foodBoardDto.getFoodType())
                .title(foodBoardDto.getTitle())
                .preview(foodBoardDto.getPreview())
                .address(foodBoardDto.getAddress())
                .info(foodBoardDto.getInfo())
                .rate(foodBoardDto.getRate()).build();

        FoodBoard saved = foodBoardRepository.save(saveFoodBoard);
        return saved;
    }
*/


    /**
     *     FoodBoard를 추가할때 첨부파일도 List에 저장함과 동시에 첨부파일 DB table에도 저장을 한다. (영속화되어 저장)
     *     web으로 넘어온 순수 form dto를
     *     user 정보가 담긴 BoardPostDto로 바꾸고
     *     해당 BoardPostDto를 최종적으로 DB에 저장하려고 한다.
     *
     *     BoardPostDto 에는 날것의 Mul Class Type의 2개의 List가 존재하고 있다.
     *     get을 통해서 가져오려고 한다. - 첫줄
     *
     *     날것의 2개 Mul Class Type을 1개의 Attachment Type List으로 바꿔준다. - 첫줄
     *
      */
    public FoodBoard post(FoodBoardPostDto boardPostDto) throws IOException {

        List<Attachment> attachments = attachmentServiceImpl.saveAttachments(boardPostDto.getAttachmentFiles());

        // 확인용 출력
        for (Attachment attachment : attachments) {
            log.info(attachment.getOriginFilename());
        }

        // 사용자가 웹에서 작성한 데이터 + 사용자 정보를 담은 dto에서 createBoard()를 하면
        // 해당 정보 모두들 담은 Entity 객체 FoodBoard를 반환 ( 해당 Entity 안에는 파일 리스트가 공백인 상태)
        FoodBoard board = boardPostDto.createBoard();

        //    attachments.stream()
        //            .forEach(attachment -> board.setAttachment(attachment));

        board.setAttachedFiles(attachments);

        return foodBoardRepository.save(board); //여기서 궁금한점 이렇게 하면 알아서 list에 있는 값들이 파일 dB에 저장이 될까??
        // Cascade.ALL안에 포함되어 있는 Cascade.Persist를 통해 같이 영속화되어 저장된다.

    }

    /**
     * 사용자가 보기 원하는 맛집 게시글의 상세 페이지를 반환
     * FoodBoardShowDto 반환
     */
    public FoodBoardShowDto show(Long id){

        FoodBoard entity = foodBoardRepository.findById(id).orElse(null);

        FoodBoardShowDto dto = FoodBoardShowDto.builder()
                .id(entity.getId())
                .locationType(entity.getLocationType().getType())
                .foodType(entity.getFoodType().getType())
                .address(entity.getAddress())
                .preview(entity.getPreview())
                .info(entity.getInfo())
                .rate(entity.getRate())
                .title(entity.getTitle())
                .attachedFiles(entity.getAttachedFiles()).build();

        return dto;

    }


    public void saveFoodCmt(Long user_db_id, Long id, FoodCmtDto foodCmtDto){

        // 로그인 사용자 중 누가 작성했는지 알기 위해서 해당 Entity 객체를 FoodCmt에 넣기 위해서 필요
        User user = userRepository.findById(user_db_id).orElse(null);

        // 어떤 맛집 게시글에 댓글을 작성했는지 확인하기 위해서
        FoodBoard foodBoard = foodBoardRepository.findById(id).orElse(null);

        String cmt = foodCmtDto.getCmt(); // 사용자가 작성한 String 댓글

        FoodCmt foodcmt = FoodCmt.builder()
                .user(user)
                .foodBoard(foodBoard)
                .foodCmt(cmt).build();

        // 영속성 컨텍스트에서 관리중인 Entity 객체에 Cmts 추가 , cascade=ALL에 의해서 연관된 entity 함께 persist 된다.
        foodBoard.addFoodCmts(foodcmt);

        // 영속성 컨텍스트에서 관리되고 있는 객체를 Dirty check를 함으로써 Cmt를 추가하고자 한다.
        // 현재 FoodBoard Entity는 이미 한번 DB_ID 값을 가지고 있기 때문에 Dirty Check 가능하다.
        foodBoardRepository.save(foodBoard);
    }


}
