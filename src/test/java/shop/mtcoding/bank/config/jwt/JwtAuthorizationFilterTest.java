package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class JwtAuthorizationFilterTest {

    @Autowired
    private ObjectMapper om; // JSON으로 변경하기 위해 필요

    @Autowired
    private MockMvc mvc;

    @Test  //doFilterInternal 테스트
    public void authorization_success_test() throws Exception{
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser= new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);//클라이언트가 토큰을 들고 인가를 받기 떄문에 토큰 생성
        System.out.println("테스트jwtToken = " + jwtToken);
        //when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test").header(jwtVo.Header, jwtToken));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test  //doFilterInternal 테스트
    public void authorization_fail_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));

        //then
        resultActions.andExpect(status().isUnauthorized()); //401
    }

    @Test  //doFilterInternal 테스트
    public void authorization_admin_test() throws Exception{
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser= new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);//클라이언트가 토큰을 들고 인가를 받기 떄문에 토큰 생성
        System.out.println("테스트jwtToken = " + jwtToken);
        //when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test").header(jwtVo.Header, jwtToken));

        //then
        resultActions.andExpect(status().isForbidden());
    }
}
