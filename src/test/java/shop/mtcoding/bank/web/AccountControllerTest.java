package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.EntityManager;
import javax.swing.undo.CannotUndoException;
import javax.validation.constraints.AssertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){

        User saar = userRepository.save(newUser("saar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스"));

        Account saarAccount = accountRepository.save(newAccount(1111L, saar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        //삭제 테스트가 진행 될떈 persist context에 머ㅁ르지 않고 깨끗해야 함

        em.clear(); //persist context 있는 것들 날리기
   }


    //Jwt 토큰을 날려 인증 필터 -> 시큐리시 세션 생성
    //헤더에 jwt토큰이 없어도 dofilter를 통해서 컨트롤러까지 갔다가 해당 코드가 낚아챔, 그래서 토큰을넣어줄 필욘 없다 테스트시
    //setupBefore=TEST_METHOD (setUp메서드 실행전에 수행)
    //setupBefore = TestExecutionEvent.TEST_EXECUTION (saveAccount_Test 메서드 실행전에 수행)
    @WithUserDetails(value = "saar", setupBefore = TestExecutionEvent.TEST_EXECUTION)  //디비에서 username=saar 조회를 해서 세션에 담아주는 어노테이션
    @Test
    public void saveAccount_test() throws Exception{

        //given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);

        String requestBody= om.writeValueAsString(accountSaveReqDto);
        System.out.println("requestBody = " + requestBody);


        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }


    //테스트 시에는 insert 한것들이 전부 pc에 올라간다(영속화)
    //영속화 된것들을 초기화 해주는 것이 개발 모드와 동일 환경으로 테스트를 할수 있게 해준다.
    //최초의 select 는 쿼리가 발생하지만 - pc에 있으면 1차 캐시를 함(영속성 컨텍스트 내부에는 엔티티를 보관하는장소를 1차캐시)
    // lazy로딩은 쿼리도 발생안함 - pc에 있다면!
    //lazy로딩을 할떄 pc에 없다면 쿼리가 발생함
    @WithUserDetails(value = "saar", setupBefore = TestExecutionEvent.TEST_EXECUTION)  //디비에서 username=saar 조회를 해서 세션에 담아주는 어노테이션
    @Test
    public void deleteAccount_test() throws Exception {
        //given
        Long number = 1111L;

        //when
        ResultActions resultActions = mvc.perform(delete("/api/s/account/" + number));  //삭제는 delete는 http에 바디가 없다
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        //Junit 테스트에서 delete쿼리는 가장 마지막 실행되면 발동안됨
        Assertions.assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(
                () ->new CustomApiException("계좌를 찾을 수 없습니다")
        )); //삭제가 되면 계좌를 찾을수 없어야 하기 떄문에

    }

    /*
    setUp에 의해 insert 4건 발생 - > em.cleaer() ps 초기화
    삭제 테스트 진행시 @WithUserDetails value 값이 cos 조회로 쿼리발생
    saarAccount 조회 쿼리발생 <- 뭐지?

    pc에는 -> cos, saarAcoount 존재

    //user를 Lazy로 설정 해놔서
    saarAccount.getUser.getId(); //상관이 없음
    saarAccount.getUser.getUsername(); //조회쿼리 발생 -> pc에 saar이 없으니까



     */
}
