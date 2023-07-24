package shop.mtcoding.bank.temp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongTest {

    @Test
    public void long_test3() throws Exception{
        long v1 = 1208L;
        long v2 = 1208L;

        Assertions.assertThat(v1).isEqualTo(v2);
    }

    @Test
    public void long_test2() throws Exception{
        // -127 ~ 127 범위까진 .longValue() 없이 비교가능
        //given
        Long v1 = 200L;
        Long v2 = 200L;
        //when
        if(v1.longValue() ==  v2.longValue()){
            System.out.println("테스트 : 같습니다.");
        }

        //then
    }

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
