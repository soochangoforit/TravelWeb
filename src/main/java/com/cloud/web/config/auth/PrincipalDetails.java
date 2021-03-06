package com.cloud.web.config.auth;



import com.cloud.web.domain.User;
import com.cloud.web.dto.response.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


//  시큐리티가 /login 주소 요쳥이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되어 로그인 성공을 하면 해당 유저의 정보를 시큐리티가 가지고 있는 고유한 session에 저장을 한다.
// 같은 session 공간인데 자신 만의 시큐리티 session 공간을 가진다.
// (Security ContextHolder) 라는 Key가 되는 곳에 session 정보를 저장을 한다.
// contextHolder에 들어갈 수 있는 session 정보의 Object가 정해져 있다.
// security 자기 자신이 가지고 있는 고유한 session 저장소 ( Security ContextHolder )에는 특정 Object로 들어와야 하는데
// 특정 Object는 Authenticaion 객체이여야 한다.
// Authentication 안에는 결국 User 정보가 있어야 한다.
// User의 정보도 특정한 Type으로 감싸져여 한다. User 정보를 담고 있는 객체의 Type은 -> UserDetails라는 타입의 객체로 로그인에 성공한 user 정보가 감싸져 있어야 한다.
// Security Session ( Security ContextHolder ) -> Authentication -> UserDetails (실제 User 정보를 감싸고 있다.)
// UserDetails 객체를 get으로 꺼내면, 드디어 최종적으로 User Class의 객체에 접근할 수 있다.
// 어떻게 UserDetails에서 User Class 객체로 접근할 수 있었을까??
// PrincipalDetails 해당 객체가 UserDetails랑 같은 Type으로 실제 로그인에 성공한 User Class 객체 정보를 담고 있으면 접근이 가능하다.
// 따라서ㅣ,,, 내 생각에는 로그인에 성공한 User Class 객체를 PrincipalDetails 로 감싸서 Security Session, Authenticaion에 넣을  수 있다.
// Authenticaion 객체를 만들어서 security session에 넣어야 한다.
// PrincipalDetails 는 PrincipalDetailsService와는 달리 @Bean으로 등록하지 않았다!! 왜?? 강제로 띄울 예정이다.


// 일단 우선적으로 내 생각에는 User 가 로그인을 성공했는지에 대한 과정이 먼저 우선되어야 한다. ( 그래야지 로그인된 해당 유저로부터 권한 ROLE를 가져올 수 있다.)
// 즉 이 Class 보다 먼저 선수행 되어야 하는 과정이 필요하다 (로그인 폼에 입력한 정보가 DB에 등록된 유저인지 확인하는 과정)

//@bean으로 등록된 PrincipalDetailsService 에서 loadUserByUsername() 으로 부터 사용이 되기 때문에 굳이 @Bean으로 등록하지 X

/**
 * getUser 추가
 *
 * 2022-07-04 implements OAuth2User 추가
 */
public class PrincipalDetails implements UserDetails  , OAuth2User {

    // override 해줘야 한다.
    // 우리의 로그인에 성공한 정보는 User Class 객체가 가지고 있다.
    private UserResponse user; //콤포지션

    /**
     * OAuth2User가 가지고 있는 Attributes를 그대로 가져오기 위해서 선언 ,
     * 셍성자 시점에서 초기화 진행
     * */
    private Map<String ,Object> attributes;

    // PrincipalDetails 가 실제 로그인에 성공한 User 정보를 담기 위해서 생성자로 해당 User 객체를 받아서 감싸준다.

    /**
     * 일반적인 로그인 할때 사용하는 생성자
     */
    public PrincipalDetails(UserResponse user) {
        this.user = user;
    }

