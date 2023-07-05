package shop.mtcoding.bank.temp;


import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

//java.util.regx.Pattern
public class RegexTest {

    @Test
    public void 한글만_된다_test() throws  Exception{
        String value = "한글";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void 한글_안된다_test() throws  Exception{
        String value = "ABC";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void 영어만_된다_test() throws  Exception{
        String value = "saar";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void 영어_안된다_test() throws  Exception{
        String value = "호";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void 영어_숫자_만된다_test() throws  Exception{
        String value = "ASDSADㄷㄴ";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void 영어만되고_길이는_최소2_최대4_test() throws  Exception{
        String value = "saSDSar";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void user_username_test() throws  Exception{
        String username = "saar";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void user_fullname_test() throws  Exception{
        String fullname = "saar";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        System.out.println("테스트 = " + result);
    }

    @Test
    public void user_email_test() throws  Exception{
        String email = "saAar@nate.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", email);
        System.out.println("테스트 = " + result);
    }

    //username, email, fullname
}
