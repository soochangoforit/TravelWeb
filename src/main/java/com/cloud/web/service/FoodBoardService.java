package com.cloud.web.service;

import com.cloud.web.domain.Attachment;
import com.cloud.web.domain.FoodBoard;
import com.cloud.web.domain.User;
import com.cloud.web.dto.request.BoardPostDto;
import com.cloud.web.dto.request.FoodBoardSaveDto;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.repository.FoodBoardRepository;
import com.cloud.web.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    public FoodBoard post(BoardPostDto boardPostDto) throws IOException {

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


}
