package hello.core;

import hello.core.config.AppConfig;
import hello.core.domain.Member;
import hello.core.role.Grade;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.Annotation;

public class MemberApp {

    public static void main(String[] args) {

        //Spring 방식
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService ms = applicationContext.getBean("memberService", MemberService.class);

        // AppConfig 에 의한 주입방식(DI)
        //AppConfig appConfig = new AppConfig();
        //MemberService ms = appConfig.memberService();

        // 기존 직접 생성하선 방식
        //MemberService ms = new MemberServiceImpl();
        Member m = new Member(1L,"memberA", Grade.VIP);
        ms.join(m);

        Member findMember = ms.findMember(1L);
        System.out.println("new member = " + m.getName());
        System.out.println("findMember = " + findMember.getName());
    }
}
