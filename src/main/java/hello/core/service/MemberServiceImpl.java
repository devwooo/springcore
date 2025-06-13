package hello.core.service;

import hello.core.domain.Member;
import hello.core.repository.MemberRepository;
import hello.core.repository.MemoryMemberRepository;

public class MemberServiceImpl implements MemberService {

    // 1. MemberServiceImpl 는 memberRepository(역할)과 MemoryMemberRepository(구현체) 모두 의존하고 있다 (DIP 위반)
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
