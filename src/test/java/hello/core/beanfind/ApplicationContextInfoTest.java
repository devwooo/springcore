package hello.core.beanfind;

import hello.core.config.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String bdn : beanDefinitionNames) {
            Object bean = ac.getBean(bdn);
            System.out.println("bean name = " + bdn + ", Object = " + bean);
        }
    }

    @Test
    @DisplayName("직접 등록한 빈 출력하기")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String bdn : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(bdn);

            // BeanDefinition.ROLE_APPLICATION : 직접 등록한 애플리케이션빈
            // BeanDefinition.ROLE_INFRASTRUCTURE : 스프링이 내부에서 사용하는 빈
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(bdn);
                System.out.println("bean name = " + bdn + ", Object = " + bean);
            }

        }
    }



}
