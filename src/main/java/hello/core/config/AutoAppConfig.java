package hello.core.config;

import hello.core.repository.MemberRepository;
import hello.core.repository.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = {"hello.core.repository", "hello.core.config", "hello.core.service", "hello.core.controller"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {
    /*
    @Bean(name = "memoryMemberRepository")
    public MemberRepository memoryMemberRepository() {
        return new MemoryMemberRepository();
    }*/

}
