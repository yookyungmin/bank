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

import static org.assertj.core.api.Assertions.*;
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

        //given //계좌입금 DTO
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


    @Test
    void 걔좌입금_test2() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01011112222");

        //stub1
        User saar = newMockUser(1L, "saar", "쌀");  //실행됨
        Account saarAccount1 = newMockAccount(1L, 1111L, 1000L, saar); //실행됨 -saarAccount(1000)
        //입금 계좌 확인
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(saarAccount1));//실행안됨 ->service 실행됨-> 1100원
        //서비스가 실행되어야 실행되는 부분


        //stub2 //독립적으로 테스트 하기 위해 유저를 한명 더 생성 사실상 같음
        User saar2 = newMockUser(1L, "saar", "쌀");
        Account saarAccount2 = newMockAccount(1L, 1111L, 1000L, saar2); //실행됨 -saarAccount(1000)
        Transaction transaction = newMockDepositTransaction(1L, saarAccount2); //실행됨 ssarAccount1 -> 1100원, transaction -> 1100원
        when(transactionRepository.save(any())).thenReturn(transaction);//실행안됨 ->
        //서비스가 실행되어야 실행되는 부분



        //when
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositRespDto);
        System.out.println("테스트= "+responseBody);

        //then
        assertThat(saarAccount1.getBalance()).isEqualTo(1100L);

    }

    //서비스 테스트를 보여드린 것은 기술적인 테크닉
    //진짜 서비스를 테스트 하고 싶으면, 내가 지금 무엇을 여기서 테스트 해야 하는지 명확히 구분(챔인부리)
    //DTO를 만드는 책임0> 서비스에 있지만 (서비스에서 DTO 검증 안할떄 - Controller 테스트 해볼거시니까)
    //DB 관련 된 것도 -> 서비스 것이 아니야 볼필요 없다
    //db 관련 된것을 조회해했을떄, 그값을 통해서 어떤 비즈니스 로직이 흘러가는 것이 있으면 ->stub으로 정의해서 테스트 해보면 된다.

    //DB 스텁, DB 스텁(가짜로 DB만들어서 deposit 검증,, 0원 검증)
    @Test
    public void 계좌입금_test3() {
        //given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        //when
        if(amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        account.deposit(100L);

        //then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }

    @Test
    void 계좌출금_test() {
        //given
//        AccountWithdrawReqDto accountWithdrawReqDto = new AccountWithdrawReqDto();
//        accountWithdrawReqDto.setNumber(1111L);
//        accountWithdrawReqDto.setPassword(1234L);
//        accountWithdrawReqDto.setAmount(100L);
//        accountWithdrawReqDto.setGubun("WITHDRAW");

        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User saar = newMockUser(1L, "saar", "쌀");
        Account saarAccount = newMockAccount(1L, 1111L, 1000L, saar);

        //when
        if(amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다.");
        }

        saarAccount.checkOwner(userId);
        saarAccount.checkSamePassword(password);
     //   saarAccount.checkBalance(amount);
        saarAccount.withdraw(amount);

        //then
        assertThat(saarAccount.getBalance()).isEqualTo(900L);
    }

    @Test
    void 계좌이체_test() {
        //given
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");

        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User saar = newMockUser(1L, "saar", "쌀");
        User cos = newMockUser(2L, "cos", "코스");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, saar);
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, cos);

        //출금 계쫘와 입금계좌가 동일하면 안된다
        if(accountTransferReqDto.getWithdrawNumber().longValue() == accountTransferReqDto.getDepositNumber().longValue()){
            throw new CustomApiException("출금계좌와 입금계좌가 동일합니다");
        }


        //0원인지 체크
        if(accountTransferReqDto.getAmount() <=0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다.");
        }

        //출금 소유자 확인
        withdrawAccount.checkOwner(userId);

//        //입금 소유자 확인
//        depositAccount.checkOwner(depositAccount.getUser().getId());

        //출금 계좌 비밀번호 확인
        withdrawAccount.checkSamePassword(accountTransferReqDto.getWithdrawPassword());

        //출금 계좌 잔액 확인하기
        withdrawAccount.checkBalance(accountTransferReqDto.getAmount());

        //이체하기
        withdrawAccount.withdraw(accountTransferReqDto.getAmount());
        depositAccount.deposit(accountTransferReqDto.getAmount());

        //then
        assertThat(withdrawAccount.getBalance()).isEqualTo(900L);
        assertThat(depositAccount.getBalance()).isEqualTo(1100L);
//      assertThat(depositAccount.getUser().getUsername()).isEqualTo("cos");
    }
}
