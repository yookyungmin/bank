package shop.mtcoding.bank.domain.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.account.Account;


import javax.persistence.*;
import java.time.LocalDateTime;


@NoArgsConstructor //스프링이 User 객체 생성 할떄 빈생성자로 new를 하기 떄문에
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction_tb") //어떤 계좌가 어떤 계좌에 이체 했는지 정보
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Account withdrawAccount;

    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Account depositAccount;

    @Column(nullable = false)
    private  Long amount;


    private Long witdrawAccountBalance; //111 걔좌 1000원 // 500 // 200 계좌의 히스토리
    private Long depositAccountBalance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum gubun; //withdraw, deposit, transfer, all


    //계좌가 사라져도 로그엔 남아야 한다
    private String sender;
    private String receiver;
    private String tel;

    @CreatedDate //insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false) //update, insert
    private LocalDateTime updatedAt;

    @Builder
    public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount, Long witdrawAccountBalance, Long depositAccountBalance, TransactionEnum gubun, String sender, String receiver, String tel, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.witdrawAccountBalance = witdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.gubun = gubun;
        this.sender = sender;
        this.receiver = receiver;
        this.tel = tel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
