package shop.mtcoding.bank.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Getter
public enum UserEnum {
    ADMIN("관리자"),
    CUSTOMER("고객");

    private String value;
}
