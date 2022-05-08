package com.cloud.web.service;

import com.cloud.web.domain.User;
import com.cloud.web.domain.enums.Role;
import com.cloud.web.dto.request.UserLoginRequest;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository , BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    // 원래는 User 사용하면 X , DTO로 사용해야한다. Join 하는 시점에 원래 role_type 필드에 권한을 넣어줘야 한다.
    @Transactional
    public UserResponse join(UserLoginRequest user){

        String rawPw = user.getPassword();
        String encodePw = bCryptPasswordEncoder.encode(rawPw);

        String loginId = user.getUsername(); // 로그인한 User의 아이디 get
        User encoder;

        String pattern = "(@!)[a-zA-Z0-9]*(!@)"; //@! 로 시작하고 !@로 끝나는 아이디는 관리자로 취급

        if(Pattern.matches(pattern, loginId) ){
            encoder = User.builder()
                    .loginId(loginId)
                    .password(encodePw)
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(Role.valueOf("ROLE_ADMIN")).build();

        }else{
            encoder = User.builder()
                    .loginId(user.getUsername())
                    .password(encodePw)
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(Role.valueOf("ROLE_USER")).build();
        }

        // repository를 통해 save하기 전에 encode 해줘야 한다.
        User entity = userRepository.save(encoder);

        UserResponse userLoginResponse = new UserResponse(entity.getId(), entity.getName(), entity.getNickname(), entity.getEmail());

        return userLoginResponse;
    }

    // 최대한 Entity를 Controller까지 들고오지 않기 위해서 DTO를 활용했다.
    public UserResponse findById(Long id){

        Optional<User> entity = userRepository.findById(id);

        UserResponse user = UserResponse.builder()
                .db_id(entity.get().getId())
                .name(entity.get().getName())
                .nickname(entity.get().getNickname())
                .email(entity.get().getEmail()).build();

        return user;
    }
}
