package shop.mtcoding.bank.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//@AutoConfigureMockMvc//Mock(가짜) 환경에 MockMvc가 등록됨
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@SpringBootTest

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTest {


    //가짜 환경에서 등록된 MockMvc DI
    @Autowired
    private MockMvc mockMvc;


    //서버는 일관성 있게 에러가 리턴되어야 한다
    //내가 모르는 에러가 프론트한테 날아가지 않게 내가 직접 다 제어하자
    @Test
    public void authentication_test() throws Exception {
        //given //테스트를 위한 기본 데이터

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello")); //api를 테스트할수 있는 메소드
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        System.out.println("httpStatusCode = " + httpStatusCode);
        System.out.println("테스트="+responseBody);

        //then
        assertThat(httpStatusCode).isEqualTo("401");
    }

    @Test
    public void authorization_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/hello")); //api를 테스트할수 있는 메소드
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        System.out.println("httpStatusCode = " + httpStatusCode);
        System.out.println("테스트="+responseBody);

        //then
        assertThat(httpStatusCode).isEqualTo(401);
    }
}
