package hello.core.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    @DisplayName("")
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        // ThreadA : A사용자가 10000원 주문
        int userA = statefulService1.order("userA", 10000);
        // ThreadB : B사용자가 20000원 주문
        int userB = statefulService1.order("userB", 20000);

        // ThreadA: A사용자 주문 금액 조회
        //int price = statefulService1.getPrice();
        //System.out.println("price = " + price); // 10000원을 예상하지만 20000원이 나왔다,
        System.out.println("price = " + userA);
        System.out.println("price = " + userB);

        // 즉 price 속성이 A사용자와 B사용자간 공유되어 A사용자가 주문한 금액이 B사용자에 의해 바꿔치기 되었다.
        //assertThat(statefulService1.getPrice()).isEqualTo(statefulService2.getPrice());

        // StatefulService의 order() 함수가 price 속성을 변경하고 있다.


    }



    static class TestConfig {
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

}