package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.userReqDto;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.user.userReqDto.*;


//@Transactional //전체 테스트시에 롤백으로 독립적인 테스트를 위해
@Sql("classpath:db/teardown.sql")//롤백이 아닌 truncate하기 위함 drop으로하면 create도 되기 떄문에 truncate
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om; // JSON으로 변경하기 위해 필요

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception{
        User user = userRepository.save(newUser("saar", "쌀"));
    }

    @Test //로그인 성공
    public void successfulAuthentication_test() throws Exception{
        //given
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("saar");
        loginReqDto.setPassword("1234");

        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("테스트 = " + requestBody);


        //when
        ResultActions resultActions = mvc.perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(jwtVo.Header);
        System.out.println("테스트 = " + responseBody);
        System.out.println("테스트 = " + jwtToken);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken); //jwt 토큰이 null 이 아니길 기대
        assertTrue(jwtToken.startsWith(jwtVo.TOKEN_PREFIX)); //접두사 확인
        resultActions.andExpect(jsonPath("$.data.username").value("saar"));//usernam이 saar인지 검증
    }


    @Test  //로그인 실패
    public void unsuccessfulAuthentication_test() throws Exception{

        //given
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("saar");
        loginReqDto.setPassword("12345");

        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("테스트 = " + requestBody);


        //when
        ResultActions resultActions = mvc.perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(jwtVo.Header);
        System.out.println("테스트 = " + responseBody);
        System.out.println("테스트 = " + jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized());
    }
}
