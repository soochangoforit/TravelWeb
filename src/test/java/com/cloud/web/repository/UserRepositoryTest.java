package com.cloud.web.repository;

import com.cloud.web.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "cloud_init.sql" , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
//@Rollback(value = false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

  @Test
  public void user저장_default_USER_check() throws Exception {
      //given
      User user = new User("a555", "a555", "이수찬", "이수찬닉", "tncksdl05@gmail.com");

      //when
      User saved = userRepository.save(user);

      //then
      Assertions.assertThat(user).isEqualTo(saved);

   }

   @Test
   public void user_Update_success() throws Exception {
       //given
       Optional<User> searched = userRepository.findById(1L);

       //when
       searched.get().setName("이수찬2"); // dirty check (변경 감지)를 통해서 update

       //then
       Optional<User> searched2 = userRepository.findById(1L);
       assertThat(searched2.get().getName()).isEqualTo("이수찬2"); // 실제 DB에서도 이수찬2로 이름이 변경된것을 확인 가능

   }


    @Test
    public void loginId_unique_check_success() throws Exception {
        //given
        // 똑같은 a111 아이디로 회원가입 시도,
        User user = new User("a111", "a555", "이수찬", "이수찬닉", "tncksdl05@gmail.com");

        //when , then -> loginId 중복 가입 했을시 unique 제약 조건에 걸린 Exception 발생 확인
        assertThatThrownBy(() -> userRepository.save(user) )
                .isInstanceOf(DataIntegrityViolationException.class);
     }



}