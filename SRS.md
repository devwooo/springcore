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


==========================
## 스프링 컨테이너와 스프링 빈
- 스프링 컨테이너 생성과정
```
    ApplicationContext applicationContext = new AnnotationconfigApplicationContext(Appconfig.class);
```
- ApplicationContext를 스프링 컨테이너 라고 하며, ApplicationContext는 인터페이스 이다. XML기반으로 생성할 수 있고, 애노테이션 기반 자바 설정 클래스로 만들수 있다.
- new AnnotationconfigApplicationContext(AppConfig.class); 이클래스는 ApllicationContext의 구현체 이다.
- 더 정확히는 스프링 컨테이너를 말할때 BeanFactory, ApplicationContext를 구분해서 말한다.

  - Appconfig.class를 읽어 
  - @Bean(name="지정할 Bean이름") 이 붙어있는 함수를 스프링컨테이너에 객체로 저장한다.
  - 의존관계 설정 

## 컨테이너에 등록한 BEAN 확인
- 모든 빈 출력
  - String[] beanDefinitionNames = ac.getBeanDefinitionNames();
- 직접등록한 빈만 출력
  - BeanDefinition beanDefinition = ac.getBeanDefinition(bdn); 
  - beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION
- 빈 이름으로 조회
  - MemberService memberService = ac.getBean("memberService", MemberService.class);
- 빈 타입으로 조회
  - MemberService memberService = ac.getBean(MemberService.class);

- 동일한 타입이 둘 이상인경우 > 오류가 발생한다 이때는 빈 이름을 지정하자
  - ac.getBeansOfType()으로 해당 타입의 모든 빈을 확인 할 수 있다.


## 스프링 빈 조회 - 상속관계
  - 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
  - 따라서 object 타입을 조회하면 모든 스프링 빈을 조회한다.


## BeanFactory 와 ApplicationContext
- BeanFactory(interface)
  - 스프링 컨테이너의 최상위 인터페이스
  - 스프링 빈을 관리하고 조회하는 역할을 담당
- ApplicationContext
  - BeanFactory 기능을 모두 상속받아서 제공한다
  - BeanFactory가 빈을 검색하고 관리하는 기능을 제공한다면 그렇다면 ApplicationContext와 는 무슨 차이가 있는걸까
  - 부가 기능을 제공해준다
    - MessageSource, EnviromentCapable, ApplicationEvnetPublisher, ResourceLoader 등등 다양한 인터페이스를 받고 있다.
      - MessageSource : 한국어로 들어오면 한국어로, 영어권으로 들어오면 영어로 출력
      - EnviromentCapable : 환경변수 > 로컬, 개발, 운영등을 분리해서 처리 가능
      - ApplicationEvnetPublisher : 이벤트를 발행하고 구독하는 모델을 편리하게 지원
      - ResourceLoader : 파일, 클래스 패스, 외부 등에서 리소스를 편리하게 조회

## 자바코드, XML 설정방식
- ApplicationContext <-  AnnotationConfigApplicationContext(AppConfig.class), GenericXmlApplicationContext(appConfig.xml), XxxApplicationContext(appConfig.xxx)

## BeanDefinition
  - BeanDefinition 이라는 추상화가 있다.
  - BeanDefinition을 빈 설정 메타 정보라 한다
    - @Bean, <bean> 당 각각 하나씩 메타 정보가 생성된다.
  - 스프링 컨테이너는 이 메타정보를 기반으로 빈을 생성한다.

  - AnnotationConfigApplicationContext가 AnnotatedBeanDefinitionRead 사용해서 > AppConfig.class를 읽어 > BeanDefinition 생성한다.
  - BeanDefinition을 직접 생성해서 스프링 컨테이너에 직접 등록할 수도 있다. 하지만 실무에서 직접 정의하거나 사용할 일은 없다.


## WebApplication과 SingleTon
- 웹 어플리케이션은 보통 여러 고객이 동시에 요청한다.
- 기존의 AppConfig는 요청을 할 때 마다 객체를 새로 생성한다. > 메모리 낭비가 심하다.
- 해결방안은 해당 객체가 딱 1개만 생성되고, 공유하도록 설계하면 된다 > 싱글톤

## Singleton
- 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴.
- 객체 인스턴스를 2개 이상 생성하지 못하도록 막아야 한다.
  - private 생성자를 통해 외부에서 접근하지 못하게 한다.
- static 영역에 객체 instance를 미리 하나 생성해 올려둔다
- 객체가 필요하면 getInstance()를 통해서만 조회할 수 있다. 따라서 항상 같은 인스턴스를 반환한다.
- 생성자를 private으로 함으로써 외부에서 new 키워드로 객체 인스턴스가 생성되는 것을 막는다.

## Singleton 의 문제점
- 코드자체가 많이 들어간다
- 클라이언트가 구체 클래스에 의존한다. > DIP 위반
- 클라이언트가 구체 클래스에 의존해서 OCP를 위반할 가능성이 높다.
- 유연성이 떨어지고
- 안티패턴으로 불리기도 한다.

