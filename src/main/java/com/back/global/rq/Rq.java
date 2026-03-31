package com.back.global.rq;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
//@RequestScope // 각 HTTP 요청마다 새로운 Rq 객체가 생성되고, 요청이 끝나면 해당 객체는 소멸됩니다.
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;
    private final MemberService memberService;
    private final HttpServletResponse response;

    public void addCokie(String name, String value){
        response.addCookie(
                new Cookie(name, value)
        );
    }
    public Member getActor() {

        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null) {
            throw new ServiceException("401-2","인증 정보가 헤더에 존재하지 않습니다.");
        }
        if(!authorizationHeader.startsWith("Bearer ")) {
            throw new ServiceException("401-3","유효하지 않은 인증 정보입니다.");
        }
        String apiKey = authorizationHeader.replace("Bearer ", ""); // "Bearer " 접두어 제거

        Member actor = memberService.findByApiKey(apiKey).orElseThrow(
                () -> new ServiceException("401-1","유효하지 않은 API Key 입니다.")
        );
        return actor;
    }
}
