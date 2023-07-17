package shop.mtcoding.bank.config.jwt;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JwtProcessTest {


    private String createToken(){
        //givem
        User user = User.builder().id(1L).role(UserEnum.ADMIN).build(); //유저 객체 만들기
        //create 할떄 LoginUser가 필요하기 떄문에, LoginUser는 user필요

        LoginUser loginUser = new LoginUser(user);

        //when
        String jwtToken = JwtProcess.create(loginUser);

        return jwtToken;
    }
    @Test
    public void create_test() throws Exception{

        //givem
//        User user = User.builder().id(1L).role(UserEnum.ADMIN).build(); //유저 객체 만들기
//        //create 할떄 LoginUser가 필요하기 떄문에, LoginUser는 user필요
//
//        LoginUser loginUser = new LoginUser(user);

        //when
        String jwtToken = createToken();
        System.out.println("jwtToken = " + jwtToken +" 어디까지야");

        //then
        assertTrue(jwtToken.startsWith(jwtVo.TOKEN_PREFIX));
    }

    @Test
    public void verify_test() throws  Exception{
        //given
        String token = createToken(); //Bearer 제거해서 처리하기

        String jwtToken = token.replace(jwtVo.TOKEN_PREFIX, "");

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("테스트 : " + loginUser.getUser().getId());  //1이면 검증 잘됨
        System.out.println("테스트 : " + loginUser.getUser().getRole().name());
        //then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
    }
}
