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
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;

@ExtendWith(MockitoExtension.class) //전체를 메모리에 띄울 필요 없기때문에
public class AccountServiceTest extends DummyObject {

    @InjectMocks //모든 Mock 들이 InjectMocks로 주입
    private AccountService accountService;

    @Mock
    private UserRepository userRepository; //가짜 userRepository가 accountService 에 주입된다

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

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


    //AcCOUNT - balance 변경 했는지 확인
    //Transaction -> balance 변경 확인
    @Test
    public void 게좌입금_test() {

        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();

        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01011112222");

        //stub
        User saar = newMockUser(1L, "saar", "쌀");  //실행됨
        Account saarAccount1 = newMockAccount(1L, 1111L, 1000L, saar); //실행됨 -saarAccount(1000)
        //입금 계좌 확인
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(saarAccount1));//실행안됨 ->service 실행됨-> 1100원
        //서비스가 실행되어야 실행되는 부분

        //stub (스텁이 진행될떄마다 연결될 객체는 새로만들어서 주입하기 - 타이밍 떄문에 꼬인다)
        //트랜잭션이 저장되면 트랜잭션이 리턴될것이다
        Account saarAccount2 = newMockAccount(1L, 1111L, 1000L, saar); //실행됨 -saarAccount(1000)
        Transaction transaction = newMockDepositTransaction(1L, saarAccount2); //실행됨 ssarAccount1 -> 1100원, transaction -> 1100원
        when(transactionRepository.save(any())).thenReturn(transaction);//실행안됨 ->
        //서비스가 실행되어야 실행되는 부분

        //when
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);
        System.out.println("테스트 : 트랜잭션 잔액 " + accountDepositRespDto.getTrasaction().getDepositAccountBalance());
        System.out.println("테스트 = 계좌쪽 잔액 " + saarAccount1.getBalance());
        System.out.println("테스트 = 계좌쪽 잔액" + saarAccount2.getBalance());

        //then
        assertThat(saarAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositRespDto.getTrasaction().getDepositAccountBalance()).isEqualTo(1100L);
    }
}
