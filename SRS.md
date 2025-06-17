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



AppConfig 의 등장으로
- 사용영역과
- 구성영역으로 구분이 되어짐

- 할인 정책 역할을 담당하는 구현을 FixFDiscountPolicy -> RateDiscountPolicy 객체로 변경
- AppConfig만 변경하면되고, 사용영역의 OrderServiceImpl 영역은 변경하지 않아도 된다.

좋은 객체 지향 설계의 5가지 원칙 적용 
 > 이프로젝트에서 SRP, DIP, OCP 적용
 
- SRP : 한 클래스는 하나의 책임만 가져야 한다. 
  - 클라이언트 객체는 직접 구현 객체를 생성하고, 연결하고, 실행하는 다양한 책임을 가지고 있었음
  - SRP 단일 책임 원칙을 따르면서 관심사를 분리함
  - 구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당하도록 
  - 클라이언트 객체는 실행하는 책임만 담당

- DIP : 추상화에 의존해야하지 구체화에 의존하면 안된다, 의존성 주입은 이 원칙을 따르는 방법중 하나다
  - OrderServiceImpl 은 DIP를 지키면 DiscountPolicy 인터페이스를 의존하는것 같았찌만 FixDiscountPolicy 구체화 구현 클래스도 함께 구현했었음
  - 클라이언트 코드가 DiscountPolicy 인터페이스만 의존하도록 코드를 변경
  - 하지만 클라이언트 코드는 인터페이스만으로는 아무것도 실행할 수없었다.
  - 그래서  AppConfig 가 FixDiscountPolicy 객체 인스턴스를 클라이언트 코드 대신 생성하여 클라이언트 코드에 의존관계를 주입해줬다.
  - 이렇게 해서 DIP를 지키면서 문제도 해결했다

- OCP : 소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다.
  - 다형성을 사용하고 클라이언트가 DIP를 지킴
  - 애플리케이션을 사용영역과 구성영역으로 나눔
  - AppConfig 가 FixDiscountPolicy ->  RateDiscountPolicy로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드는 변뎐하지 않아도 됨
  - 소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀있다.


============================================
Ioc, DI, 컨테이너
- 제어의 역전 (Ioc : Inversion of Control)
  - 기존 프로그램은 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성하고, 연결하고 실행했다. 
  - 반면에 AppConfig 가 등장한 이후 구현 객체는 자신의 로직을 실행하는 역할만 담당한다. 프로그램의 제어 흐름은 AppConfig가 가져간다
  - 즉 OrderServiceImpl은 필요한 인터페이스들을 호출하지만 어떤 구현 객체들이 실행되는지 모른다.
  - 프로그램에 대한 제어 흐름에 대한 권한은 모두 AppConfig가 가지고 있다. 심지어 OrderServiceImpl도 AppConfig가 생성한다.
  - 이렇게 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전이라고 한다.

- 프레임 워크 vs 라이브러리
  - 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다.
  - 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리이다.

- 의존관계 주입(DI)
  - OrderServiceImpl 은 DiscountPolicy 인터페이스에 의존한다. 실제 어떤 구현 객체가 사용될지는 모른다. 주입될뿐
  - 의존 관계는 '정적인 클래스 의존관계와, 실행 시점에 결정되는 동적인 객체(인스턴스)의존관계' 들을 분리해서 생각해야 한다.
    - 정적인 클래스 의존관계 : 클래스가 사용하는 import 코드만 보고 의존관계를 쉽게 판단할 수 있다. 정적인 의존관계는 애플리케이션을 실행하지 않아도 분석 할 수 있다.
    - 동적인 객체 인스턴스 의존관계 : 애플리케이션 실행 시점에 실제 생성된 객체 인스턴스의 참조가 연결된 의존관계다
      - 실행 시점에 외부에서 실제 구현 객체를 생성하고, 클라이언트에 전달해서 클라이언트와 서버의 실제 의존 관계가 연결되는 것을 의존관계 주입이라고 한다.
      - 객체 인스턴스를 생성하고, 그참조값을 전달해서 연결된다.
      - 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고 클라이언특다 호출하는 대상의 타입 인스턴스를 변경할 수 있따.
      - 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다

- Ioc컨테이너, DI 컨테이너
  - AppConfig 처럼 객체를 생성하고 관리하면서 의존관계를 연결해주는 것을 Ioc컨테이너 또는 DI 컨테이너 이라고한다.



==========================
- 스프링 컨테이너 : ApplicationContext를 스프링 컨테이너라 한다.
  - 기존에는 개발자가 AppConfig를 사용해서 직접 객체를 생성하고 DI 했지만, 이제부터는 스프링 컨테이너를 통해서 사용한다.
  - 스프링 컨테이너는 @Configuration이 붙은 AppConfig를 설정 정보로 사용한다. 여기서 @Bean 이라 적힌 메서드를 모두 호출해 반환된 객체를 스프링 컨테이너에 등록한다.
  - 스프링 빈은 @Bean 이 붙은 메서드를 명을 스프링빈의 이름으로 사용한다  @Bean(name="") 으로 변경도 가능하다.
  - 이전에는 필요한 객체를 AppConfig를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통해서 필요한 스프링 빈을 찾아야 한다
  - 스프링 빈은 applicationContext.getBean() 메서드를 사용해서 찾을 수 있다.
  - 기존에는 직접 자바코드로 모든것을 했다면 이제 스프링 컨테이너에 객체를 스프링 빈으로 등록하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.


