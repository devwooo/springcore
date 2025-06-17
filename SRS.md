### 비즈니스 요구사항과 설계
 - 회원
   - 회원을 가입하고 조회할 수 있다.
   - 회원은 일반과 VIP 두 가지 등급이 있다.
   - 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)
 - 주문과 할인 정책
   - 회원은 상품을 주문할 수 있다.
   - 회원 등급에 따라 할인 정책을 적용할 수 있다.
   - 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
   - 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루
   - 고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)
   
 > 요구사항을 보면 회원 데이터, 할인 정책 같은 부분은 지금 결정하기 어려운 부분이다. 그렇다고 이런 정책이 결정될 때
   까지 개발을 무기한 기다릴 수 도 없다. 우리는 앞에서 배운 객체 지향 설계 방법이 있지 않은가!
 


## 1차 개발 시작
- 도메인 : Member
- 회원서비스 : MemberService(인터페이스), MemberServiceImpl(구현)
- 저장소 : MemberRepository(인터페이스), 구현 version 3(메모리, 내부, 외부)

### 1차 커밋시점
- MemberService를 구현하였고 해당 Service의 테스트 케이스를 작성했다
- 이시점에서 OCP, DIP이 잘 지켜졌는지..
  - MemberServiceImpl에서 MemberRepository와 그 구현체 MemoryMemberRepository 모두 의존하고 있으므로 DIP를 위반하고 있다.
   
## 2차 개발 시작
- 주문과 할인 도메인 설계
  1. 주문생성 : 클라이언트는 주문 서비스에 주문 생성을 요청
     1. 회원ID
     2. 상품명
     3. 상품 가격
  2. 회원조회 : 할인을 적용하기 위해 회원의 등급을 조회한다.
  3. 할인 적용 : 주문 서비스는 회원등급에 따른 할인여부를 할인 정책에 위임한다.
  4. 주문결과 반환 : 주문 서비스는 할인 결과를 포함한 주문 결과를 반환한다.

## 3차 개발 시작
- 새로운 할인 정책 추가 되면 DIP, OCP 원칙이 위배되는 현상
- 이를 극복하기 위한 처리들

### 새로운 할인정책 
 - VIP 회원의 경우 주문금액당 10% 할인되는 정률 할인으로 변경하고 싶어함
 - RateDiscountPrice 클래스를 생성(구현)하여 기존의 DiscountPolicy(역할)를 상속한다
 
> 할인 정책을 변경하려면 클라이언트인 OrderServiceImpl를 수정해야 한다.
>> DIP : 주문 서비스 클라이언트(OrderServiceImpl)은 DiscountPolicy 인터페이스에 의존하면서 DIP를 지킨것 같지만
>>>  추상(인터페이스) 뿐만 아니라 구체(구햔) 클래스에도 의존하고 있다.
>>>> 추상(인터페이스)의존 : DiscountPolicy
>>>> 구체(구현)클래스 의존 : FixDiscountPolicy, RateDiscountPolicy

>> OCP : 변경하지 않고 확장할 수 있다고 했는데
>>> OrderServiceImpl에서 RateDiscountPolicy로 수정 함으로써 클라이언트 코드에 영향을 준다

* 해결방안 : 인터페이스에만 의존하도록 설계를 변경하자
```
    private DiscountPolicy discountPolicy;
```
- 구현체가 없는데 그럼 어떻게 실행될까?
  - 실제 실행시키면 NullPointException이 발생한다.
- 이 문제를 해결하려면 누군가 클라이언트인 OrderServiceImpl에 DiscountPolicy의 구현 객체를 대신 생성하고 주입해 주어야 한다.
- OrderServiceImpl는 DiscountPolicy의 구현체를 직접 선택하여 주입하는건 너무 다양한 책임을 갖고 있는걸로
- 관심사가 분리 되어야 한다.

- 해당 관심사를 관리하는 설정 클래스를 만들어보자 AppConfig
- AppConfig
  - 실제 동작에 필요한 구현 객체를 생성한다.(MemberServiceImpl, MemoryMemberRepository, OrderServiceImpl, FixDiscountPolicy)
  - AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결) 해준다.
    - MebmerServiceImpl -> MemoryMemberRepository
    - OrderServiceImpl -> MemoryMemberRepository, FixDiscountPolicy
      * 객체의 생성과 연결은 AppConfig 가 담당한다
* DIP : MemberServiceImpl은 더이상 memberREpository인 추상에만 의존하게 된다.


// 정리

기존의 코드에서는 ~ServiceImpl 에서 interface와 구현체모두 의존하고 있었음
 - ex) MemberRepository memberRepository = new MemoryMemberRepository();

이로써 DIP와 OCP모두 위반하는 상황이 발생하였고, 이를 위해 AppConfig 라는 클래스를 만들어

~ServiceImpl 에서 직접 인터페이스와, 구현체를 의존하지 말고 생성자를 통해서 주입받는 형식으로 변환했음

이로인해 DIP와 OCP모두 위반하지 않고 ServiceImpl에서는 오로지 interface만 의존하게 되고 이로인해

구현체를 직접 의존할 필요가 없어짐

```
 public class MemberServiceImpl implement MemberService {
     private MemberRepository memberRepository;
     
     public 생성자(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
     }
 }
 
 class AppConfig {
    public MemberService() {
        new MemberServiceImpl(new MemoryMemberRepository());
    }
 }
 

```
MemberServiceImpl 이 MemberService를 구현하고 있으니

MemberService 를 리턴받아 이를 통해 MemberServiceImpl을 사용할 수있따. 

즉 AppConfig가 리턴하는 MemberService는 결국 MemberServiceImpl 인거고 

MemoryMemberRepository 또한 포함되어있는 상태이다.

