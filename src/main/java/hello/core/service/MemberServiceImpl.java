package hello.core.service;

import hello.core.domain.Member;
import hello.core.repository.MemberRepository;
import hello.core.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService {

    // 1. MemberServiceImpl 는 memberRepository(역할)과 MemoryMemberRepository(구현체) 모두 의존하고 있다 (DIP 위반)
    //private final MemberRepository memberRepository = new MemoryMemberRepository();

    // 관심사의 구분
    @Autowired  // 마치 ac.getBean(MemberRepository.class) 한것처럼
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    //test 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
