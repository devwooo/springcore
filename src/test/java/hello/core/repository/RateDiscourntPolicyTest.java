package hello.core.repository;

import hello.core.domain.Member;
import hello.core.role.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RateDiscourntPolicyTest {

    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Test
    @DisplayName("VIP는 10% 할인이 적용 되어야 한다.")
    void vip_0() {
        // given
        Member member = new Member(1L, "memberA", Grade.VIP);
        // when
        int discount = discountPolicy.discount(member, 10000);
        // then
        assertThat(discount).isEqualTo(1000);
    }

    @Test
    @DisplayName("VIP가 아닌경우 할인이 되면 안된다.")
    void vip_non() {
        Member member = new Member(1L, "memberA", Grade.BASIC);
        // when
        int discount = discountPolicy.discount(member, 10000);
        // then
        assertThat(discount).isEqualTo(0);

    }

}