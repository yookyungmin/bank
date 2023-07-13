package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountListRespDto 계좌목록보기_유저별(Long userId){
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다.")
        );
        // 유저의 모든 계좌 목록 //리턴 하기 위해 DTO 필요
        List<Account> accountListPS = accountRepository.findByUser_id(userId);

        return new AccountListRespDto(userPS, accountListPS);
    }



    @Transactional //커밋이 되어야 하기 떄문에 readonly True x
    public AccountSaveRespDto 계좌등록(AccountSaveReqDto accountSaveReqDto, Long userId){
        //User가 DB에 있는지 검증
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        ); //DB에서 가져온 정보라 PS를 붙임

        //해당 계좌가 DB에 있는 중복여부를 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if(accountOP.isPresent()){ //존재하면
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }
        // 계좌등록
        Account accountPS = accountRepository.save(accountSaveReqDto.ToEntity(userPS));

        //DTO 응답
        return new AccountSaveRespDto(accountPS);
    }

    @Transactional //삭제, db의 영향을 주니 Transactional
    public void 계좌삭제(Long number, Long userId){
        //계좌 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        );

        //게좌 소유자 확인
        accountPS.checkOwner(userId); //saar이 이미 조회 되었기 떄문에 LazyLoading 할떄 select 쿼리가 발동 안한다

        //계좌 삭제
        //accountRepository.deleteById(accountPS.getId());

        accountRepository.deleteById(1L);
    }


}
