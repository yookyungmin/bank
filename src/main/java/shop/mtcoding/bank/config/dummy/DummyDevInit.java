package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject{

    @Profile("dev") //prod mod에서는 실행 되면 안됨
    @Bean //DI
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository){
        return (args) -> {
            //서버 실행시 무조건 실행된다.
            User saar = userRepository.save(newUser("saar", "쌀"));
            User cos = userRepository.save(newUser("cos", "코스"));

            Account saarAccount = accountRepository.save(newAccount(1111L, saar));
            Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        };
    }
}
