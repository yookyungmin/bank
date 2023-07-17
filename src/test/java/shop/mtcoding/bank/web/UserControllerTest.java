package shop.mtcoding.bank.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.userReqDto;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.user.userReqDto.*;


//@Transactional

//@SpringBootTest하는곳엔 전부다 teardown.sql을붙여주자
//실행시점 : Beforeach 마다
@Sql("classpath:db/teardown.sql") //롤백이 아닌 truncate하기 위함 drop으로하면 create도 되기 떄문에 truncate
@ActiveProfiles("test") //없으면 dev로 설정된곳에서 saar이 생성되고 테스트시에 존재하는 유저 오류가 뜬다, 근데 없어도 teardown때문에 오류는 안뜸하지만 고정
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        userRepository.save(newUser("sar", "쌀"));
        em.clear(); //영속성 컨텍스트 초기화
    }

    @Test
    public void join_test() throws Exception{

        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("saar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("saar@nate.com");
        joinReqDto.setFullname("쌀");

        String requestBody = om.writeValueAsString(joinReqDto);
        System.out.println("테스트= "+requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        //    System.out.println("responseBody = test =  " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());

    }

    @Test
    public void join_fail_test() throws Exception{

        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("sar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("saar@nate.com");
        joinReqDto.setFullname("쌀");

        String requestBody = om.writeValueAsString(joinReqDto);
        System.out.println("테스트= "+requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        //    System.out.println("responseBody = test =  " + responseBody);

        //then
        resultActions.andExpect(status().isBadRequest());

    }

//   // private void dataSetting(){
//    userRepository.save(newUser("saar", "쌀"));
//    }
}
