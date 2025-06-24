package hello.core.lifeCycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
    }


    @Configuration
    static class LifeCycleConfig {
        //@Bean(initMethod = "init", destroyMethod = "close")  // default (inferred)
        @Bean
        public NetworkClient networkClient() {
            //객체 생성과 초기화 단계를 분리하는 주된 이유
            //빈 생성 후 의존성 주입이 이뤄지므로, 주입된 값을 사용하는 초기화는 그 이후에 필요해요. 생성자는 주입 전 호출되죠.
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("https://naver.com");
            // 생성자 호출 후 의존성 주입하고 나서
            // afterPropertiesSet() 이 실행되네
            return networkClient;
        }
    }


}