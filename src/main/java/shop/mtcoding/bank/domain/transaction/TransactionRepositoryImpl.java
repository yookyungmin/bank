package shop.mtcoding.bank.domain.transaction;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.bank.domain.account.QAccount;

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

    private final JPAQueryFactory jpaQueryFactory;

    //jqpl이 무엇인지
    //join fetch
    //왜 outer join을 해야하는지
//    @Override
//    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
//        //동적쿼리(gubun 값을 가지고 동적쿼리 = DEPOSIOT, WITHDRAW, ALL)
//        //JPQL
//        String sql = "";
//        sql += "select t from Transaction t ";
//
//        if(gubun.equals("WITHDRAW")){
//            sql += "join fetch t.withdrawAccount wa ";  //fetch를 지우면 join 해서 데이터를 끌고왔지만 조회는 안한다.
//            sql += "where t.withdrawAccount.id = :withdrawAccountId";
//        }else if(gubun.equals("DEPOSIT")){
//            sql += "join fetch t.depositAccount da ";
//            sql += "where t.depositAccount.id = :depositAccountId";
//        }else { //gubun = all
//            sql += "left join t.withdrawAccount wa ";  //left 뺴면 null값은 조회 안한다 1345
//            sql += "left join t.depositAccount da "; // left 빼면 345가 나옴 두번쨰 테이블의 1번이 null이라서
//            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
//            sql += "or ";
//            sql += "t.depositAccount.id = :depositAccountId";
//        }
//
//        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class); //jpql문법
//
//        if(gubun.equals("WITHDRAW")){
//            query = query.setParameter("withdrawAccountId", accountId);
//        }else if(gubun.equals("DEPOSIT")){
//            query = query.setParameter("depositAccountId", accountId);
//        }else{
//            query = query.setParameter("withdrawAccountId", accountId);
//            query = query.setParameter("depositAccountId", accountId);
//        }
//
//        query.setFirstResult(page*5); // 5, 10, 15
//        query.setMaxResults(5);
//
//        return query.getResultList();
//    }



    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        QTransaction t = QTransaction.transaction;
//        QAccount withdrawAccount = QAccount.account;
//        QAccount depositAccount = QAccount.account;

        JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(t);


        if ("WITHDRAW".equals(gubun)) {
            query = query
                    .innerJoin(t.withdrawAccount, QAccount.account)
                    .fetchJoin()
                    .where(t.withdrawAccount.id.eq(accountId));
        } else if ("DEPOSIT".equals(gubun)) {
            query = query
                    .innerJoin(t.depositAccount, QAccount.account)
                    .fetchJoin()
                    .where(t.depositAccount.id.eq(accountId));
        } else { // gubun = all
            query = query
                    .leftJoin(t.withdrawAccount, QAccount.account)
                    .leftJoin(t.depositAccount, QAccount.account)
                    .where(t.withdrawAccount.id.eq(accountId)
                            .or(t.depositAccount.id.eq(accountId)));
        }

        return query
                .offset(page * 5)
                .limit(5)
                .fetch();
    }


//    @Override
//    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
//        QTransaction t = QTransaction.transaction;
//        JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(t);
//
//        if ("WITHDRAW".equals(gubun)) {
//            query.innerJoin(t.withdrawAccount, QTransaction.transaction.withdrawAccount)
//                    .where(t.withdrawAccount.id.eq(accountId));
//        } else if ("DEPOSIT".equals(gubun)) {
//            query.innerJoin(t.depositAccount, QDepositAccount.depositAccount)
//                    .where(t.depositAccount.id.eq(accountId));
//        } else { // gubun = all
//            query.leftJoin(t.withdrawAccount, QWithdrawAccount.withdrawAccount)
//                    .leftJoin(t.depositAccount, QDepositAccount.depositAccount)
//                    .where(t.withdrawAccount.id.eq(accountId)
//                            .or(t.depositAccount.id.eq(accountId)));
//        }
//
//        return query.offset(page * 5)
//                .limit(5)
//                .fetch();
//    }

//    @Override
//    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
//        QTransaction t = QTransaction.transaction;
//        QAccount withdrawAccount = QAccount.account;
//        QAccount depositAccount = QAccount.account;
//
//        JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(t);
//
//        if ("WITHDRAW".equals(gubun)) {
//            query.innerJoin(t.withdrawAccount, withdrawAccount)
//                    .fetchJoin()
//                    .where(t.withdrawAccount.id.eq(accountId));
//        } else if ("DEPOSIT".equals(gubun)) {
//            query.innerJoin(t.depositAccount, depositAccount)
//                    .fetchJoin()
//                    .where(t.depositAccount.id.eq(accountId));
//        } else { // gubun = all
//            query.leftJoin(t.withdrawAccount, withdrawAccount)
//                    .fetchJoin()
//                    .leftJoin(t.depositAccount, depositAccount)
//                    .fetchJoin()
//                    .where(t.withdrawAccount.id.eq(accountId)
//                            .or(t.depositAccount.id.eq(accountId)));
//        }
//
//        return query.offset(page * 5)
//                .limit(5)
//                .fetch();
//    }


}
