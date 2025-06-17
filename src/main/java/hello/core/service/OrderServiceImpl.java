package hello.core.service;

import hello.core.domain.Member;
import hello.core.domain.Order;
import hello.core.repository.*;

public class OrderServiceImpl implements OrderService {

    //private final MemberRepository memberRepository = new MemoryMemberRepository();

    /*
    * 기존의 방식
    * */
    // 기존 1000원 할인 구현체
    // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    // 할인 정책을 변경하려면 클라이언트인 OrderServiceImpl을 수정해야 한다
    // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    /*
     * 인터페이스만 상속 받게 하는 방식
     * */
    //private DiscountPolicy discountPolicy;


    //관심사의 분리
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);
        return new Order(member.getId(), itemName, itemPrice, discountPrice);
    }
}
