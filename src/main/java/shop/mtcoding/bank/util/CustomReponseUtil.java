package shop.mtcoding.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import shop.mtcoding.bank.dto.ResponseDto;

import javax.servlet.http.HttpServletResponse;

public class CustomReponseUtil {
    private static final Logger log = LoggerFactory.getLogger(CustomReponseUtil.class);

    public static void success(HttpServletResponse response, Object dto){
        try {
            ObjectMapper om = new ObjectMapper(); //json으로만들기위해
            ResponseDto<?> responseDto = new ResponseDto<>(1, "로그인 성공",dto);
            String responseBody = om.writeValueAsString(responseDto); //json으로 변환
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200); //로그인이 안된 사용자가 접속시
            response.getWriter().println(responseBody);  //이쁘게 메시지 포장 공통적인 dto
        }catch(Exception e) {
            log.error("서버 파싱 에러");

        }
    }

    public static void fail(HttpServletResponse response, String message, HttpStatus httpStatus){
        try {
            ObjectMapper om = new ObjectMapper(); //json으로만들기위해
            ResponseDto<?> responseDto = new ResponseDto<>(-1, message,null);
            String responseBody = om.writeValueAsString(responseDto); //json으로 변환
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody);  //이쁘게 메시지 포장 공통적인 dto
        }catch(Exception e) {
            log.error("서버 파싱 에러");

        } //unAuthentiaction 코드 리팩토링
    }

//    public static void unAuthentiaction(HttpServletResponse response, String message){
//        try {
//            ObjectMapper om = new ObjectMapper(); //json으로만들기위해
//            ResponseDto<?> responseDto = new ResponseDto<>(-1, message,null);
//            String responseBody = om.writeValueAsString(responseDto); //json으로 변환
//            response.setContentType("application/json; charset=utf-8");
//            response.setStatus(401); //로그인이 안된 사용자가 접속시
//            response.getWriter().println(responseBody);  //이쁘게 메시지 포장 공통적인 dto
//        }catch(Exception e) {
//            log.error("서버 파싱 에러");
//
//        }
//    }


}
