package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import shop.mtcoding.bank.handler.ex.CustomForbiddenException;
import shop.mtcoding.bank.service.AccountService;

import javax.validation.Valid;

import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;
import static shop.mtcoding.bank.service.AccountService.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/s/account") //@AuthenticationPrincipal 사용할수 있는 이유는 강제로 만들어져 담아져 있기 떄문이다
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto, BindingResult bindingResult,
                                         @AuthenticationPrincipal LoginUser loginUser){

        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 등록 성공", accountSaveRespDto), HttpStatus.CREATED);
    }

    //인증이 필요하고, account 테이블에 1번 row 를 주세요!
    // cos로 로그인을 햇는데, cos의 id가 2번이에요! <- 컨트롤러에 권한처리가 필요하게됨

    //인증이 필요하고, account 테이블에 login 한 유저의 계좌만 주세요
   @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser){

//        if(id != loginUser.getUser().getId()){
//            throw new CustomForbiddenException("권한이 없습니다.");
//        } pathVariable사용를 하게 되면 필요한 로직

        AccountListRespDto accountListRespDto = accountService.계좌목록보기_유저별(loginUser.getUser().getId());

        return  new ResponseEntity<>(new ResponseDto<>(1, "계좌목록보기_유저별 성공", accountListRespDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser){

        accountService.계좌삭제(number, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit") //인증이 필요없기에 /s제외
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto, BindingResult bindingResult){

        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto, BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){

        AccountWithdrawRespDto accountWithdrawRespDto = accountService.계좌출금(accountWithdrawReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountWithdrawRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountTransferReqDto accountTransferReqDto, BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){

        AccountTransferRespDto accountTransferRespDto = accountService.계좌이제(accountTransferReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountTransferRespDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> findDetatilAccount(@PathVariable Long number,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @AuthenticationPrincipal LoginUser loginUser){

        AccountDetailRespDto accountDetailRespDto = accountService.계좌상세보기(number, loginUser.getUser().getId(), page);

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌상세보기 성공", accountDetailRespDto), HttpStatus.OK);
    }
}
