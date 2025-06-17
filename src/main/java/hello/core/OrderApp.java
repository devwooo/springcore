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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {

    public static void main(String[] args) {

        //Spring 방식
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService ms = ac.getBean("memberService", MemberService.class);
        OrderService os = ac.getBean("orderService", OrderService.class);


        //AppConfig방식
//        AppConfig appConfig = new AppConfig();
//        MemberService ms = appConfig.memberService();
//        OrderService os = appConfig.orderService();

        //MemberService ms = new MemberServiceImpl(null);
        //OrderService os = new OrderServiceImpl(null, null);
        DiscountPolicy dp = new FixDiscountPolicy();

        Long memberId = 1L;
        Member member = new Member(memberId, "springA", Grade.VIP);
        ms.join(member);

        Order order = os.createOrder(memberId, "ItemA", 20000);
        System.out.println(order.toString());
        System.out.println("order = " + order.calculatorPrice());
    }
}
