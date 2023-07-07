package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

//인증필터  /api/login
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
            //JWT를 쓴다 하더라도 컨트롤러 진입을 하면 시큐리티의 권한체크 인증체크의 도움을 받을 수 있게 세션을 만든다.//인증이 되고 세션은 만들어지자마자
            // CustomReponseUtil.success(response, loginRespDto);에 의해 리턴되며 컨트롤러까지 안가고 없어진다 //
            // 이세션의 유효기간은 request하고 response하면 끝
            Authentication authentication = authenticationManager.authenticate(authenticationToken);  //강제 로그인
            //Authentication은 authenticationManager 매니저가 필요하다
            //강제 로그인
            //authentication.getPrincipal() < - LoginUser 객체가 담긴다

            return authentication; //successfulAuthentication() 호출

        }catch (Exception e){
            //unsuccessfulAuthentication 호출함
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }
    /*  ///api/login 하면
        1. UPAF 동작 username, password dto로 받는다 로그인 dto를 받는다
        2. 파싱해서 loginReqDto object로 바꾼다.
        3. 인증 토큰을 만든다// jwt 토큰 x authenticationToken
        4. authenticationToken 인증 토큰으로 authenticate() 요청하면
        userDetailService의 loadUserByUsername() 가 호출  -username, password DB확인
        5. 없으면 unsuccessfulAuthentication() 실행 (컨트롤러까지 도달이 안된상태라),
        6. 있으면 단순히 LoginUser 객체 생성하고 리턴
        7. 시큐리티 전용 세션에 담김.(전체 세션의 SecurityContextHolder 부분만 Authtication에 LoginUser를 담는다)authentication.getPrincipal()
        8. JWT 토큰을 생성하고 response 헤더에 담는다. successfulAuthentication()
        //response되면 세션에 저장된건 사라진다 stateless정책을 사용해서

     ==== 클라이언트가 JWT토큰을 갖는다.

          //api/s/hello
          1. BAF 동작, 토큰 검증
            //stateless 정책때문에 SecurityContextHolder 에 로그인하면서 loginUser가 저장되더있던게 사라져 있기떄문에 다시 생성하는 과정
            //세션에 인증과 권한 체크용으로만 저장을 하고 응답을 하면 사라진다.
            // /api/s/hello 면 인증인지 확인만 하면 되고 /api/admin/hello는 권한체크까지 해야하기 때문에



     */



    //로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomReponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED);
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
