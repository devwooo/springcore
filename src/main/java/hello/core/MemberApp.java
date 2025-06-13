package hello.core;

import hello.core.domain.Member;
import hello.core.role.Grade;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;

public class MemberApp {

    public static void main(String[] args) {
        MemberService ms = new MemberServiceImpl();
        Member m = new Member(1L,"memberA", Grade.VIP);
        ms.join(m);

        Member findMember = ms.findMember(1L);
        System.out.println("new member = " + m.getName());
        System.out.println("findMember = " + findMember.getName());
    }
}