## Singleton Container
- 스프링 컨테이너는 싱글톤 패턴의 문제를 해결함으로써, 객체 인스턴스를 싱글톤으로 관리한다.
- 스프링 컨테이너는 싱글톤 패턴을 적용하지 않아도 객체 인스턴스를 싱글톤으로 관리한다.
- 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스트리라 한다.
- 스프링 컨테이너의 이른 기능 덕분에 싱글톤 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수있다.
  - 싱글톤 패턴의 지저분한 코드가 들어가지 않아도된다.
  - DIP, OCP, 테스트, private생성자로 부터 자유롭게 싱글톤을 사용할 수 있다.
  
## Singleton 방식의 주의점
- 싱글톤 패턴이든, 스프링과 같은 싱글톤 컨테이너를 사용하든. 객체 인스턴스를 하나만 생성해서 공유하는 싱글톤 방식은 여러 클라이언트가
- 하나의, 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지 하게 설계하면 안된다.
- 무상태로 설계해야한다.
  - 특정 클라이언트에 의존적인 필드가 있으면 안된다
  - 값을 변경할수 있는 필드가 있으면 안된다.
  - 가급적 읽기만 가능해야한다.
  - 자바에서 공유되지 않는 지역변수, 파라미터, ThreadLocal등을 사용해야한다.
  - 스프링 빈의 필드에 공유값을 설정하면 정말 큰 장애가 발생할 수 있따.
  
## @Configuration과 싱글톤
```
AppConfig Bean 등록시 호출되는 메시지 예상결과
call AppConfig.memberService
call AppConfig.memberRepository
call AppConfig.memberRepository
call AppConfig.orderService
call AppConfig.memberRepository

실제 결과
call AppConfig.MemberService
call AppConfig.memberRepository
call AppConfig.orderService
```

## @Configuration과 바이트코드 조작의 마법
- 스프링 컨테이너는 싱글톤 레지스트리다. 따라서 스프링 빈이 싱글톤이 되도록 보장해주어야 한다.
- 그런데 스프링이 자바 코드까지 어떻게 하기 어렵다. 위에 실제 결과를 보면 3번 호출되는게 맞다
- 그래서 스프링은 클래스의 바이트 코드를 조작하는 라이브러리를 사용한다.
```
순수한 클래스라면 class hello.core.config.AppConfig 되어야 하는데
class hello.core.config.AppConfig$$SpringCGLIB$$0 이렇게 찍힘

AppConfig <- AppConfig@CGLIB 
임의의 다른 클래스가 바로 싱글톤이 보장되도록 해준다.

AppConfig@CGLIB 예상코드
@Bean
public MemberRepository memberRepository() {
  if (memberRepository가 이미 스프링 컨테이너에 등록되어 있으면 ?) { 
    return 스프링 컨테이너에서 찾아서 반환
  } else {  // 스프링 컨테이너에 없으면
    기존 로직을 호출해서 MemoryMemberRepository를 생성 하고 스프링 컨테이너에 등록
    return 반환
    
  }

}
```
- 정리하자면
  - @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고, 
    - 스프링 빈이 없으면 생성해서 스프링 빈으로 등록 후 반환하는 코드가 동적으로 마늗ㄹ어진다
    - 덕분에 싱글톤이 보장되는 것이다
  - AppConfig@CGLIB는 AppConfig의 자식 타입이므로, AppConfig 타입으로 조회가 가능하다.  

- @Configutation 없이 @Bean만 사용할 경우 Bean 등록이 되지만 싱글톤은 보장되지 않는다.


## 컴포넌트 스캔과 의존관계 자동 주입 시작하기
 - @Bean이나 XML 등을 통해서 설정 정보에 직접 등록할 스프링 빈을 나였했다.
 - 이렇게 일일이 등록하게 되면 문제가 발생할 수 있다. 따라서 스프링은 설정 정보가 없어도 자동으로 
 - 스프링 빈을 등록하는 컴포넌트 스캔이라는 기능을 제공한다.
 - 의존 관계도 자동으로 주입하는 @Autowired 기능을 제공한다.
 - 
 - 컴포넌트 스캔을 사용하려면 @ComponentScan 을 붙여준다.
 - 기존의 AppConfig와는 다르게 @Bean이 존재하지 않는다.
    - 컴포넌트 스캔은 이름 그대로 @Component 애노테이션이 붙은 클래스를 스캔해서 스프링 빈으로 등록한다.
 - @Component 스캔에 의해서 Bean으로 등록될때 의존관계 주입을 하기 위해서는 @Autowired를 붙여주면 자동으로 의존성을 주입해준다
   - @Autowired가 붙어있는 메서드의 파라미터의 타입을 찾아 넣어준다.
 - @Component 어노테이션은 구현체에 등록해줘야 한다.
    - 빈 이름 기본 전략 : MemberServiceImpl > memberServiceImpl
    - 빈 이름 직접 지정 : @Component("memberServiceImpl") 이런식으로

