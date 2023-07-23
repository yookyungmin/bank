package shop.mtcoding.bank.handler.Handler;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.handler.ex.CustomValidationException;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class CustomValidationAdvice {

        @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
        public void postMapping(){}

        @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
        public void putMapping(){}


    //postMapping, putMapping 두군데다 하겠따
        @Around("postMapping() || putMapping()") //joinpoint 의 전후 제어
        public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            Object[] args = proceedingJoinPoint.getArgs(); //joinpoint의 매개변수들, joinpoint는 메서드들

            for(Object arg : args){
                if(arg instanceof BindingResult){ //BindingResult를 상속받거나 인스턴스면
                    BindingResult bindingResult = (BindingResult) arg; //형변환

                    if(bindingResult.hasErrors()){ //bindingresult에 에러가 있으면
                        Map<String, String> errorMap = new HashMap<>();

                        for(FieldError error : bindingResult.getFieldErrors()){
                            errorMap.put(error.getField(), error.getDefaultMessage()); //에러들이 담긴다.
                        }
                      throw new CustomValidationException("유효성 검사 실패", errorMap);
                    }
                }
            }
            return proceedingJoinPoint.proceed();
        }
        /*postmapping, putmapping이 붙어있는 모든 컨트롤러가 동작 할때 BindingResult 매개변수가 있으면 if가 동작
          동작해도 에러가 있을때만 throw new를 날리고 에러가 없으면 proceed() 실행 정상적으로 실행하라는 메서드
        * */
}

// 유효성 검사라는건 바디 데이터가 있는곳에서만 하면 된다
/*
* get(바디x), delete(바디x), post(바디o), put(바디o)
 */