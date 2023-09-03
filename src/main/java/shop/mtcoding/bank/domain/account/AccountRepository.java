package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    //JPA QUERY METHOD
    //select * from account where number = :number
    // 신경안써도됨 리팩토리 예정(계좌 소유자 확인시에 쿼리가 두번 나가기 떄문에 join fetch)
    //account.getUser().getId()
    //이프로그램 같은경우엔 account select할떄 user를 가지고올일이 없다고 봐야한다. 그래서 리팩토링을 할 필요 없다

    //join fetch를 하면 조인에서 객체 에 값을 미리 가져올 수 있다.
    //acoount 조회시 user를 계쏙 가져오게 될 경우에 아래 쿼리를 날리면 select 할떄 lazy 한걸 미리  당겨온다
    //@Query("SELECT ac FROM Account ac JOIN FETCH ac.user u WHERE ac.number = :number")
    Optional<Account> findByNumber(Long number);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ac from Account ac where ac.number = :number")
    Optional<Account> findByNumberWithPessimisticLock(Long number);

    //jpa query method
    //select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}
