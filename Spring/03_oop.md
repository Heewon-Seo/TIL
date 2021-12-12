# Section 3 - 스프링 핵심 원리 이해 2 - 객체 지향 원리 적용

### RateDiscountPolicy 적용

```java
package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RateDiscountPolicyTest {

    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Test
    @DisplayName("VIP는 10% 할인이 적용되어야 한다")
    void vip_o() {
        // given
        Member member = new Member(1L, "memberVIP", Grade.VIP);

        // when
        int discount = discountPolicy.discount(member, 10000);

        // then
        assertThat(discount).isEqualTo(1000);

    }

    @Test
    @DisplayName("VIP가 아니면 할인이 적용되지 않아야 한다")
    void vip_x() {
        // given
        Member member = new Member(1L, "memberBASIC", Grade.BASIC);

        // when
        int discount = discountPolicy.discount(member, 10000);

        // then
        assertThat(discount).isEqualTo(1000);
        // static import 하는게 좋음 
        /*
        테스트 결과
        Expected :1000
        Actual   :0
         */
    }

}

```

현재는 구체 클래스를 변경할 때 클라이언트 코드도 변경해야하는 문제점이 있음
어떻게 해결?

```java
private final DiscountPolicy discountPolicy;
```

그래서 이렇게 new를 없애고 interface만 의존 했더니
구현체가 없어서 실행이 안됨 NPL(Null Pointer Exception) 발생

**해결방안**

이 문제를 해결하려면 누군가 클라이언트인 'OrderServiceImpl'에 'DiscountPolicy'의 구현 객체를 대신 생성하고 주입해줘야 함

## 관심사의 분리

어떤 구현체가 할당될지는 다른 친구가 해줘야 함 > AppConfig

## AppConfig
구현 객체를 결정하고 생성하는 별도의 클래스

```java
package hello.core;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class AppConfig {

    // 기존에는 MemberServiceImpl에서 직접 new를 해줬지만 이제는
    // AppConfig에서 다 해줌

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }


}
```

생성한 객체 인스턴스의 참조(레퍼런스)를 **생성자를 통해서 주입** 해준다

Impl 클래스 입장에서 생성자를 통해 어떤 구현 객체가 주입될지는 알 수 없고
오직 외부(AppConfig)에서 결정된다
Impl은 의존관계에 대한 고민 하지 않고 실행에만 집중하면 된다

클라이언트인 Impl입장에서 봅면 의존관계를 마치 외부에서 주입해주는 것 같다고 해서 DI(Dependency Injection), 의존관계 주입이라고 한다

#### 실행 예

```java
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;

public class MemberApp {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();
//        MemberService memberService = new MemberServiceImpl();
        Member member = new Member(1L, "memberA", Grade.VIP);

        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find member = " + findMember.getName());
    }
}
```

### 테스트 코드도 수정해줌

```java
package hello.core.order;

import hello.core.AppConfig;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

//    MemberService memberService = new MemberServiceImpl();
//    OrderService orderService = new OrderServiceImpl();

    MemberService memberService;
    OrderService orderService;

    // 테스트 코드를 실행하기 전에 무조건 한 번 실행되는 메소드
    @BeforeEach
    public void beforeEach() {
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        orderService = appConfig.orderService();
    }

    @Test
    void createOrder() {
        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
}
```

이렇게 하면 인터페이스만 고민해서 실행만 하면 됨

### AppConfig Refactoring

```java
package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class AppConfig {

    // 기존에는 MemberServiceImpl에서 직접 new를 해줬지만 이제는
    // AppConfig에서 다 해줌

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

}
```

- 이렇게 하면 메소드명만 봐도 역할이 다 드러남
- 설계 정보에 대한 것이 그대로 드러나도록 리팩터링
- new MemoryMemberRepository() 부분이 중복 제거 됨
- 역할과 구현 클래스가 한눈에 들어옴

## SOLID 원칙 적용

SRP, DIP, OCP 적용됨

### SRP 단일 책임 원칙
- 관심사를 분리하면서 AppConfig 클래스 생성
- AppConfig에서 구현 객체를 생성 및 연결
- 클라이언트(Impl) 객체는 실행하는 책임만

### DIP 의존관계 역전 원칙
- 처음에는 클라이언트 객체에서 구현 클래스에서 의존했음
- 추상화 인터페이스에만 의존하도록 코드 변경
- AppConfig에서 객체 인스턴스를 생성해서 의존 관계를 주입해주면서 해결

### OCP 확장에는 열려있고 변경에는 닫히게
- 다형성 사용 O
- 클라이언트가 DIP 지킴
- DiscountPolicy 변경 시에도 AppConfig만 수정하면 됨
- **소프트웨어 요소를 새롭게 확장해도 사용 영역(클라이언트) 변경은 닫힘**

## IoC, DI, Container

### IoC (Inversion of Control) 제어의 역전
- 프로그램에 대한 제어 흐름의 권한은 모두 AppConfig가 가지고 있음
- 이렇게 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을
- 제어의 역전(IoC)라고 한다

### 프레임워크 & 라이브러리
- 프레임워크: 내가 작성한 코드를 제어하고 대신 실행 (JUnit)
- 라이브러리: 내가 작성한 코드가 직접 제어의 흐름을 담당

### DI (Dependency Injection) 의존관계 주입
- 정적 클래스 의존관계와 실행 시점에 결정되는 동적인 객체(인스턴스) 의존관계를 분리해서 생각
- 정적인 의존관계 - 클래스 다이어그램
- 동적인 객체 인스턴스 의존관계 - 객체 다이어그램 (FixDiscountPolicy, RateDiscountPolicy)
- **의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고 동적인 객체 인스턴스 의존관계를 변경할 수 있다**

### IoC 컨테이너 / DI 컨테이너
- AppConfig처럼 객체를 생성하고 관리하면서 의존관계를 연결 > IoC 컨테이너 or DI 컨테이너
- 주로 DI 컨테이너라고 부름

## 스프링 컨테이너
**ApplicationContext = 스프링 컨테이너**

스프링한테 환경정보를 던져주고 스프링이 읽어와서 필요한걸 가져와서 설정을 해주고
getBean 해서 이거를 꺼낼거야! 하는걸 지정해주면 됨

```java
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//        OrderService orderService = appConfig.orderService();
//        MemberService memberService = appConfig.memberService();
//        MemberService memberService = new MemberServiceImpl();
//        OrderService orderService = new OrderServiceImpl();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService",MemberService.class);
        // 여기에 들어가는 이름은 메소드 이름, 그리고 실제 클래스
        OrderService orderService = applicationContext.getBean("orderService",OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 30000);

        System.out.println("order = " + order);
    }
}
```



### 스프링 컨테이너의 장점
다음시간에..
