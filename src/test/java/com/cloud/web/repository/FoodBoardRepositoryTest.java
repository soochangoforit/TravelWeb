package com.cloud.web.repository;

import com.cloud.web.domain.FoodBoard;
import com.cloud.web.domain.FoodType;
import com.cloud.web.domain.LocationType;
import com.cloud.web.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "cloud_init.sql" , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
@Rollback(value = false)
public class FoodBoardRepositoryTest {

    @Autowired
    private FoodBoardRepository foodBoardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationTypeRepository locationTypeRepository;
    @Autowired
    private FoodTypeRepository foodTypeRepository;


    @Test
    public void check_게시글_생성시간_성공() throws Exception {
        //given
        User user = userRepository.findById(1L).get();
        LocationType locationType = locationTypeRepository.findById(1L).get();
        FoodType foodType = foodTypeRepository.findById(1L).get();


        FoodBoard foodBoard = new FoodBoard(user, locationType, foodType, "맛집 게시글 test", "test", "사진경로", "주소", "맛집 정보", 4.3);

        //when
        foodBoardRepository.save(foodBoard);

        //then
        Assertions.assertThat(foodBoard.getCreatedDate()).isNotNull();
     }

     @Test
     public void chech_게시글_수정시간_성공() throws Exception {
         User user = userRepository.findById(1L).get();
         LocationType locationType = locationTypeRepository.findById(1L).get();
         FoodType foodType = foodTypeRepository.findById(1L).get();


         FoodBoard foodBoard = new FoodBoard(user, locationType, foodType, "맛집 게시글 test", "test", "사진경로", "주소", "맛집 정보", 4.3);

         //when
         foodBoardRepository.save(foodBoard);

         foodBoard.setAddress("주소 변경했음");

         //then
         Assertions.assertThat(foodBoard.getModifiedDate()).isNotNull();

      }




}