## 탐색 위치와 기본 스캔 대상
- 탐색할 위치를 지정 할 수 있다.
  - basePackages = "helle.core"  >> hello.core 해당 패키지부터 하위패키지를 모두 찾아서 등록한다. 
  - 여러개 등록시 basePackages = {"1", "2", "3","4"}
  - 만약 지정하지 않으면 해당 @ComponentScan 을 붙인 클래스의 패키지를 기준으로 하위 패키지를 찾아서 등록한다.
  - basePackageClasses = "" 지정시 해당 클래스의 패키지가 기준이 되어 하위 패키지를 찾아서 등록한다.

  - 컴포넌트 스캔의 대상 
    - @Component
    - @Controller
    - @Service
    - @Repository : 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환해준다.
    - @Configuration
  
## 필터
  - userDefaultFilter 옵션은 기본으로 켜져있는데, 이옵션을 끄면 기본 스캔 대상들이 제외된다.
  - includeFilters >> 해당 어노테이션이 붙으면 스프링 빈에 등록해라 >> 사용자 어노테이션을 추가하여 스프링 생성 하는거겠죠?
    - includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
  - excludeFilter >> 해당 어노테이션이 붙으면 스프링 빈에 등록하지 않는다 >> 스프링 빈 생성 방지 
    - excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)

  - FilterType 옵션
    - ANNOTATION : 기본값, 애노테이션을 인식해서 동작
      - org.example.SomeAnnotation
    - ASSIGNABLE_TYPE : 지정한 타입과 자식 타입을 인식해서 동작한다.
      - org.example.SomeClass
    - ASPECTJ : AspectJ 패턴 사용
      - org.example..*Service+
    - REGEX : 정규표현식
      - org/.example/.Default.*
    - CUSTOM : TypeFilter라는 인터페이스를 구현해서 처리

## 중복 등록과 충돌
  - 같은 빈 이름을 등록하면 어떻게 될까?
    1. 자동 빈 등록 vs 자동 빈 등록
       - 컴포넌트 스캔에 의해 자동으로 스프링빈이 등록되는데 이름이 같을 경우 오류를 발생시킨다. ConflictingBeanDefinitionException
       2. 수동 빈 등록 vs 자동 빈등록
          - 수동 빈 등록이 우선권을 가진다. 
          - 수동 빈 등록시 남는 로그
          ```
            Overriding bean definition for bean 'memoryMemberRepository' with a diffrent 
            definition: replacing
           ```
          
    - 최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 기본 값을 바꾸었다.
      - 수동 빈 등록, 자동 빈 등록 오류시 스프링 부트 에러
      ```
      Description:
      The bean 'memoryMemberRepository', defined in class path resource [hello/core/config/AutoAppConfig.class], could not be registered. A bean with that name has already been defined in file [E:\20250101\springcore\out\production\classes\hello\core\repository\MemoryMemberRepository.class] and overriding is disabled.
      Action:
      Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
      
      application.yml 또는 application.properties 에서
      spring.main.allow-bean-definition-overriding=true 지정하면 오버라이딩 되도록 설정하게 된다.
      ```
      

## 의존관계 자동 주입
- 생성자 주입
  - 생성자 호출시점에 딱 1번만 호출되는게 보장된다. // 불변, 필수 의존관계에 사용
  - 생성자가 단 한개만 있는경우 스프링 컨테이너가 자동으로 @Autowired를 붙여준다.
```
//관심사의 분리
private final MemberRepository memberRepository;
private final DiscountPolicy discountPolicy;

@Autowired
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

- 수정자주입(setter주입)
  - 필드 값을 수정하는 경우 set
  - 생성자 이후에 이뤄진다.
  - 선택, 변경이 가능하다.
  - @Autowired는 주입할 대상이 없으면 오류 발생, 따라서 주입 대상이 없어도 동작 가능하게 하려면 @Autowired(required=false) 값을 주어야 한다.
```
    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
    
```
- 필드주입
  - 외부에서 변경이 불가능해서 테스트하기 힘들다는 치명적인 단점이 있다.
  - DI 프레임워크가 없으면 아무것도 할 수 없다.
  - 왠만하면 사용하지 말자, 테스트 코드에서는 사용해도 된다.
```
    // 필드 주입방법
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DiscountPolicy discountPolicy;

    
    순수 자바로 테스트시 
    @Test
    void fieldInjectionTest(){
        // 내가 직접적으로 new 해서 생성하는 OrderServiceImpl는 Injection이 되지 않는다. 
        OrderServiceImpl orderService1 = new OrderServiceImpl();
        orderService1.createOrder(1L, "grape", 10000);
    }
    NullPointerException 발생한다.
    java.lang.NullPointerException: Cannot invoke "hello.core.repository.MemberRepository.findById(java.lang.Long)" because "this.memberRepository" is null
    
    따라서 추가적인 setter 를 추가해줘야 한다.


