package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.user.UserRespDto;
import shop.mtcoding.bank.dto.user.userReqDto;
import shop.mtcoding.bank.util.CustomReponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static shop.mtcoding.bank.dto.user.UserRespDto.*;
import static shop.mtcoding.bank.dto.user.userReqDto.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) { //생성자
        super(authenticationManager);
        setFilterProcessesUrl("/api/login"); //url 주소 /login 에서 /api/login으로 변경
        this.authenticationManager = authenticationManager;
    }

    //post : /login 동작  //로그인의 데이터를 받아 강제 로그인진행
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
            log.debug("디버그: attemptAuthentication 호출됨");
        try {
            ObjectMapper om = new ObjectMapper(); //로그인하면 request안에 json이있어서 om이 필요
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class); //LoginReqDto.class타입

            //강제 로그인
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(), loginReqDto.getPassword());  //토큰 생성

            //UserDetailService 의 LoadUserByUsername을 호출
            //JWT를 쓴다 하더라도 컨트롤러 진입을 하면 시큐리티의 권한체크 인증체크의 도움을 받을 수 있게 세션을 만든다.
            // 이세션의 유효기간은 request하고 response하면 끝
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            //Authentication은 authenticationManager 매니저가 필요하다

            return authentication;

        }catch (Exception e){
            //unsuccessfulAuthentication 호출함
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }


    //로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomReponseUtil.unAuthentiaction(response, "로그인 실패");
    }

    //return authentication이 잘 작동하면 successfulAuthentication 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("디버그: successfulAuthentication 호출됨");
        //로그인이 됐다, 세션이 만들어짐

        LoginUser loginUser = (LoginUser) authResult.getPrincipal(); //로그인유저
        String jwtToken = JwtProcess.create(loginUser); //jwtToken 토큰 생성

        response.addHeader(jwtVo.Header, jwtToken); //토큰을 헤더에 담는다

        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser());  //유저 정보를 넣어주면된다

        CustomReponseUtil.success(response, loginRespDto);  //jwt 토큰 필터 구현완료
    }
}
