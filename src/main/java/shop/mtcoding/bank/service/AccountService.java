package shop.mtcoding.bank.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;
import shop.mtcoding.bank.util.CustomDateUtil;

import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    public AccountListRespDto 계좌목록보기_유저별(Long userId){
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다.")
        );
        // 유저의 모든 계좌 목록 //리턴 하기 위해 DTO 필요
        List<Account> accountListPS = accountRepository.findByUser_id(userId);

        return new AccountListRespDto(userPS, accountListPS);
    }



    @Transactional //커밋이 되어야 하기 떄문에 readonly True x
    public AccountSaveRespDto 계좌등록(AccountSaveReqDto accountSaveReqDto, Long userId){
        //User가 DB에 있는지 검증
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        ); //DB에서 가져온 정보라 PS를 붙임

        //해당 계좌가 DB에 있는 중복여부를 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if(accountOP.isPresent()){ //존재하면
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }
        // 계좌등록
        Account accountPS = accountRepository.save(accountSaveReqDto.ToEntity(userPS));

        //DTO 응답
        return new AccountSaveRespDto(accountPS);
    }

    @Transactional //삭제, db의 영향을 주니 Transactional
    public void 계좌삭제(Long number, Long userId){
        //계좌 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        );

        //게좌 소유자 확인
        accountPS.checkOwner(userId); //saar이 이미 조회 되었기 떄문에 LazyLoading 할떄 select 쿼리가 발동 안한다

        //계좌 삭제
        //accountRepository.deleteById(accountPS.getId());
        accountRepository.deleteById(accountPS.getId());
    }

    @Transactional
    public AccountDepositRespDto 계좌입금(AccountDepositReqDto accoutDepositReqDto){ //ATM -> 누군가의 계좌
            //0원 체크
            if(accoutDepositReqDto.getAmount() <= 0L){
                throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
            }
            //입금 계좌 있는지 확인
             Account depositAccountPS = accountRepository.findByNumber(accoutDepositReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다."));

            //입금(해당 계좌 balance조정 - update문 더티체킹)
            depositAccountPS.deposit(accoutDepositReqDto.getAmount());

            //거래 내역 남기기
            Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(depositAccountPS)
                .witdrawAccountBalance(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accoutDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accoutDepositReqDto.getNumber()+"")
                .tel(accoutDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositRespDto(depositAccountPS, transactionPS);
    }

    @Transactional
    public AccountWithdrawRespDto 계좌출금(AccountWithdrawReqDto accountWithdrawReqDto, Long userId){

        //0원인지 체크
        if(accountWithdrawReqDto.getAmount() <=0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다.");
        }

        //출금 계좌 확인하기
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        //출금 소유자 확인
        withdrawAccountPS.checkOwner(userId);

        //출금 계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.getPassword());

        //출금 계좌 잔액 확인하기
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());

        //출금
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        //거래 내역 남기기(내 계좌에서 atm으로 출금
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .witdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawReqDto.getNumber()+ "")
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        //DTO응답
        return new AccountWithdrawRespDto(withdrawAccountPS, transactionPS);
    }

    @Transactional
    public AccountTransferRespDto 계좌이제(AccountTransferReqDto accountTransferReqDto, Long userId){

        //출금 계쫘와 입금계좌가 동일하면 안된다
        if(accountTransferReqDto.getWithdrawNumber().longValue() == accountTransferReqDto.getDepositNumber().longValue()){
            throw new CustomApiException("출금계좌와 입금계좌가 동일합니다");
        }


        //0원인지 체크
        if(accountTransferReqDto.getAmount() <=0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다.");
        }

        //출금 계좌 확인하기
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferReqDto.getWithdrawNumber())
                .orElseThrow(
                        () -> new CustomApiException("출금계좌를 찾을 수 없습니다"));

        //출금 계좌 확인하기
        Account depositAccountPS = accountRepository.findByNumber(accountTransferReqDto.getDepositNumber())
                .orElseThrow(
                        () -> new CustomApiException("입금계좌를 찾을 수 없습니다"));

        //출금 소유자 확인
        withdrawAccountPS.checkOwner(userId);


//        //입금 소유자 확인
//        depositAccountPS.checkOwner(depositAccountPS.getUser().getId());

        //출금 계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountTransferReqDto.getWithdrawPassword());

        //출금 계좌 잔액 확인하기
        withdrawAccountPS.checkBalance(accountTransferReqDto.getAmount());

        //이체하기
        withdrawAccountPS.withdraw(accountTransferReqDto.getAmount());
        depositAccountPS.deposit(accountTransferReqDto.getAmount());

        //거래 내역 남기기(내 계좌에서 atm으로 출금
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(depositAccountPS)
                .witdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountTransferReqDto.getAmount())
                .gubun(TransactionEnum.TRANSFER)
                .sender(accountTransferReqDto.getWithdrawNumber()+ "")
                .receiver(accountTransferReqDto.getDepositNumber()+ "")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        //DTO응답
        return new AccountTransferRespDto(withdrawAccountPS, transactionPS);
    }
}
