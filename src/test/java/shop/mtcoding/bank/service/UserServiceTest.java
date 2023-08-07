package shop.mtcoding.bank.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.user.UserRespDto.*;
import static shop.mtcoding.bank.dto.user.userReqDto.*;


///Spring 관련 bean들이 하나도 없는 환경
@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends DummyObject {

    @InjectMocks  //userService에 userRepository를 주입해준다
    private UserService userService;

    @Mock //가짜로 띄운다 service를 테스트 하는거기 떄문에
    private UserRepository userRepository;

    @Spy //진짜를 가짜에 집어넣는걸 passwordEncoder 진짜를 userservice에 주입
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("saar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("saar@nate.com");
        joinReqDto.setFullname("썰");

        //stub 1 //가정법, 가설  //어떤걸 실행하면 어떤게 리턴되는지
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        //when(userRepository.findbyUsername(any())).thenReturn(Optional.of(new User()));

        //stub 2
        User saar = newMockUser(1L, "saar", "쌀");
        when(userRepository.save(any())).thenReturn(saar);


        //when
        JoinRespDto joinRespDto = userService.회원가입(joinReqDto);
        System.out.println("테스트" + joinRespDto);

        //then
        assertThat(joinRespDto.getId()).isEqualTo(1L);
        assertThat(joinRespDto.getUsername()).isEqualTo("saar");
    }
}
