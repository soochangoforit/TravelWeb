package com.cloud.web.config.testSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 해당 클래스가 시큐리티 필터 체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true , prePostEnabled = true)  // secured 어노테이션 활성화 Controller 주소위에 @secured라고 하면 // preAuthorize 활성화 또한 동시에 PostAuthorize 활성화 근데 해당 PostAuthorize는 해당 메소드가 끝나고 실행해서 추천 X
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    // 시큐리티 에서 비밀번호를 암호화 하기 위해서 사용한다.
    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").authenticated() // 여기서 굉장히 중요한 기능이!! 로그인 하지 않은 user 사용자가 해당 주소로 갈려고 하면 최소한은 로그인은 되어야 하기 때문에 /loginForm으로 넘어가고, 넘어가서 로그인이 인증이 되면 리다이렉트 주소로 /user로 온다. (접속을 시도한 시점의 URL로 온다.)
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 권한이 없으면 403 나와야 한다. hasRole은 앞에 "ROLE_"을 붙인 상태에서 확인하고, hasAnyAuthority는 붙지 않는다.?? 그래서 접근은 가능하지만 @Secured 기능에서 사용되지 않는다.
                .anyRequest().permitAll()

                .and() // 로그인 조차 하지 않았을때 여기로 넘어온다. USER로 로그인 했는데 권한이 없는 경우, 403 Error가 난다.
                .formLogin()
                .loginPage("/loginForm") // URL에 "/logout" 이라고 하면 "/loginForm" 경로로 다시 돌아가고 결국 LoginForm이 나타난다.
                .loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 대신 낚아채서 대신 로그인을 진행한다. 사용자 원하느걸로 해도 ㄱㅊ 근데 html에서의 action이랑 같은 설정으로 해야한다.
                .defaultSuccessUrl("/"); //로그인이 시큐리티가 가로채서 성공을 하면 "/"위치로 돌아간다. (로그인 성공했을 경우) , GET 방식으로! 즉, 이방식은 이 /loginForm으로 와서 로그인을 하면 "/"로 보내줄건데 , 로그인을 요하는 특정 페이지에 의해서 로그인을 하는 경우, 로그인 성공시 해당 특정 페이지로 다시 보내준다.(사용자가 원했던 페이지로)

    }
}
