package hello.core.scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);

        PrototypeBean bean1 = ac.getBean(PrototypeBean.class);
        bean1.addCount();
        assertThat(bean1.getCount()).isEqualTo(1);

        PrototypeBean bean2 = ac.getBean(PrototypeBean.class);
        bean2.addCount();
        assertThat(bean2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        ClientBean clientBean2 = ac.getBean(ClientBean.class);

        int logic = clientBean1.logic();
        assertThat(logic).isEqualTo(1);
        int logic1 = clientBean2.logic();
        assertThat(logic1).isEqualTo(1);


    }

    @Scope("singleton")
    static class ClientBean {

//        private final PrototypeBean prototypeBean; // 생성 시점에 주입이 되어 있음

//        @Autowired
//        private ObjectProvider<PrototypeBean> prototypeBeanProvider;
//
//        @Autowired
//        public ClientBean(PrototypeBean prototypeBean) {
//            this.prototypeBean = prototypeBean;
//        }

//        @Autowired
//        ApplicationContext applicationContext;

        @Autowired
        private Provider<PrototypeBean> provider;

        public int logic() {
//            prototypeBean.addCount();
//            int count = prototypeBean.getCount();
//            return count;


//            PrototypeBean bean = applicationContext.getBean(PrototypeBean.class);
//            bean.addCount();
//            int count = bean.getCount();
//            return count;

//            PrototypeBean object = prototypeBeanProvider.getObject();
//            object.addCount();
//            return object.getCount();

            PrototypeBean prototypeBean = provider.get();
            prototypeBean.addCount();
            return prototypeBean.getCount();

        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init" + this);
        }

        @PreDestroy
        public void destory() {
            System.out.println("PrototypeBean.destory");
        }
    }


}
