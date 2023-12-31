package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {


    //출금
    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository){
        account.withdraw(100L); // 1000원이 있다면 900원이 됨
        //서비스레이어에서 값을 바꾸는게 아니기 떄문에 더티체킹이 안됨 직접 바꿔주어ㅑ함

        //Repository Test에는 더티체킹 됨
        //controller test에서는 더티체킹 안됨
        //더티체킹이 안되어서 업데이트 쿼리 날리게끔 작성
        if(accountRepository != null){
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(account)
                .depositAccount(null)
                .witdrawAccountBalance(account.getBalance())
                .depositAccountBalance(null)
                .amount(100L)
                .gubun(TransactionEnum.WITHDRAW)
                .sender(account.getNumber() + "")
                .receiver("ATN")
                .build();

        return transaction;

    }

    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository){
        withdrawAccount.withdraw(100L); // 1000원이 있다면 900원이 됨
        depositAccount.deposit(100L);
        //서비스레이어에서 값을 바 꾸는게 아니기 떄문에 더티체킹이 안됨 직접 바꿔주어ㅑ함

        if(accountRepository != null){
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .witdrawAccountBalance(withdrawAccount.getBalance())
                .depositAccountBalance(depositAccount.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.TRANSFER)
                .sender(withdrawAccount.getNumber() + "")
                .receiver(depositAccount.getNumber() + "")
                .build();

        return transaction;

    }

    //입금
    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository){
        account.deposit(100L); // 1000원이 있다면 900원이 됨
        //서비스레이어에서 값을 바꾸는게 아니기 떄문에 더티체킹이 안됨 직접 바꿔주어ㅑ함

        if(accountRepository != null){
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(account)
                .witdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01022227777")
                .build();

        return transaction;

    }

    //계좌 1111L 1000원
    //입금 트랜잭션 -> 1100원 변경 -> 입금 트랜잭션 히스토리가 생성되어야 함
    protected static Transaction newMockDepositTransaction(Long id, Account account){
        account.deposit(100L);
        //입금 트랜잭션은 입금이 우선적으로 발생되어야 하기 떄문에

        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .depositAccount(account)
                .witdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber()+"")
                .tel("01088887777")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return transaction;
    }

    protected  User newUser(String username, String fullname){ //엔티티 save용
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword )
                .email(username+"saar@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();

        //id는 객체만들어서 save 하면 자동으로 id, localdatetime만들어지기떄문에 삭제
    }

    protected  User newMockUser(Long id, String username, String fullname){//가짜로 만들어낼떄
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword )
                .email(username+"saar@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Account newAccount(Long number, User user){
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }  //save할때 사용하기위함

    protected Account newMockAccount(Long id, Long number, Long balance, User user){
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }  //select시 나올 가짜

}
