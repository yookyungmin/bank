package shop.mtcoding.bank.domain.transaction;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.bank.domain.account.QAccount;

import javax.persistence.EntityManager;
import java.util.List;

import static shop.mtcoding.bank.domain.transaction.QTransaction.transaction;

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

//    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page){
//
//            JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(transaction);
//
//        return  query
//
//                     .where(gubunCheck(gubun, accountId))
//                     .limit(3).offset(page*3)
//                     .fetch();
//
////        //join
////        query.leftJoin(transaction.withdrawAccount).leftJoin(transaction.depositAccount);
////
////        //where
////        query.where(gubunCheck(gubun, accountId));
////
////        //paging
////        query.limit(3).offset(page * 3);
//
////        return query.fetch();
//    }
//
//    private BooleanExpression gubunCheck(String gubun, Long accountId){
//        if (!StringUtils.hasText(gubun)){
//            return
//                    transaction.withdrawAccount.id.eq(accountId).or(transaction.depositAccount.id.eq(accountId));
//
//        }else if(TransactionEnum.valueOf(gubun) == TransactionEnum.DEPOSIT){
//            return
//                    transaction.depositAccount.id.eq((accountId));
//        }else if(TransactionEnum.valueOf(gubun) == TransactionEnum.WITHDRAW){
//            return
//                    transaction.withdrawAccount.id.eq(accountId);
//        }else {
//            return null;
//        }
//    }

//        @Override
//    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
//
//        JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(transaction);
//
//        if ("WITHDRAW".equals(gubun)) {
//                query
//                    .innerJoin(transaction.withdrawAccount, QAccount.account)
//                    .fetchJoin()
//                    .where(transaction.withdrawAccount.id.eq(accountId));
//        } else if ("DEPOSIT".equals(gubun)) {
//                 query
//                     .innerJoin(transaction.depositAccount, QAccount.account)
//                     .fetchJoin()
//                     .where(transaction.depositAccount.id.eq(accountId));
//        } else { // gubun = alls
//                query
//                    .leftJoin(transaction.withdrawAccount, QAccount.account)
//                    .leftJoin(transaction.depositAccount, QAccount.account)
//                    .where(transaction.withdrawAccount.id.eq(accountId)
//                            .or(transaction.depositAccount.id.eq(accountId)));
//        }
//
//        return query
//                .offset(page * 5)
//                .limit(5)
//                .fetch();
//    }
    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {

        JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(transaction);

        if ("WITHDRAW".equals(gubun)) {
            query
                    .innerJoin(transaction.withdrawAccount, QAccount.account)
                    .fetchJoin()
                    .where(withDrawEq(accountId));
        } else if ("DEPOSIT".equals(gubun)) {
            query
                    .innerJoin(transaction.depositAccount, QAccount.account)
                    .fetchJoin()
                    .where(depositEq(accountId));
        } else { // gubun = all
            query
                    .leftJoin(transaction.withdrawAccount, QAccount.account)
                    .leftJoin(transaction.depositAccount, QAccount.account)
                    .where(allEq(accountId));
        }

        return query
                .offset(page * 5)
                .limit(5)
                .fetch();
    }
    private BooleanExpression withDrawEq(Long accountId) {
        return accountId != null ? transaction.withdrawAccount.id.eq(accountId): null;
    }

    private BooleanExpression depositEq(Long accountId) {
        return accountId != null ? transaction.depositAccount.id.eq(accountId): null;
    }

    private BooleanExpression allEq(Long accountId) {
        return accountId != null ? transaction.withdrawAccount.id.eq(accountId).or(transaction.depositAccount.id.eq(accountId)): null;
    }

}
