package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {

    protected  User newUser(String username, String fullname){ //엔티티 save용
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword )
                .email(username+"saar@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();

        //id는 객체만들어서 save 하면 자동으로 id, localdatetime만들어지기떄문에 삭제
    }

    protected  User newMockUser(Long id, String username, String fullname){//가짜로 만들어낼떄
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword )
                .email(username+"saar@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
