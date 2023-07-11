package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    //JPA QUERY METHOD
    //select * from account where number = :number
    // checkpoint 리팩토리 예정(계좌 소유자 확인시에 쿼리가 두번 나가기 떄문에 join fetch)
    Optional<Account> findByNumber(Long number);
}
