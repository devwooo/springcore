package hello.core.config;

import hello.core.domain.Member;
import hello.core.domain.Order;
import hello.core.repository.DiscountPolicy;
import hello.core.repository.FixDiscountPolicy;
import hello.core.repository.MemoryMemberRepository;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class AppConfig {
    
    // AppConfig 리팩토링
    // 
    public MemberService memberService() {
        // 생성자 주입
        return new MemberServiceImpl(memberRepository());
    }

    public OrderService orderService() {
        // 생성자 주입
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    private MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

    // new MemoryMemberRepository() 중복을 memberRepository()로 변경
    // 구현체들을 함수로 빼고, 해당 메서드를 주입해주므로써 역할이 분명해졌다
}
