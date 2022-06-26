package com.cloud.web.config.auth;

import com.cloud.web.domain.User;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증, 인가 거부된 요청시 이동될 사용되는 Class
 * msg와 nextPage attribute를 이용하여 alert를 띄우며 다음 페이지도 이동됩니다.
 *
 * 접근 권한이 없는(=인가되지 않은) 사용자의 경우 403 http status code를 응답하면서
 * nextPage로 redirect 합니다.
 * (ROLE_ADMIN 권한을 가지고 있지 않은 사용자가 관리자 메뉴에 접속했을 때에 관한 처리를 해주는 부분입니다.)
 */
@Configuration
public class WebAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
                       AccessDeniedException ade) throws IOException, ServletException {

        res.setStatus(HttpStatus.FORBIDDEN.value());

        if(ade instanceof AccessDeniedException){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // user인 사용자가 main 페이지에 있는 관리자 페이지에 접근하고자 할때 "/adminPage"로 접근하고자 할때 "/"으로 돌려보낸다.
            if(authentication != null && ((PrincipalDetails)authentication.getPrincipal()).getUser().getRole().getAuthority().equals("ROLE_USER")){
                req.setAttribute("msg","접근 권한 없는 사용자입니다.");
                req.setAttribute("nextPage","/");
            }

        }
        req.getRequestDispatcher("/login").forward(req,res);
    }
}
