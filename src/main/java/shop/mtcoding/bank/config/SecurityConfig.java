package shop.mtcoding.bank.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.util.CustomReponseUtil;


@Configuration
public class SecurityConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());
    //@slf4j로 대체 가능 하지만 테스트 할떄 문제가 생김

    @Bean  //Configuration 의 붙어있는 Bean만작동
    public BCryptPasswordEncoder passwordEncoder(){
        log.debug("디버그 : BCryptPasswordEncoder 빈등록");
        return new BCryptPasswordEncoder();
    }

    //추후 jwt 필터 등록 필요
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager)); //강제 세션 로그인을 위해 AuthenticationManager 필요
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    //jwt 서버를 만들 예정 Session 사용안함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{

        log.debug("디버그 : filterChain 빈등록");
        http.headers().frameOptions().disable(); //iframe 허용 안함
        http.csrf().disable(); // enable이면 post 맨 적용안함(메타코딩 유튜브 시큐리티 강의)
        http.cors().configurationSource(configurationSource()); //자바스크립트에서 요청하는걸 막겠따는건데 허용

        //jsessionId를 서버쪽에서 관리안하겠다는 뜻
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //react, 앱 같은데서 요청을 받을거라 form로그인 방식 x
        http.formLogin().disable(); //비허용

        //브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable(); //비허용

        // 필터 적용
        http.apply(new CustomSecurityFilterManager());

        //인증실패
        http.exceptionHandling().authenticationEntryPoint((request, response,outhException)->{//commence 메서드의 매개변수
            CustomReponseUtil.fail(response, "로그인을 진행해주세요", HttpStatus.UNAUTHORIZED);
        });
        //여기서 터지면 Authorization 인가는 통과했다는 뜻
        //헤더에 jwt토큰이 없어도 dofilter를 통해서 컨트롤러까지 갔다가 해당 코드가 낚아챔
        //postman에서 에러 나오는걸 통제하기위해 AuthenticationEntryPoint의 제어권을 뺏는다

        //권한 실패
        http.exceptionHandling().accessDeniedHandler((request, response, e)->{
            CustomReponseUtil.fail(response, "권한이 없습니다", HttpStatus.FORBIDDEN);
        });

        http.authorizeHttpRequests()
                .antMatchers("/api/s/**").authenticated()//인증이 필요한 부분
                .antMatchers("/api/admin/**").hasRole(""+ UserEnum.ADMIN)//최근 공식문서는 ROLE_안붙여도됨  //prefix
                .anyRequest().permitAll(); //나머지 요청 모두 허용

        return http.build();

        //끝난건 아니고 jwt 셋팅, 필터 적용도 있어야함
    }

    public CorsConfigurationSource configurationSource(){
        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterchain 등록");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*"); //모든 헤더를 다 받겠다
        configuration.addAllowedMethod("*"); //모든 메서드 get,post, put, delete(자바스크립트 요청 허용
        configuration.addAllowedOriginPattern("*"); //모든 ip주소 허용(프론트 엔드 ip만 허용 react, 핸드폰은 자바스크립트로 요청하는게 아니라 cors에 안걸림 )
        configuration.setAllowCredentials(true); //클라이언트에서 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //모든 주소 요청에, configuration 설정을 넣어주겠다.

        return source;
    }
}
