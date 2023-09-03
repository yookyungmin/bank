package shop.mtcoding.bank.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.mtcoding.bank.domain.user.User;

import java.util.ArrayList;
import java.util.Collection;


/*
             //시큐리티가 /login 주소 요청이 오면 낚아채서 로그인 진행
             //로그인 진행이 완료가 되면 시큐리티 session을 만들어줍니다(Seucrity ContextHolder)
             //오브젝트 => Authentication 타입 객체
             //Authentication 안에 User 정보가 있어야 됨.
             //User오브젝트 타입=> UserDetails 타입 객체

             //Security Session 영역에  Authentication 객체를 저장할수 있는데,
              Authentication 에 저장할 유저정보는 UserDetails 타입
              UserDetails 타입을 꺼내면 User오브젝트에 접근 가능
              Security Session => Authhentication => UserDetails(LoginUser)

              Authhentication 객체를 만들어서 Session에 넣기 위해선 UserDetailsService 필요
               로그인을 하게 되면 LoginService(UserDetailsService) 거쳐서 LoginUser(UserDetails)를 반환 해준다
                그후 security session에 Authhentication을 넣어줌 =>로그인완료
 */
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
        //ex)1년 동안 회원이 로그인을 안하면 휴면계정 전환
        //현재시간 - 로긴 시간 ==> 1년을 초과하면 return false
        // user.getLoginDate();
        return true;
    }
}
