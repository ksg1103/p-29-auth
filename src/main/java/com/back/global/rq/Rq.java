package com.back.global.rq;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
//@RequestScope // 각 HTTP 요청마다 새로운 Rq 객체가 생성되고, 요청이 끝나면 해당 객체는 소멸됩니다.
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;
    private final MemberService memberService;
    private final HttpServletResponse response;

    public void addCokie(String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 쿠키가 모든 경로에서 유효하도록 설정
        cookie.setHttpOnly(true); // JavaScript에서 쿠키에 접근하지 못하도록 설정 (보안 강화)
        cookie.setDomain("localhost"); // 쿠키가 유효한 도메인 설정 (필요에 따라 변경)

        response.addCookie(cookie);
    }
    public Member getActor() {

        String authorizationHeader = request.getHeader("Authorization");
        String apiKey = null;
        //헤더방식 vs 쿠키방식
        if(authorizationHeader != null) {
//            throw new ServiceException("401-2","인증 정보가 헤더에 존재하지 않습니다.");

            if(!authorizationHeader.startsWith("Bearer ")) {
                throw new ServiceException("401-3","유효하지 않은 인증 정보입니다.");
            }

            apiKey = authorizationHeader.replace("Bearer ", ""); // "Bearer " 접두어 제거
        } else{
            apiKey = request.getCookies() == null ? ""
                    : Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("apiKey"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseThrow(()-> new ServiceException("401-4","인증 정보가 쿠키에 존재하지 않습니다."));
        }

        if(apiKey.isBlank()){
            throw new ServiceException("401-5","인증 정보가 존재하지 않습니다.");
        }

        Member actor = memberService.findByApiKey(apiKey).orElseThrow(
                () -> new ServiceException("401-1","유효하지 않은 API Key 입니다.")
        );
        return actor;
    }
}
