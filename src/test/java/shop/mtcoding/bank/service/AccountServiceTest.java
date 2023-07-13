package shop.mtcoding.bank.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static shop.mtcoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;

@ExtendWith(MockitoExtension.class) //전체를 메모리에 띄울 필요 없기때문에
public class AccountServiceTest extends DummyObject {

    @InjectMocks //모든 Mock 들이 InjectMocks로 주입
    private AccountService accountService;

    @Mock
    private UserRepository userRepository; //가짜 userRepository가 accountService 에 주입된다

    @Mock
    private AccountRepository accountRepository;

    @Spy //진짜 객체를 InjectMocks에 주입입
    private ObjectMapper om;

    @Test
    public void 계좌등록_test() throws Exception {
        //given
        Long userId = 1L;
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        //stub1 //유저 확인
        User saar = newMockUser(userId, "saar", "쌀"); //아이디를 넣기 위해 newUser가 아닌 mockUser
        when(userRepository.findById(any())).thenReturn(Optional.of(saar));

        //stub2 //계좌 확인
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty()); //등록된 계자가 없길 기대한다

        //stub3
        Account saarAccount = newMockAccount(1L, 1111L, 1000L, saar);
        when(accountRepository.save(any())).thenReturn(saarAccount);


        //when
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto, userId);

        String responseBody = om.writeValueAsString(accountSaveRespDto);
        System.out.println(responseBody);

        //then
        assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    public void 계좌삭제_test() throws Exception{

        //given
        Long number = 1111L;
        Long userId = 2L;

        //stub //무언갈 기대한다 리턴이 있을시
        User saar = newMockUser(1L, "saarr", "쌀");
        Account saarAccount = newMockAccount(1L, 1111L, 1000L, saar);

        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(saarAccount));
        //saaAccount 리턴을 가정

        //when
        assertThrows(CustomApiException.class, ()->accountService.계좌삭제(number, userId));
        //Exception이 발생하면 정상
    }
}
