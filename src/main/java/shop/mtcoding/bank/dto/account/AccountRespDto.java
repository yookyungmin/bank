package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {
    @Getter
    @Setter
    public static class AccountSaveRespDto{
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
        //dto로 만들어서 반환할거기 떄문에 생성자 필요
    }

    @Getter
    @Setter
    public static class AccountListRespDto{
        private String fullname;

        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            //this.accounts = accounts.stream().map((account)-> new AccountDto(account)).collect(Collectors.toList());
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class AccountDto{
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) { //엔티티 객체를 dto로 옮기는 과정, 원하지 않는 Lazy로딩 방지
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }

    }


    @Getter
    @Setter
    public static class AccountDepositRespDto{
        private Long id; //계좌ID
        private Long number; //계좌번호
        private TransactionDto trasaction;  //dto 안에 엔티티 들어올수 없다, 순환참조 될수 있따

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.trasaction = new TransactionDto(transaction); //엔티티를 Dto로 변환
        }

        @Getter
        @Setter
        public class TransactionDto{
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createdAt;
            @JsonIgnore //내 계좌가 아니기에 가려준다 json 응답시 테스트시에는 제거 하고 확인
            private Long depositAccountBalance;


            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }


    //Dto가 똑같아도 재사용 x (나중에 만약에 출금할떄 무언가 조금 dto가 달라져야 한다면 독립적으로 만들어야 됨
    @Getter
    @Setter
    public static class AccountWithdrawRespDto{
        private Long id; //계좌ID
        private Long number; //계좌번호
        private Long balance; //남은 잔액 확인위함
        private TransactionDto trasaction;  //dto 안에 엔티티 들어올수 없다, 순환참조 될수 있따

        public AccountWithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.trasaction = new TransactionDto(transaction); //엔티티를 Dto로 변환
        }

        @Getter
        @Setter
        public class TransactionDto{
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

//            @JsonIgnore //내 계좌가 아니기에 가려준다 json 응답시 테스트시에는 제거 하고 확인
//            private Long depositAccountBalance; //


            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
//              this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }
}
