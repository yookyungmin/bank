package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject{

    @Profile("dev") //prod mod에서는 실행 되면 안됨
    @Bean //DI
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
        return (args) -> {
            //서버 실행시 무조건 실행된다.
            User saar = userRepository.save(newUser("saar", "쌀"));
            User cos = userRepository.save(newUser("cos", "코스,"));
            User love = userRepository.save(newUser("love", "러브"));
            User admin = userRepository.save(newUser("admin", "관리자"));

            Account saarAccount1 = accountRepository.save(newAccount(1111L, saar));
            Account cosAccount = accountRepository.save(newAccount(2222L, cos));
            Account loveAccount = accountRepository.save(newAccount(3333L, love));
            Account saarAccount2 = accountRepository.save(newAccount(4444L, saar));

            Transaction withdrawTransaction1 = transactionRepository
                    .save(newWithdrawTransaction(saarAccount1, accountRepository));
            Transaction depositTransaction1 = transactionRepository
                    .save(newDepositTransaction(cosAccount, accountRepository));
            Transaction transferTransaction1 = transactionRepository
                    .save(newTransferTransaction(saarAccount1, cosAccount, accountRepository));
            Transaction transferTransaction2 = transactionRepository
                    .save(newTransferTransaction(saarAccount1, loveAccount, accountRepository));
            Transaction transferTransaction3 = transactionRepository
                    .save(newTransferTransaction(cosAccount, saarAccount1, accountRepository));
        };
    }
}