```
- 일반 메서드 주입
  - 아무메서드에 @Autowired를 붙이면 한번에 여러 필드를 주입 받을 수 있다. // 일반적으로 잘 사용하지 않는다.
  - 스프링 빈이 아닌 Member 클래스에서 @Autowired를 붙인다고 주입받지 않는다.


## 옵션처리
- 주입할 스프링 빈이 없어도 동작 해야 할 떄가 있다 
  - @Autowired만 사용하면 required 옵션의 기본 값이 true이므로 자동 주입 대상이 없으면 오류가 발생한다.

- 자동 주입 대상을 옵션으로 처리하는 방법은 다음과 같다.
- @Autowired(required=false)  : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출 안됨
```
@Autowired(required = false)
public void setNoBean1(Member noBean1){
System.out.println("noBean1 = " + noBean1);
}

출력값 
X  >> setNoBean1() 은 @Autowired(required=false) 이므로 호출 자체가 안됨
```
  - org.springframework.lang.@Nullable : 자동주입할 대상이 없으면 null이 입력된다.
```
@Autowired
public void setNoBean2(@Nullable Member noBean2){
    System.out.println("noBean2 = " + noBean2);
}
출력값
noBean2 = null >> null처리된다.
```
  - Optional<> : 자동 주입할 대상이 없으면 Optional.empty 가 입력된다.
```
@Autowired
public void setNoBean3(Optional<Member> noBean3){
    System.out.println("noBean3 = " + noBean3);
}
출력값
noBean3 = Optional.empty >> Optional의 empty 처리
```


## 생성자 주입을 선택해라 !
- 수정자 주입과, 필드 주입을 많이 사용했지만, 최근 스프링을 포함한 DI 프레임워크 대부분이
- 생성자 주입을 권장 하고 있따.

- 불변
  - 대부분의 의존관계 주입은 한번 일어나면 애플리케이션 종료 시점까지 변경할 일이 없다.
  - 수정자 주입을 사용하면 setXxx 메서드를 pulic으로 열어 두어야 하고
  - 그로인해 실수로 변경될수가 있다 따라서 생성자 주입은 객체 생성시 딱 1번만
  - 호출되므로 이후에 호출될 일이 없고, 따라서 불변하게 설계 할 수 있따.
- 누락
  - 프레임워크 없이 순수 자바 코드로 단위 테스트 하는 경우에 
  - 수정자 의존관계인경우 의존관계 주입이 생략 될 수 있다.
  - 생성자 주입의 경우 컴파일 에러가 발생하여 바로 알아 차릴 수 있다.

- final 키워드
  - 생성자에서 혹시 값이 설정되지 않는 오류를 컴파일 시점에서 막아준다.
  - 수정자 주입을 포함한 나머지 주입 방식은 모두 생성자 이후에 호출되므로 피륻에
  - final키워드를 사용 할 수 없다,오직 생성자 주입 방식만 final키워드를 사용 할 수 있다.

## 정리
- 생성자 주입 방식을 선택하는 이유는, 프레임 워크에 의존하지 않고,
- 순수한 자바 언어의 특징을 잘 살리는 방법이다.
- 기본적으로 생성자 주입을 사용하고, 필수 값이 아닌 경우에는 수정자 주입방식을
- 옵션으로 부여하면 된다. 생성자 주입 방식과 + 수정자 주입방식은 동시에 사용 할 수 있다.
- 항상 생성자 주입을 선택하고, 옵션이 필요하면 수정자 주입을 선택해라 
- 필드 주입방식은 사용하지 않는게 좋다.

## 롬복과 최신 트렌드
- @RequiredArgsConstructor  : final 이 붙은 필드를 포함하는 생성자를 만들어준다. 
- 의존성 주입을 lombok 이 자동으로 해줌

## 조회 빈이 2개 이상 - 문제
 - @Autowired는 Type으로 조회하기 때문에
 - ac.getBean(DiscountPolicy.class) 와 같이 동작한다(더 많은 기능을 제공함)
 - 따라서 DiscountPolicy 의 하위타입인 Fix~, Rate~ 둘다 스프링 빈으로 등록시에
 - NoUniqureBeanDefinitionException 오류가 발생한다
 - 스프링 빈을 수동 등록해서 문제 해결하면 되지만 자동 주입으로도 해결 가능하다.

## 문제 >> 해결방법
- @Autowired 필드명 매칭
- @Autowired는 타입 매칭을 시도하고, 이때 여러 빈이 있다면, 필드이름, 파라미터 이름으로 빈 이름을 추가 매칭한다.
  ```
      기존코드
      @Autowired
      private DiscountPolicy discountPolicy
    
      필드 명을 빈 이름으로 변경
      @Autowired
      private DiscountPolicy rateDiscountPolicy
    
    
  ```
  - @Quilifier > @Quilifier 끼리 매칭 > 빈 이름 매칭
    - 추가 구분자를 붙여주는 방법, 주입시 추가적인 방법을 제공하는거지 빈 이름을 변경하는건 아니다
    ```
    생성자 자동 주입 예시
    @Autowired
    public OderServiceImpl(MemberRepository memberRepositorym
                      @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy){
      this.memberRepository = memberRepository;
      this.discountPolicy = discountPolicy; 
    }
    수정자 자동 주입 예시
    @Autowired
    public DiscountPolicy setDiscountPolicy(@Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy){
      return discountPolicy;
    }
    ```
    - @Qualifier 로 주입할때 @Qualifier("mainDiscountPolicy") 를 못찾으면
    - mainDiscountPolicy이름의 스프링 빈을 추가로 찾는다. @Qualifier는 @Qualifier 찾는
    - 용도로만 사용하는게 명확하다.
    - 직접 @Bean 등록시에도 동일 하게 사용가능하다
    ```
      @Bean
      @Qualifier("mainDiscountPolicy")
      public DiscountPolicy discountPolicy() {
       retur new ~
      }
    ```
    - @Qualifier 는 스프링 빈 등록하기 위한 애노테이션은 아니고 스프링 빈으로 등록된것 들 중에 이것을 선택하겠다라고 지정하는 개념
    ```
    @Component
    public class Dog implements Animal {
      @Override
      public void sound() {
        System.out.println("멍멍");
      }
    }
    
    @Component
    public class Cat implements Animal {
      @Override
      public void sound() {
        System.out.println("야옹");
      }
    }
    
    @Component
    public class AnimalService {
      private final Animal animal;
      // 여러개의 Animal 들중에 dog를 넣어주라고 지정 
      public AnimalService(@Qualifier("dog") Animal animal) {
          this.animal = animal;
      }
    }
    
    
    ```
- @Primary 사용
  - 우선순위를 정하는 방법이다.
  - @Autowired 시에 여러 빈 매칭되면 @Primary가 붙어있는 빈이 우선순위가 된다.

- 우선순위는 Qualifier >>>> Primary 로  Qualifier 가 더 높다


## 애노테이션 직접 만들기
 - @Qualifier("문자") 의경우 컴파일 타입 체크가 안된다. >> 애노테이션을 직접 만들어 사용하므로써 해결할 수 있다.

## 조회한 빈이 모두 필요할떄 List, Map
- Map, List 로 모든 DiscountPolicy를 주입받는다. 이때 fixDiscountPolicy, rateDiscountPolicy가 주입된다.
- discount() 메서드는 discountCode로  Map에서 주입됬던 bean을 찾아 실행한다.
```
@Autowired
public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
  this.policyMap = policyMap;
  this.policies = policies;
  System.out.println("policyMap = " + policyMap);
  System.out.println("policies = " + policies);
}
policyMap = {fixDiscountPolicy=hello.core.repository.FixDiscountPolicy@29df4d43, rateDiscountPolicy=hello.core.repository.RateDiscountPolicy@5dd91bca}
policies = [hello.core.repository.FixDiscountPolicy@29df4d43, hello.core.repository.RateDiscountPolicy@5dd91bca]

