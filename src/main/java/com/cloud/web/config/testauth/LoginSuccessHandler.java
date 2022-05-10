package com.cloud.web.config.testauth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 로그인 성공 후 항상 실행되는 로직 설정.
 *
 * TestPrincipalDetails user = (TestPrincipalDetails) authentication.getPrincipal();
 * 이렇게 생각한 이유 loadbyusername()에서 TestPrincipalDetails 반환하고 있어서...
 */
@Configuration
public class LoginSuccessHandler  implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal(); // 이렇게 생각한 이유 loadbyusername에서 TestPrincipalDetails 반환하고 있어서...

        HttpSession session = request.getSession();

        session.setAttribute("user", user.getUser()); // user 객체 그대로 저장

        // 로그인 실패하더라도 다시 로그인 성공하면 해당 실패 session 삭제하는 method
        clearAuthenticationAttributes(request);

        response.sendRedirect("/"); // main으로 redirect

    }


    protected void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false); //  세션이 있다면 그 세션을 리턴하지만, 세션이 존재하지 않는다면 null을 리턴한다 .
        if(session==null) return; //세션이 null 즉, 세션에 에러가 없다면 그냥 return 된다.

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        //WebAttributes.AUTHENTICATION_EXCEPTION 이름 값으로 정의된 세션을 지운다.

    }






}
