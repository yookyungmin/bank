package shop.mtcoding.bank.handler.ex;

import lombok.Getter;

import java.util.Map;


@Getter //리턴하기위해 게터
public class CustomValidationException extends RuntimeException {

    private Map<String, String> errorMap;

    public CustomValidationException(String message, Map<String, String> errorMap) {
        super(message);
        this.errorMap = errorMap;
    }
}
