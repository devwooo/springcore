package hello.core.service;

import hello.core.domain.Member;
import hello.core.domain.Order;
import hello.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor // final 이 붙은 필드가 붙은 생성자를 만들어준다.
public class OrderServiceImpl implements OrderService {

    // private final MemberRepository memberRepository = new MemoryMemberRepository();
    // private final MemberRepository memberRepository;
    // private final DiscountPolicy discountPolicy;
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

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

    // 필드 주입방법
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private DiscountPolicy discountPolicy;

    //setter 주입방법
    /*@Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        System.out.println("memberRepository = " + memberRepository);
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        System.out.println("discountPolicy = " + discountPolicy);
        this.discountPolicy = discountPolicy;
    }*/

    // 생성자 주입방법
    //@Autowired 생성자가 1개일때는 생략가능(자동으로 붙여줌)
    public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
        System.out.println("OrderService.OrderServiceImpl");
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);
        return new Order(member.getId(), itemName, itemPrice, discountPrice);
    }

    //test용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }


}
