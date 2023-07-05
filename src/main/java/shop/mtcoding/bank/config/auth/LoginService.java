package shop.mtcoding.bank.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    //시큐리티로 로그인이 될때 시큐리티가 loadUserByUsername()을 실행해서 username체크
    //체크해서 없으면 오류, 있으면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인 된 세션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증실패")
                //시큐리티를 타고 있을때 username을 못찾으면 new Internal~ 얘로 제어를해줘야 한다. 시큐리티를 타고 있을떈 제어권이 없기 떄문
                //나중에 테스트할때 설명 예정
        );

        return new LoginUser(userPS); //username을 찾으면 userPS를 담아서 세션에 만들어진다
    }
}
