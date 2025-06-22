package hello.core.service;

import hello.core.domain.Member;
import hello.core.domain.Order;
import hello.core.repository.FixDiscountPolicy;
import hello.core.repository.MemoryMemberRepository;
import hello.core.role.Grade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class OrderServiceImplTest {

    @Test
    void createOrder() {
        MemoryMemberRepository memoryMemberRepository = new MemoryMemberRepository();
        memoryMemberRepository.save(new Member(1L, ",memberA", Grade.VIP));
        OrderServiceImpl orderService = new OrderServiceImpl(memoryMemberRepository, new FixDiscountPolicy());
        Order memberA = orderService.createOrder(1L, "memberA", 10000);
        assertThat(memberA.getDiscountPrice()).isEqualTo(1000);

    }
}

