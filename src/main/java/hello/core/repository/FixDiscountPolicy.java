package hello.core.repository;

import hello.core.domain.Member;
import hello.core.role.Grade;

public class FixDiscountPolicy implements DiscountPolicy {

    private int discountFixAmount = 1000; // 1000원 할인
    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP ) {
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
