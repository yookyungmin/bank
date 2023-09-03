package shop.mtcoding.bank.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseDto<T> { //응답의 dto는 수정할일이 없어서 final
    private final Integer code;//1. 성공 -1 실패
    private final String msg;
    private final T data;
}
