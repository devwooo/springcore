package hello.core.sacn;

import hello.core.config.AutoAppConfig;
import hello.core.repository.MemberRepository;
import hello.core.service.MemberService;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;

public class AutoAppConfigTest {

    @Test
    void basicScan() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);

        MemberService bean = ac.getBean(MemberService.class);
        assertThat(bean).isInstanceOf(MemberService.class);

        OrderServiceImpl bean1 = ac.getBean(OrderServiceImpl.class);
        MemberRepository memberRepository = bean1.getMemberRepository();
        System.out.println("memberRepository = " + memberRepository);
    }
}
