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
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .witdrawAccountBalance(null)
                .amount(accoutDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(depositAccountPS.getNumber()+"")
                .tel(accoutDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositRespDto(depositAccountPS, transactionPS);
    }

    @Getter
    @Setter
    public static class AccountDepositRespDto{
        private Long id; //계좌ID
        private Long number; //계좌번호
        private TransactionDto trasaction;  //dto 안에 엔티티 들어올수 없다, 순환참조 될수 있따

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.trasaction = new TransactionDto(transaction); //엔티티를 Dto로 변환
        }

        @Getter
        @Setter
        public class TransactionDto{
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createdAt;
            @JsonIgnore //내 계좌가 아니기에 가려준다 json 응답시 테스트시에는 제거 하고 확인
            private Long depositAccountBalance;


            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")  //
        private String gubun;
        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }


}