```

## 자동, 수동 올바른 실무 운영 기준
- 자동 기능을 기본으로 사용
- 수동 빈 등록은 언제 사용하나?
  - 업무 로직 빈 : 자동 빈 등록
    - 비즈니스 로직 중 다형성을 적극 활용 할 때 
    - ```
          이렇게 다형성을 사용해서
          Map<String, DiscountPolicy> map ... 이런식으로 DI 받아서 사용하는 경우 한눈에 보기쉽게 해당 클래스에 대해서 수동 등록하는것도 좋다.
          @Configuration
          public class DiscountPolicyConfig {
            @Bean
            public DiscountPolicy rateDiscountPolicy() {...}
            @Bean
            public DiscountPolicy fixDiscountPolicy() {...}
          }   
      ```
  - 기술 지원 빈 : 수동 빈 등록
    - 기술적인 문제나, 공통 관심사를 처리할 때 주로 사용된다.
    - 데이터베이스 연결이나, 공통 로그처리 처럼 업무 로직을 지원하기 위한 하부 기술이나 공통 기술들이다.
      - 스프링 부트의 경우 DataSouce 같은 데이터베이스 연결에 사용하는 기술 지원 로직까지 내부에서 자동 등록하는데,
      - 이런 부분은 메뉴얼을 잘 참고해서 스프링 부트가 의도한 대로 편리하게 사용하면 된다.
      - 반면에 스프링 부트가 아니라 내가 직접 기술 지원 객체를 스프링 빈으로 등록 한다면 수동으로 등록해서 명확하게 들어내는 것이 좋다.


## 빈 생명주기 콜백
- 데이터베이스 커넥션 풀이나, 네트워크 소켓 처럼 애플리케이션 시작 시점에 필요한 연결을 미리 해두고 애플리케이션 종료 시점에
- 연결을 모두 종료하는 작업을 진행하려면, 객체의 초기화와 종료 작업이 필요하다.

  - 간단하게 외부 네트워크에 미리 연결하는 개체를 하나 생성한다고 가정해보고,
  - 애플리케이션 시작시점에 connect(), 호출해서 연결을 맺어두어야하고, 종료되면 disConnect() 호출해서 연결을 끊어야 한다.
```
객체를 생성하는 단계에는 url이 없고, 객체를 생성한 다음에 외부에서 수정자 주입을 통해서 setUrl()이 호출되어야 url이 존재하게 된다.
생성자 호출, urlnull
connect: null
call : null, message :초기화 연결 메시지

