package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

public class LongTest {

    @Test
    public void long_test() throws Exception{
        Long number1 = 1111L;
        Long number2 = 1111L;


        if(number1.longValue() == number2.longValue()){ //동일한지 비교는 longValue() 필요 O
            System.out.println("테스트 = 동일합다 " );
        }else{
            System.out.println("테스트  = 동일하지 않습니다. ");
        }

        Long amount1 = 100L;
        Long amount2 = 1000L;

        if(amount1 < amount2){  //대소 비교는 longValue() 필요 x
            System.out.println("테스트 : amount1이 작습니다");
        }else{
            System.out.println("테스트 : amount2가 큽니다 ");
        }
    }
}
