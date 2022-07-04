package com.cloud.web.config.Security;

import com.cloud.web.config.Oauth2.PrincipalOauth2UserService;
import com.cloud.web.config.auth.LoginFailureHandler;
import com.cloud.web.config.auth.LoginSuccessHandler;
import com.cloud.web.config.auth.WebAccessDeniedHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URLEncoder;

/**
 * .defaultSuccessUrl("/") 를
 * successHandler(new LoginSuccessHandler()) 여기에서 처리하도록 구현했음
 */


@Configuration
@EnableWebSecurity // 해당 클래스가 시큐리티 필터 체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true , prePostEnabled = true)  // secured 어노테이션 활성화 Controller 주소위에 @secured라고 하면 // preAuthorize 활성화 또한 동시에 PostAuthorize 활성화 근데 해당 PostAuthorize는 해당 메소드가 끝나고 실행해서 추천 X
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private  LoginFailureHandler loginFailureHandler; // 로그인 실패시 처리하는 클래스

    private  LoginSuccessHandler loginSuccessHandler; // 로그인 성공시 처리하는 클래스

    private  WebAccessDeniedHandler webAccessDeniedHandler; // 권한 없을때 처리하는 클래스

    private PrincipalOauth2UserService principalOauth2UserService;

    private BCryptPasswordEncoder encodePwd;



    @Autowired
    public SecurityConfig(LoginFailureHandler loginFailureHandler, LoginSuccessHandler loginSuccessHandler,
                          WebAccessDeniedHandler webAccessDeniedHandler,PrincipalOauth2UserService principalOauth2UserService
                          ,BCryptPasswordEncoder encodePwd) {
        this.loginFailureHandler = loginFailureHandler;
        this.loginSuccessHandler = loginSuccessHandler;
        this.webAccessDeniedHandler = webAccessDeniedHandler;
        this.principalOauth2UserService = principalOauth2UserService;
        this.encodePwd = encodePwd;
    }




    // 시큐리티에서 js,css,image,font 등의 리소스를 제어하기 위한 접근은 무시하도록 한다.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().antMatchers( "/static/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                // 로그인 하지 않은 user 사용자가 해당 주소로 갈려고 하면
                // 최소한은 로그인은 되어야 하기 때문에 시큐리티 로그인 기본 경로인 /login으로 넘어가고,
                // 넘어가서 로그인이 인증이 되면 리다이렉트 주소로 접속을 시도한 시점의 URL로 되돌아온다.
                .antMatchers("/foodForm/**","/myPage/**").authenticated()
                // 권한이 없으면 403 나와야 한다. hasRole은 앞에 "ROLE_"을 붙인 상태에서 확인하고,
                // hasAnyAuthority는 붙지 않는 상태에서 권한을 확인하고자 한다.  그래서 접근은 가능하지만 @Secured 기능에서 사용되지 않는다.
                .antMatchers("/adminPage").access("hasRole('ROLE_ADMIN')")
                // 나머지 요청에 대해서는 로그인 없이도 가능하다.
                .anyRequest().permitAll()

                .and() // 로그인 조차 하지 않았을때 여기로 넘어온다. ADMIN만 접근 가능한 요청을 USER로 로그인 했는데 권한이 없는 경우, 403 Error가 난다.
                .formLogin()
                // 일반적인 로그인 요청이 아닌, 최소한의 로그인 권한이 없어서 해당 설정으로 로그인이 필요하다고 들어오면
                // /login 요청으로 가도록 하고, "로그인 필요합니다" 라는 문구를 추가적으로 보내도록 한다.
                // controller에서 지정한 /login 경로로 가서 로그인하기 위한 loginForm.html를 반환한다.
                .loginPage("/login?alertmsg=" + URLEncoder.encode("로그인 필요합니다.", "UTF-8"))
                // 해당 loginForm.html에서 사용자가 데이터를 넣고, post 요청시 /loginProccess로 하게 되면
                // Spring Security가 그 요청을 낚아채서, 로그인을 대신 수행한다.
                // /loginProcess 같은 경우는 사용자가 직접 설정해도 되지만, 대신 html의 post 요청 경로랑 일치해야 한다.
                .loginProcessingUrl("/loginProcess")
                // 로그인이 실패한 경우, 해당 클래스에서 처리하도록 위임한다.
                .failureHandler(loginFailureHandler)
                // 로그인이 성공한 경우, 해당 클래스에서 처리하도록 위임한다.
                .successHandler(loginSuccessHandler)
                //.defaultSuccessUrl("/") // 로그인이 성공하면 이동할 페이지를 지정한다. 하지만 지금은 해당 처리를 loginSuccessHandler에서 처리한다.

                .and()
                .logout() // "/logout"이 default 값이다.
                .logoutSuccessUrl("/") //main으로 돌아가게 설정
                // JSESSIONID 이름으로 된 쿠키를 제거한다.
                .deleteCookies("JSESSIONID")

                .and()
                .exceptionHandling().accessDeniedHandler(webAccessDeniedHandler) // 모든 403에 대한 에러를 처리하는 핸들러
                .and()
                .oauth2Login()
                .loginPage("/login")
                // 구글 로그인이 완료된 뒤의 후처리가 필요함
                // 1. 코드 받기 (인증) , 2. 엑세스 토큰(권한), 3. 사용자 프로필 정보를 가져오고
                // 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
                // 4-2. (이메일, 전화번호, 이름, 아이디 ) 쇼핑몰 -> (집주소) , 백화점몰 -> (VIP 등급, 일반등급)
                // 사용자로부터 추가적인 데이터를 받아서 회원가입을 진행 해야한다.
                // 중요** oauth2 client library를 사용하면, 구글 로그인이 완료가 되면
                // 코드 X -> (엑세스토큰 + 사용자프로필정보)를 한번에 받는다. library의 장점(편리하다.)
                .userInfoEndpoint()
                .userService(principalOauth2UserService); // 파라미터 타입이, OAuth2UserService여야 한다.



    }
}
