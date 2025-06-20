package hello.core.beanDefinition;

import hello.core.config.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class BeanDefinitionTest {


    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    GenericXmlApplicationContext ga = new GenericXmlApplicationContext("appConfig.xml");

    @Test
    @DisplayName("빈 설정 메타정보 확인 FactoryMethod를 사용하는방법 class에서 @Bean으로")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String key : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(key);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                System.out.println("beanDefinition = " +  key + ", beanDefinition = " + beanDefinition);
            }
        }
    }
    @Test
    @DisplayName("빈 설정 메타 정보확인 직접 등록하는 방법 xml 에 <Bean>으로")
    void findApplicationBeanByXml() {
        String[] beanDefinitionNames = ga.getBeanDefinitionNames();
        for (String key : beanDefinitionNames) {
            BeanDefinition beanDefinition = ga.getBeanDefinition(key);
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                System.out.println("beanDefinition = " +  key + ", beanDefinition = " + beanDefinition);
            }
        }
    }


}
