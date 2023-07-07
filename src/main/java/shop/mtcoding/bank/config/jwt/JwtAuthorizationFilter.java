package shop.mtcoding.bank.config.jwt;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//모든 주소에서 동작함(토큰검증)
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(isHeaderVerify(request, response)){//토큰이 존재한다면
            //토큰 접두사 제거
            String token = request.getHeader(jwtVo.Header).replace(jwtVo.TOKEN_PREFIX,"");
            LoginUser loginUser = JwtProcess.verify(token);  //토큰검증


            //UserDetails 타입 or username) 을 넣을수 있는데 NULL이기떄문에  loginUser = userdetails 통쨰로 넣음
            //임시 세션 만들기기//토큰도 있고 검증도 되었으니 인증된 유저로 보면 된다 // 강제로 토큰에 세션을 만든다
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication); //강제 로그인

            //필터를 다 타면 컨트롤러로가낟

        } //SecurityConfig 의 jwt 필터 등록 필요

        chain.doFilter(request, response); // 필터가없으면 다음 필터로 간다

    }

    //헤더 검증하는 메서드
    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(jwtVo.Header);

        if (header == null || !header.startsWith(jwtVo.TOKEN_PREFIX)) { //헤더가 널이거나 시작값이 Bearer 이 아니면
            return false;
        }else{
            return true;
        }
    }
}