```
  - 객체 생성 -> 의존관계 주입 setter, 필드 injdect (생성자 주입 예외임) 
  - 스프링 빈은 객체를 생성하고, 의존관계 주입이 다 끝난 다음에야 필요한 데이터를 사용 할 수 있는 준비가 완료된다.
  - 따라서 초기화 작업은 의존관계 주입이 모두 완료되고 난 다음에 호출해야 한다. 그렇다면 의존관계 주입이 완료된 시점은 어떻게 확인할 수 있나?
    - 스프링은 의존관계 주입이 완료되면 스프링 빈에게 콜백 메서드를 통해서 초기화 시점을 알려주는 다양한 기능을 제공 한다.
    - 또한 스프링은 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 준다. 따라서 안전하게 종료 작업을 진행할 수 있다.
    -  스프링 빈의 이벤트 라이프사이클(싱글톤에 대한 설명)
      - 스프링 컨테이너 생성 > 스프링 빈 생성 > 의존관계 주입 > 초기화 콜백 > 사용 > 소멸전 콜백 > 스프링 종료
        - 초기화 콜백 : 빈이 생성되고, 빈의 의존관계 주입이 완료된 후 호출
        - 소멸전 콜백 : 빈이 소멸되기 직전에 호출
  - 참고 : 객체의 생성과 초기화를 분리하자
    - 생성자는 필수 정보를 받고, 메모리를 할당해서 객체를 생성하는 책임을 가진다.
    - 반면 초기화는 생성된 값을 활용해서 외부 커넥션을 연결하는등 무거운 동작을 수행한다
    - 따라서 생성자 안에서 무거운 초기화 작업을 함꼐 하는것 보다는 객체를 생성하는 부분과 초기화 하는 부분을 명확하게 나누는 것이 유지보수 관점에서 좋다.
    - 물론 초기화 작업이 내부 값들만 약간 변경하는 정도로 단순한 경우에는 생성자에서 한번에 다 처리하는게 더나을수 있다.

  - 크게 3가지 방법으로 빈 생명주기 콜백을 지원한다.
    - 인터페이스(InitializingBean, DisposableBean)
    - 설정 정보에 초기화 메서드, 종료 메서드 지정
    - @PostConstrict, @PreDestory 애노테이션 지원

## InitializingBean > afterPropertiesSet() 메서드로 초기화를 지원한다.
## DisposableBean > destory()메서드로 소멸을 지원한다.
 - 초기화, 소멸 인터페이스의 단점 > 스프링 전용이다, 해당 코드가 스프링 전용 인터페이스에 의존한다
 - 초기화, 소멸 메서드 이름 변경 불가능, 내가 코드를 고칠수 없는 외부 라이브러리에 적용할 수 없다. 
 - 잘 사용하지 않는다.

## 빈 등록시 초기화, 소멸 메서드 지정
 - 메서드 이름을 자유롭게 줄 수 있다.
 - 스프링 빈이 스프링 코드에 의존하지 않는다
 - 코드 가 아니라 설정 정보를 사용하기 때문에 코드를 고칠 수 없는 외부 라이브러리에도 초기화, 종료메서드를 적용 할 수 있다.
 - @Bean으로 등록하려는 클래스 안에있는 메서드들중에 선택하여 initMethod = "", destoryMethod ="" 를지정한다.
 - @Bean 의 destoryMethod에 특별한 기능이 있다. 기본값이 (inferred)[추론] 로 되어있으며
 - 이 추론 기능은 'close', 'shutdown' 라는 이름의 메서드를 자동으로 호출해준다. 따라서 직접 스프링 빈으로 등록하면
 - 종료메서드는 따로 적어주지 않아도 잘 작동한다.
 - 이 추론기능을 사용하고 싶지 않다면 destoryMethod="" 처럼 빈 공백을 지정하면 된다.

## 애노테이션 @PostConstruct, @PreDestroy
 - 스프링 권장 방법, 자바 표준으로 스프링이 아닌 다른 컨테이너에서도 동작한다.
 - 컴포넌트 스캔과 잘어울린다.
 - 유일한 단점은 외부 라이브러리에는 적용하지 못한다.외부 라이브러리를 초기화, 종료해야하면 @Bean 기능을 사용해야 한다.

## 빈 스코프란?
 - 스프링 빈이 스프링 컨테이너의 시작 ~ 종료까지 유지된다고 여태 학습했다
 - 그 이유는 싱글톤 스코프로 생성되기 때문이다.
 - 스코프란 말대그로 빈이 존재할 수 있는 범위를 말한다.
 
 - 싱글톤 : 기본 스코프, 컨테이너의 시작 ~ 종료까지 유지되는 가장 넓은 범위의 스코프
 - 프로토타입 : 스프링 컨테이너는 프로토타입 빈의 생성과 의존관계 주입까지만 관여하고 더는 관리하지 않는 매우 짧은 범위의 스코프
 - 웹관련 스코프
   - request : 웹 요청이 들어오고 나갈때 까지 유지되는 스코프
   - session : 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프
   - application : 웹의 서블릿 컨텍스와 같은 범위로 유지되는 스코프 
```
>> 컴포넌트 스캔 자동등록 스코프
@Scope("prototype)
@Component
public class HelloBean{}

>> 수동 등록 
@Scope("prototype)
@Bean
PrototypeBean HelloBean() {
    return new HelloBean();
}
```

## 프로토타입 스코프
 - 프로토타입 스코프를 스프링 컨테이너에 조회하면 스프링 컨테이너는 항상 새로운 인스턴스를 생성해서 반환한다.
   - 프로토타입 스코프의 빈을 스프링 컨테이너에 요청 > 스프링 컨테이너는 이시점에 프로토타입 빈을 생성하고, 의존관계를 주입한다. > 초기화 메서드까지 실행
   - 스프링 컨테이너는 생성한 프로토타입 빈을 클라이언트에 반환한다. > 이후에 스프링 컨테이너에 같은 요청이 오면 항상 새로운 프로토타입 빈을 반환한다.
   - ** 핵심은 >> 프로토타입 빈을 생성하고 > 의존관계 주입하고 > 초기화 까지만 처리한다. > 클라이언트에 반환 > 이후 스프링컨테이너가 관리 X
   - ** 프로토타입빈을 관리할 책임은 받은 클라이언트에 있다. 따라서 @PreDestory 같은 종료 메서드가 호출 되지 않는다.
   
 - 싱글톤 빈 : 스프링 컨테이너 생성시점에 초기화 메서드 실행, >> 스프링 컨테이너가 관리하므로 종료 메서드가 실행되지만
 - 프로토타입 빈 : 스프링 컨테이너에서 빈을 조회할때 생성되고, 초기화 메서드도 실행된다. > 스프링컨테이너가 관리하지 않으므로 종료 메서드가 실행되지 않는다.

## 프로토타입 스코프 : 싱글톤 빈과 함께 사용시 문제점
 - 프로토타입 스코프의 빈을 요청하면 항상 새로운 객체 인스턴스를 생성해서 반환한다.
 - 하지만 싱글톤 빈과 함께 사용할 때는 의도한 대로 잘 동작 하지 않는다.

 - clientBean은 싱글톤이므로, 보통 스프링 컨테이너 생성 시점에 함께 생성되고, 의존관계 주입도 발생한다.
   1. clientBean은 의존관계 자동 주입을 사용한다. 주입 시점에 스프링 컨테이너에 프로토타입 빈을 요청한다.
   2. 스프링컨테이너는 프로토타입 빈을 생성해서 clientBean에 반환했다. 프로토타입 빈의 count 필드 값은 0 이다.
   3. 이제 clientBean은 프로토타입 빈을 내부 필드에 보관한다.
   4. 클라이언트 A는 clientBean을 스프링 컨테이너에 요청해서 받는다 > 싱글톤이므로 항상 값은 clinetBean을 반환받는다.
   5. 클라이언트는 A는 clientBean.logic()을 호출한다 > 내부에 prototypeBean.addCount() 를 호출해서 count 값을 반환한다.
   6. 클라이언트 B는 clientBean을 스프링 컨테이너에 요청해서 받는다 > 싱글톤이므로 항상 값은 clinetBean을 반환받는다.
   7. ** clientBean이 내부에 가지고 있는 프로토타입 빈은 이미 과거에 주입이 끝난 빈이다. 주입시점에 스프링 컨테이너에 요청해서 프로토타입 빈이 새로 생성된거지
   8. 사용 할 때마다 새로 생성되는 것은 아니다.
   9. 클라이언트 B는  clientBean.logic()을 호출한다. 
   10. clientBean은 prototypeBean 의 addCount()를 호출해서 프로토타입 빈의 count를 증가한다. 1 > 2가 된다.
    
 - but 내가 원하는것은 프로토타입 빈을 주입 시점에만 새로 생성하는게 아니라, 사용할 때 마다 새로 생성해서 사용하는것을 원한다.
 - 참고 : 여러 빈에서 같은 프로토타입 빈을 주입 받으면, 주입 받는 시점에 각각 새로운 프로토타입 빈이 생성된다.
 - 예를 들자면 ClintA, ClinetB 각각 의존관계 주입을 받으면 각각 다른 인스턴스의 프로토타입 빈을 주입 받는다.
 - ClinetA > prototypeBean@x01
 - ClinetB > prototypeBean@x02
 - 해결 방법에 대해서 알아보도록 하자

## 프로토타입 스코프 - 싱글톤 빈과 함께 사용시 Provider로 문제 해결
 - 싱글톤 빈과 프로토 타입 빈을 함께 사용할 때, 어떻게 하면 사용할 때 마다 새로운 프로토토입 빈을 생성할 수 있을까?
 1. 스프링컨테이너에 요청
 - 아래와 같이 외부에서 주입 받는게 아니라 직접 필요한 의존관계를 찾는 것을 Dependency lookup 의존관계 조회라고 한다.
      ```
      PrototypeBean bean = applicationContext.getBean(PrototypeBean.class);
      bean.addCount();
      int count = bean.getCount();
      return count;
      ```
2. ObjectFactory, ObjectProvider
   - 지정한 빈을 컨테이너에서 대신 찾아주는 DL 서비스를 제공하는게 ObjectFactory, ObjectProvider이다.
   - prototypeBeanProvider.getObject()을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인 할 수 있다.
   ```
    PrototypeBean object = prototypeBeanProvider.getObject();
    object.addCount();
    return object.getCount();
   ```
   
3. JSR-330 Provider 
   - 마지막 방법은 javax.inject.Provider 라는 JSR-330자바 표준을 사용하는 방법으로
   - 이 방법을 사용하려면 'jakarta.inject:jakarta.inject-api:2.0.1' 라이브러리를 추가해줘야 한다.


## 웹 스코프
 - 웹 환경에서만 도앚ㄱ, 종료시점까지 관리한다 따라서 종료 메서드가 호출된다
 - 종류
   - request : HTTP 요청이 들어오고, 나갈 떄 까지 유지됨, 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성됨
   - session 
   - application
   - websocket
 - 로그를 출력하기 위한 MyLogger / @Scope(value = "request"), HTTP 요청당 하나씩 생성되고, HTTP 요처잉 끝나는 시점에 소멸된다.
 - 빈이 생성되는 시점에 자동으로 @PostConstruct 초기화 메서드로 uuid를 지정했고,  해당 uuid는 HTTP 당 하나씩 생성되므로 다른 HTTP 요청과 구분된다.
 - 소멸시점에 @PreDestory 호출된다.

 - 에러발생 
   - Scope 'request' is not active for the current thread
   - 스프링 애플리케이션을 실행하는 시점에 싱글톤 빈은 생성해서 주입이 가능하지만, request Scope Bean은 실제 고객이 요청이 와야 생성할 수 있다
   - 따라서 에러가 발생한것이다
   - 실제 Bean을 받는 단계를 뒤로 미뤄야 한다.(스프링 컨테이너가 동작할때 주입받는게 아니라 요청이 오면 받도록)
    
 - 해결방법
   - ObjectProvider 사용한다.
   - MyLogger의 Proxy 객체가 LogDemoController, LogDemoService에 주입되고
   - 실제 요청이 들어올때 MyLogger Proxy객체를 토대로 실체 인스턴스를 생성하여 주입된다.
   - 한개의 HTTP 요청으로 생성된 MyLogger는  해당 요청에서는 항상 같은 MyLogger 객체를 반환한다.
   - 따라서 각각의 HTTP 요청마다 각개의 MyLogger가 생성되고 해당 HTTP는 controller, service 등등에서 공유 된다.

    
## 프록시와 스코프
 - @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
 - proxyMode 옵션을 추가해주므로써 Provider 없이 정상실행된다.
 - 옵션으로
   - 적용 대상이 인터페이스가 아닌 클래스면 : TARGET_CLASS
   - 적용 대상이 인터페이스인 경우 : INTERFACES를 선택한다.
 - MyLogger의 가짜 프록시 클래스를 만들어주고 HTTP request 에 

 - 결과
 - 가짜 프록시 빈은 내부에 실제 MyLogger를 찾는 방법을 알고 있다.
 - 클라이언트가 myLogger.logic() 을 호출하면 사실은 가짜 프록시 객체의 메서드를 호출한것이다
 - 가짜 프록시 객체는 request 스코프의 진짜 myLogger.logic()을 호출한다.
 - 가짜 프록시 객체는 원본 클래스를 상속 받아서 만들어졌기 때문에 이객체를 사용하는 클라이언트 입장에서는 사실 원본인지 아닌지모르게 동일하게 사용할 수 있따(다형성)

- CGLIB라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어 주입
- 가짜 프록시 객체는 실제 요청이 오면 그때 내부에서 실제 빈을 요청하는위임 로직이 들어있다.
- 내부에 단순한 위임 로직만 있고 싱글톤 처럼 사용이 가능하다.

- 진짜 객체 조회를 꼭 필요한 시점까지 지연처리한다는게 가장 중요한 특징이다.!
- 단지 애노테이션 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있다. 이것이 다형성과 DI 컨테이너가 가진 큰 장점이다.
- 웹 스코프가 아니더래도 프록시는 사용할 수있다.




















