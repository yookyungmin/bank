package shop.mtcoding.bank.domain.account;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor //스프링이 User 객체 생성 할떄 빈생성자로 new를 하기 떄문에
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 4)
    private Long number; //계좌번호

    @Column(nullable = false, length = 4)
    private Long password; //계좌비번

    @Column(nullable = false)
    private Long balance; //잔액 기본값1000

    //항상 orm에서 fk의 주인은 Many Entity 쪽
    @ManyToOne(fetch =  FetchType.LAZY) //Account.getUser().아무필드호출() == Lazy발동
    private User user;  //한명의 유저는 많은 계좌를 가질 수 있다.

    @CreatedDate //insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false) //update, insert
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //Lazy 로딩이어도 id를 조회할때는 select 쿼리가 날아가지 않는다.
    //Lazy 로딩 할땐 persist context에 있으면 쿼리를 날리지 않는다.
    public void checkOwner(Long userId){
        
        String testUserName = user.getUsername();
        System.out.println("테스트 = " + testUserName);
        
        if(user.getId() != userId){
            throw new CustomApiException("계좌 소유자가 아닙니다.");
        }
    }

    public void deposit(Long amount){
        balance = balance + amount;
    }
}

