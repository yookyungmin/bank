package shop.mtcoding.bank.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

interface Dao{
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun,
                                          @Param("page") Integer page);
}
//파라미터 여러개값이@param 붙여야함
//TransactionRepository에 만들면 쿼리는 짤수 있는데 동적쿼리는불가해서 인터페이스로 만듬


//impl 필수, TransactionRepository가 앞에 붙어야 한다.
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements  Dao {
    private final EntityManager em; //di됨

    //jqpl이 무엇인지
    //join fetch
    //왜 outer join을 해야하는지
    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        //동적쿼리(gubun 값을 가지고 동적쿼리 = DEPOSIOT, WITHDRAW, ALL)
        //JPQL
        String sql = "";
        sql += "select t from Transaction t ";

        if(gubun.equals("WITHDRAW")){
            sql += "join fetch t.withdrawAccount wa ";  //fetch를 지우면 join 해서 데이터를 끌고왔지만 조회는 안한다.
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        }else if(gubun.equals("DEPOSIT")){
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId";
        }else { //null
            sql += "left join fetch t.withdrawAccount wa ";  //left 뺴면 null값은 조회 안한다 1345
            sql += "left join fetch t.depositAccount da "; // left 빼면 345가 나옴 두번쨰 테이블의 1번이 null이라서
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class); //jpql문법

        if(gubun.equals("WITHDRAW")){
            query = query.setParameter("withdrawAccountId", accountId);
        }else if(gubun.equals("DEPOSIT")){
            query = query.setParameter("depositAccountId", accountId);
        }else{
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page*5); // 5, 10, 15
        query.setMaxResults(5);

        return query.getResultList();
    }
}
