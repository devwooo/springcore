package hello.core;

import hello.core.config.AppConfig;
import hello.core.domain.Member;
import hello.core.domain.Order;
import hello.core.repository.DiscountPolicy;
import hello.core.repository.FixDiscountPolicy;
import hello.core.repository.MemoryMemberRepository;
import hello.core.role.Grade;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;

public class OrderApp {

    public static void main(String[] args) {

        AppConfig appConfig = new AppConfig();
        MemberService ms = appConfig.memberService();
        OrderService os = appConfig.orderService();
        //MemberService ms = new MemberServiceImpl(null);
        //OrderService os = new OrderServiceImpl(null, null);
        DiscountPolicy dp = new FixDiscountPolicy();

        Long memberId = 1L;
        Member member = new Member(memberId, "springA", Grade.VIP);
        ms.join(member);

        Order order = os.createOrder(memberId, "ItemA", 10000);
        System.out.println(order.toString());
        System.out.println("order = " + order.calculatorPrice());
    }
}
