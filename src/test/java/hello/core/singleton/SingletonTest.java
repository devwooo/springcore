package hello.core.singleton;

import hello.core.config.AppConfig;
import hello.core.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;

public class SingletonTest {
    
    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();
    
        
        //1. 조회 : 호출할 때 마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();
        //2. 조회 : 호출할 때 마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();

        // 참조값이 다른것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        // memberService1 != memberService2
        assertThat(memberService1).isNotSameAs(memberService2);

        // AppConfig는 요청을 할 때 마다 객체를 새로 생성한다. > 메모리 낭비가 심하다.
    }

    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 적용")
    void singletonServiceTest() {
        // java: SingletonService() has private access in hello.core.singleton.SingletonService
        // SingletonService s = new SingletonService();

        SingletonService s1 = SingletonService.getInstance();
        SingletonService s2 = SingletonService.getInstance();

        System.out.println("s1 = " + s1);
        System.out.println("s2 = " + s2);

        // same == 참조를 비교
        assertThat(s1).isSameAs(s2);
    }

    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService memberService1 = ac.getBean("memberService", MemberService.class);
        MemberService memberService2 = ac.getBean("memberService", MemberService.class);

        // 참조값이 같은것을 확인
        System.out.println("memberService1 = " + memberService1); // MemberServiceImpl@553f1d75
        System.out.println("memberService2 = " + memberService2); // MemberServiceImpl@553f1d75

        // memberService1 != memberService2
        assertThat(memberService1).isSameAs(memberService2);
    }


}
