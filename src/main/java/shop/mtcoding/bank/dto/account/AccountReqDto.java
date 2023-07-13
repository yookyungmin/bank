package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class AccountReqDto {
    @Getter
    @Setter
    public static class AccountSaveReqDto{

        @NotNull
        @Digits(integer = 4, fraction = 4)  //최소4~최대4
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;
        //유저는 세션에 있는걸로 검증 할거니 필요x

        public Account ToEntity(User user){
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }
}