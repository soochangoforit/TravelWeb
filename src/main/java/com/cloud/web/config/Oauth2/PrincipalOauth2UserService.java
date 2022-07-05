package com.cloud.web.config.Oauth2;

import com.cloud.web.config.Oauth2.Provider.FacebookUserInfo;
import com.cloud.web.config.Oauth2.Provider.GoogleUserInfo;
import com.cloud.web.config.Oauth2.Provider.NaverUserInfo;
import com.cloud.web.config.Oauth2.Provider.OAuth2UserInfo;
import com.cloud.web.config.auth.PrincipalDetails;
import com.cloud.web.domain.User;
import com.cloud.web.domain.enums.Role;
import com.cloud.web.dto.response.UserResponse;
import com.cloud.web.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리가 되는 함수
    // oauth2 client library에 의해서 userRequest에 액세스 토큰을 가지고 있으며
    // super.loadUser(userRequest)를 호출하면 사용자의 구글 정보를 받아올 수 있다.
    // getAttributes : sub(구글에 로그인한 자신 아이디가 순자로 나옴)
    // name , given_name , family_name , picture, email, email_verified=true  ,locale=ko

    // 예시
    // username = "google_{sub}"
    // password = "암호화(겟인데어)" -> 어차피 구글 로그인으로 진행되는 회원이라서, 아이디랑 비밀번호를 쳐서 로그인을 하는것은 아니다.
    // password는 null만 아니고, 아무거나 해도 상관이 없다.
    // email = "tncksdl05@gmail.com" (그대로 넣는다.)
    // role = "ROLE_USER"

    // 만약 저런식으로만 회원가입을 진행한다며, 일반적인 사용자인지 , Oauth를 활용한 사용자인지 구분 X
    // 따라서, User Entity에 provider, providerId를 넣어준다. private String provider, private String providerId;
    // provider : "google" (registrationId), providerId : {sub} 를 회원가입할때 추가적으로 넣어주도록 한다.


    // registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth2-Client 라이브러리가 받아준다.) -> code를 통해서 AccessToken 요청
        // AccessToken를 받는것 까지가 userRequest가 담당하는 역할이다.
        // userRequest가 가지고 있는 AccessToken를 통해서 loadUser를 호출하면 구글로부터 회원 프로필을 받을 수 있다.
        // 그때 사용되는 함수가 loadUsr함수이다.
        // loadUser 함수를 통해서 AccessToken를 통해 ->  회원 프로필을 데이터를 받을 수 있다.
        OAuth2User oAuth2 = super.loadUser(userRequest);

        // 회원가입을 강제로 진행해볼 예정 (다만, 구글과 페이스북에 제공하는게 조금씩 다르기 때문에 유지보수 입장에서 인터페이스 활용)
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2.getAttributes().get("response"));
        }else{
            throw new IllegalArgumentException("알수없는 OAuth2 클라이언트 정보 : " + userRequest.getClientRegistration().getRegistrationId());
        }




        //String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String provider = oAuth2UserInfo.getProvider(); // google
        //String providerId = oAuth2.getAttribute("sub");
        String providerId = oAuth2UserInfo.getProviderId(); // {sub} or {id}
        String loginId = provider + "_" + providerId; // google_124567890 중복을 피하기 위해서
        String password = passwordEncoder.encode("web"); // 암호화(겟인데어) -> 어차피 구글 로그인으로 진행되는 회원이라서, 아이디랑 비밀번호를 쳐서 로그인을 하는것은 아니다. -> 따라서 솔직히 username일아 password가 필요없다. (그냥 만드는거다.)
        //String email = oAuth2.getAttribute("email"); // email
        String email = oAuth2UserInfo.getEmail(); // email
        String name = oAuth2UserInfo.getName(); // name
        Role role = Role.valueOf("ROLE_USER"); // ROLE_USER

        // 이미 회원이 있는지 확인한다. userRepository를 통해서 회원이 있는지 확인한다.
        User user = userRepository.findByLoginId(loginId).orElse(null);

        UserResponse userResponse = null;
        // Oauth2 로그인을 한번이라도 한 회원이 존재하지 않는다면, 회원가입을 해야한다.
        if (user == null) {
            // 회원이 없다면, 회원가입을 한다. 닉네임은 구글 데이터에서 제공하지 않음으로 prePersist를 통해서 넣어준다.
            // 우리 프로젝트 에서는 admin은 제외하고, 일반 회원이 되는 것이다.
            // 왜냐하면 admin은 role를 "ROLE_ADMIN"으로 가져와야 하는데
            // 구글에서 제공하는 데이터만 가지고 해당 로그인한 사람이 admin인지 user인지 구분하지 못하기 때문이다.
            // 그렇기 때문에 Oauth 로그인 같은 경우는, User에 한해서만 실시할 계획이다.
            User entity = User.builder()
                    .loginId(loginId)
                    .password(password)
                    .name(name)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            User saved = userRepository.save(entity);

            userResponse = UserResponse.builder()
                    .db_id(saved.getId())
                    .loginId(saved.getLoginId())
                    .password(saved.getPassword())
                    .email(saved.getEmail())
                    .name(saved.getName())
                    .nickname(saved.getNickname())
                    .role(saved.getRoleType())
                    .provider(saved.getProvider())
                    .providerId(saved.getProviderId())
                    .build();
        }else{
            // Oauth2 로그인을 한번이라도 한 회원이 있다면, 기존 회원에 대한 정보를 반환한다.
            // session DB를 사용하기 위해서는 Serializable을 할 수 있는 UserResponse로 넣어줘야 한다.
            userResponse = UserResponse.builder()
                    .db_id(user.getId())
                    .loginId(user.getLoginId())
                    .password(user.getPassword())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRoleType())
                    .provider(user.getProvider())
                    .providerId(user.getProviderId())
                    .build();
        }

        // 해당 return 값은 Spring Security 에 Authentication 객체로 들어갈것이다.
        // 일반적으로 로그인 한다고 하면 단순히 UserResponse에 대한 데이터만 들고 있겠지만
        // Oauth2로 로그인을 한다고 하면 UserResponse와 Attributes를 함께 가지게 된다.
        return new PrincipalDetails(userResponse , oAuth2.getAttributes());
    }
}
