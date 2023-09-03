package shop.mtcoding.bank.domain.transaction;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.QueryDSLConfig;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

//Repository 테스트를 하기떄문에 스프링부트테스트나 모키토가 필요 없다
//jpa테스트할떈 truncate를 쓰면 안된다. //롤백 되서 데이터는 초기화 되는데 autocretment가 초기화 안됨
//@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@DataJpaTest //DB관련된 BEAN이 올라온다
@Import(QueryDSLConfig.class)
public class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    private TransactionRepository transactionRepository;
    //dao인터페이스를 상속하고 있어서 TransactionRepositoryImpl 를 테스트 예정

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        autoincrementReset();
        dataSetting();
        em.clear(); //퍼시스트 컨텍스트 초기화, 레포 테스트에서 필수
    }

    @Test
    public void findTransactionList_all_test() {
        //given
        Long accountId = 1L;
        //when
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountId, "ALL", 0);

        transactionListPS.forEach((t) -> {
            System.out.println("테스트 : id : "+ t.getId());
            System.out.println("테스트 : amount :"+ t.getAmount());
            System.out.println("테스트 : sender :"+ t.getSender());
            System.out.println("테스트 : receiver :"+ t.getReceiver());
            System.out.println("테스트 : withdrawAccount 잔액 :"+ t.getWitdrawAccountBalance());
            System.out.println("테스트 : depositAccount 잔액 :"+ t.getDepositAccountBalance());
            System.out.println("테스트 : 잔액 :"+ t.getWithdrawAccount().getBalance());
            System.out.println("테스트 : fullname  :"+ t.getWithdrawAccount().getUser().getFullname());
            System.out.println("=======================================");

        });

        //then
        //Assertions.assertThat(transactionListPS.get(3).getDepositAccountBalance()).isEqualTo(800L);
    }

    @Test
    public void dataJpa_test1(){
        List<Transaction> transactionList = transactionRepository.findAll();
        
        transactionList.forEach((transaction) -> {
            System.out.println("transaction.getId() = " + transaction.getId());
            System.out.println("transaction.getSender = " + transaction.getSender());
            System.out.println("transaction gubun= " + transaction.getGubun());
            System.out.println("=========================================== = ");
        });
    }

    @Test
    public void dataJpa_test2(){
        List<Transaction> transactionList = transactionRepository.findAll();

        transactionList.forEach(transaction -> {
            System.out.println("transaction.getId() = " + transaction.getId());
            System.out.println("transaction.getSender = " + transaction.getSender());
            System.out.println("transaction.getId() = " + transaction.getId());
            System.out.println("transaction gubun= " + transaction.getGubun());
            System.out.println("=========================================== = ");
        });
    }

    private void dataSetting() {
        User saar = userRepository.save(newUser("saar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스"));
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
    }

    private void autoincrementReset() { //autoincrement초기화
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

}
