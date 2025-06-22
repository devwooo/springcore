package hello.core.config;

import hello.core.domain.Member;
import hello.core.domain.Order;
import hello.core.repository.DiscountPolicy;
import hello.core.repository.FixDiscountPolicy;
import hello.core.repository.MemoryMemberRepository;
import hello.core.repository.RateDiscountPolicy;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {
    // AppConfig 리팩토링
    //


    // 예상결과
    //call AppConfig.memberService
    //call AppConfig.memberRepository
    //call AppConfig.memberRepository
    //call AppConfig.orderService
    //call AppConfig.memberRepository

    // 실제 결과
    //call AppConfig.MemberService
    //call AppConfig.memberRepository
    //call AppConfig.orderService

    @Bean
    public MemberService memberService() {
        // 생성자 주입
        System.out.println("call AppConfig.MemberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        // 생성자 주입
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
        //return null;
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
        //return new RateDiscountPolicy();
    }

    // new MemoryMemberRepository() 중복을 memberRepository()로 변경
    // 구현체들을 함수로 빼고, 해당 메서드를 주입해주므로써 역할이 분명해졌다
}
