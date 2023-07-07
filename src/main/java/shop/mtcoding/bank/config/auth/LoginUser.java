package shop.mtcoding.bank.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.mtcoding.bank.domain.user.User;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor //userDetails , 시큐리티에서 사용자의 정보를 담는 인터페이스이다
public class LoginUser implements UserDetails {

    private final User user;

    @Override //해당 User의 권한을 리턴하는곳
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(() -> "ROLE_" + user.getRole()); //Role 넣는과정, 람다식
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override // 계정 만료
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //계정 잠금
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //비밀번호 기간
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //1년동안 회원 로그인안하면 휴먼 계정 ex
    public boolean isEnabled() {
        return true;
    }
}
