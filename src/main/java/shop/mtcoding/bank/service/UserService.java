package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRespDto;
import shop.mtcoding.bank.dto.user.userReqDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;

import static shop.mtcoding.bank.dto.user.UserRespDto.*;
import static shop.mtcoding.bank.dto.user.userReqDto.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    //서비스는 DTO로 요청 받고 DTO로 응답
    @Transactional
    public JoinRespDto 회원가입(JoinReqDto joinReqDto){
        //1. 동일 유저네임 문제 검사
        Optional<User> userOp = userRepository.findByUsername(joinReqDto.getUsername());
        if(userOp.isPresent()){
            //유저네임 중복
            throw new CustomApiException("동일한 username이 존재합니다");
        }
        //2. 패스워드 인코딩+회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));
        //persistence컨텍스트에 들어갔다온거라 ps를 붙임

        //3.dto 응답
        return new JoinRespDto(userPS);
    }
}
