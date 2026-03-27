package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member join(String username, String password, String nickname){
        Member member = new Member(username,password,nickname);
        return memberRepository.save(member);
    }

    public long count(){
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username){
//        return memberRepository.findAll().stream()
//                .filter(m-> m.getUsername().equals(username))
//                .findFirst(); //이방법도 있긴 한데, 대용량의 정보가 넘어오면 과부하 걸림
//                              이경우는 어플리케이션에서 가공하는법
        return memberRepository.findByUsername(username);
        // 이경우는 데이터베이스에서 가공해서 가져오는 법
    }

    public Optional<Member> findByApiKey(String apiKey){

        return memberRepository.findByApiKey(apiKey);
    }
}