    /**
     * OAuth2 로그인에 성공한 경우 사용하는 생성자
     * UserResponse 객체에 대한 데이터를 Attributes 정보를 토대로 해서 우리가 생성할 것이다.
     */
    public PrincipalDetails(UserResponse user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    /**
     * 기본 session에 저장하기 위해서 사용한다.
     *
     * 2022/07/18
     * 더 이상 기본적인 HttpSession은 사용하지 않는다.
     * HtttpSession에 민감한 정보를 담지 않기 위해서 해당 getUser를 통해
     * 민감하지 않는 데이터로만 구성된 UserResponse를 반환하고자 하였지만,
     * 이젠 부턴 Spring Security Session만 사용하게 됨으로써, UserResponse를 그대로 return 하도록한다.
     * 추후 Controller에서 사용하기 위해서
     */
    public UserResponse getUser() {

//        UserResponse userDto = UserResponse.builder()
//                .db_id(user.getDb_id())
//                .name(user.getName())
//                .nickname(user.getNickname())
//                .email(user.getEmail())
//                .role(user.getRole()).build();

        return user;
    }



    /**
     *return Type이 Collection 형태에다가 GrantedAuthority 를 return 해야한다.
     * 해당 User의 권한을 return 하는 곳이다.
     * 회원 가입 시점에서부터 앞서 우리가 Join 할때 커스텀 했듯이
     * 이미 어떠한 권한을 가질 수 있는 필드 값을 가지고 있다.
     * 그래서 로그인 시점에서 로그인이 성공하면 DB로 부터 실제 해당 유저 정보를 가지고 있는
     * 데이터를 가져오고, User Class가 가지고 있는 Enum 필드으로부터 권한 정보를 가져오는 역할을 한다.
     * 따라서, PrincipalDetails 가 감싸고 있는 로그인에 성공한 User Class 객체가 가지고 있는
     * Enum ROLE 정보를 get()으로 가져오면 된다. 그렇게 할 수 있었던 이유는,
     * 로그인에 성공된 해당 User class 객체 안에 이미 Join 시점에 할당 받았던
     * 특정한(USER) 권한을 나타내는 필드 값을 가지고 있기 때문이다.
     * User가 가지고 있는 role를 단순히 getRoleType()를 통해서 가져올 순 없다.
     * 왜냐하면 기본적으로 getAuthority() return Type이 String이기 때문이다.
     * 나의 경우는 Enum Role Type 이다.
     * user.getRoleType();
     * 근데 getRoleType()에서 반환 하는것은 Enum이다. String Type 으로 바꿔줄 필요 있다.
     * 그러고 나서 String을 다시 GrantedAuthority 으로 만들어주는게 필요하다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // 나의 경우는 Enum Role Type 이다. ...
        // user.getRoleType();
        // 근데 user ROLE이 Enum이다. String Type 으로 바꿔줄 필요 있다. 그래서 GrantedAuthority 으로 만들어주는게 필요하다.

        Collection<GrantedAuthority> collection = new ArrayList<>();
        // collection 안에는 GrantedAuthority이 들어가야 하니깐 새롭게 GrantedAuthority 객체를 생성해준다.
        collection.add(new GrantedAuthority() {
            // 정해진 메소드에서는 String Type만을 return 할 수 있다.
            @Override
            public String getAuthority() {
                return user.getRole().getAuthority(); // getRoleType 는 Enum 객체를 반환하지만 그 안에서 String 형태를 하고 있는 Authority 를 반환하고자 한다.
            }
        });

        return collection; // 그리고 Collection 형태로 하는 이유는 한 User에 대해서 여러 권한을 가지고 있을 수 있기 때문이다.
    }

    @Override // 로그인 한 사용의 비밀번호를 얻어서 Spring Security가 알아서 확인해준다.
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자의 로그인 id를 반환 unique 한 값 ( 즉 사용자의 이름이 아니다.)
    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    // 계정이 만료 되었니 물어보면은??그냥 아니요 라고 우선적으로 답을 한다. (TRUE 설정)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 니 계정 잠겻니?? 일단 아니요 라고 대답한다. TRUE 설정
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 니 계정의 비밀번호가 1년이 지냤니?? 이거 구현할꺼야?? 아니요 (TRUE 설정)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 니 계정이 활성화 되었니?? 이거 구현할꺼니?? 아니요 (TRUE 설정)
    @Override
    public boolean isEnabled() {

        // 언제 어떻게 활성화를 하냐면
        // 예를 들어서 우리 사이트에서 1년 동안 회원이 로그인 안하면 휴면 계정으로 바꾼다고 하면은
        // user 모델에 Timestamp으로 loginDate를 만들어줘야 한다.
        // 현재 시간 - user.getLoginDate() (마지막 로그인 시간)  : 1년을 초과하면 return false 한다.;

        return true;
    }


    /**
     * 아래 2개의 메소드는 OAuth2를 implements 함으로써, 2개가 새롭게 생성 되었다.
     *
     * Security Session 에는 Authentication 객체만 들어올 수 있다.
     * Authentication 객체는 2가지 타입을 가질 수 있다.
     * 1. UserDetails 2. OAuth2User
     * 단순히 추상적으로 UserDetails 혹은 OAuth2User 만으로는 User Object를 뽑아낼 수 없다.
     * 따라서, 우리는 UserDetails랑 같은 역할을 하는 구체적인 클래스 PrincipalDetails Class를 만들어서
     * 해당 클래스가 User Object를 포함하게 만들었다.
     * 그래서 PrincipalDetailsService에서 loadByUsername에서 return 값으로 UserDetails를 넣지않고
     * 같은 역할을 하고 있는 PrincipalDetails class를 넣어줬다.
     * 그렇게 하면 Spring Security Session에는 PrincipalDetails Class Type이 들어있고 User Object도 가지고 있다.
     * Security Session에 접근을 하면 User Object에 들어갈 수 있다.
     * 그렇지만, OAuth2로 로그인 했는 경우 OAuth2User Class Type으로 Security Session에 들어감으로 신경 써야할게 많아진다.
     * 그래서 해결 방법으로는 PrincipalDetails class를 UserDetails와 OAuth2User 둘다 implements 하도록 한다.
     * 그러면 해당 PrincipalDetails는 2가즤 Type을 가질 수 있게 된다.
     * 그러면, OAuth2User Type도 PrincipalDetails Type으로 바뀌기 때문에, User Object를 가지고 있다.
     * 따라서, Security Session에 접근할때는 PrincipalDetails로만 접근을 해도 User Object를 가져올 수 있다.
     *
     * OAuth2 로그인을 했을때 , PrincipalOauth2UserService에서 가지고 있는
     * super.loadUser(userRequest)를 호출하면 OAuth2User Class Type이 나오고
     * 해당 객체 안에는 여러개의 Attributes가 있다.
     * 그 중에서 sub이라는 여러개의 숫자로 이루어진것은 로그인한 사람이 들고 있는 구글의 primary key 이다. 즉, 아이디이다. 고유하다.
     * name , profile , email 등의 정보를 가지고 있다. 해당 데이터들이 Map<String, Object> 형태로 저장되어 있다.
     *
     * OAuth2User에서 가지고 있는 Map<String ,Object>를 통째로, 메소드에 값으로 할당 시켜줄것이다.
     *
     * Class Filed로 Map<String ,Object> 추가해준다. 추가해주고 변수 초기화는 생성자를 통해서 해준다.
     * 그러면 2개의 생성자가 만들어지는데, 첫번째 생성자 파라미터 값으로 User 같은 경우는 OAuth2 로그인을 하면 안생기기 때문에
     * 우리가 직접 custom으로 user object를 생성해 줄것이다.
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    @Override
    public String getName() {
        // getName은 Attributes가 들고 있는 sub를 return 하도록 하면 된다.
        // 근데 실질적으로 잘 안쓰이기 떄문에 null로 해도 상관없다.
        // return attributes.get("sub").toString();
        return user.getName() + "님 환영합니다!!";
    }
}
