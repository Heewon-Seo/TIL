# Section 7 - 의존관계 자동 주입

## 의존관계 주입 방법
- 생성자 주입
- 수정자 주입(Setter 주입)
- 필드 주입
- 일반 메서드 주입

### 생성자 주입

- 우리가 여태까지 했던 것이 생성자 주입
- 생성자 호출 시점에 딱 1번만 호출 보장
- **불변/필수** 의존관계
- 필수: 생성자에 파라미터로 들어간 것은 웬만하면 null 값을 넣지 않음 (=필수값)
- **중요! 생성자가 딱 1개만 있으면 @Autowired를 생략해도 자동 주입 된다. 물론 스프링 빈에만 해당한다**

### 수정자 주입(setter 주입)
- **선택/변경** 가능성이 있을 때

```java
@Component
    public class OrderServiceImpl implements OrderService {
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
}
```
- 주입할 대상이 없어도 동작하게 하려면 `@Autowired(required=false)`로 지정

#### 자바빈 프로퍼티 규약
- 필드 값을 직접 변경 하지 않고 getter, setter 메서드를 만들어서 수정하는 규칙

### 필드 주입
- 필드에 주입하는 방법
- 간단하지만 안티패턴
- 외부 변경이 불가능 > 테스트 하기 힘듦
- 사용하지 말자
- 사용 가능한 곳 : 테스트 코드, 스프링 설정을 목적으로 하는 @Configuration 같은 곳

### 일반 메서드 주입
- 한번에 여러 필드를 주입 받을 수 있다
- 일반적으로 잘 사용하지 않음
- 의존관계 자동 주입은 스프링 컨테이너가 관리하는 스프링 빈이어야만 동작하는 점 주의

## 옵션 처리

- @Autowired(required=false) : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출 안됨
- org.springframework.lang.@Nullable : 자동 주입할 대상이 없으면 null이 입력된다.
- Optional<> : 자동 주입할 대상이 없으면 Optional.empty 가 입력된다.

```java
package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {

    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }

    static class TestBean {

        @Autowired(required = false)
        public void setNoBean1(Member noBean1) {
            System.out.println("noBean1 = " + noBean1);
            // 아예 호출이 안됨
       }

        @Autowired
        public void setNoBean2(@Nullable Member noBean2) {
            System.out.println("noBean2 = " + noBean2);
            // noBean2 = null
        }

        @Autowired
        public void setNoBean3(Optional<Member> noBean3) {
            System.out.println("noBean3 = " + noBean3);
            // noBean3 = Optional.empty 
        }
    }
}
```

## 생성자 주입을 선택해야 하는 이유

### 불변
- 대부분의 의존관계 주입은 한번 세팅되면 변경할 일이 없음
- 수정자 주입을 하려면 setter를 열어두어야 해서 누군가 실수로 변경할 수도 있음

### 누락
- 프레임워크 없이 순수한 자바 코드를 단위 테스트 하는 경우에 누락을 막을 수 있음

```java
package hello.core.order;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

public class OrderServiceImplTest {

    @Test
    void createOrder() {
        MemoryMemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "name", Grade.VIP));

        OrderServiceImpl orderService = new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
        Order order = orderService.createOrder(1L,"itemA",10000);

        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
}
```
- 생성자로 해야 한번에 찾아낼 수 있음
- final을 넣어줄 수 있음 (only 생성자 주입 방식만!)
- 프레임워크에 의존하지 않고 순수한 자바 언어의 특징을 잘 살리는 방법
- 기본적으로 생성자 주입을 사용하고, 필수값이 아닌 경우에는 수정자 주입방식을 옵션으로 부여 (동시 사용 가능)

## 롬복과 최신 트렌드

### 롬복이란..

```java
package hello.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.html.HTMLLegendElement;

@Getter
@Setter
@ToString
public class HelloLombok {

    private String name;
    private int age;

    public static void main(String[] args) {

        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("name");

        System.out.println("toString = " + helloLombok.toString());

        String name = helloLombok.getName();
        System.out.println("name = " + name);

    }
}
```

```java
package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// final 필드를 파라미터로 받는 생성자를 만들어줌
public class OrderServiceImpl implements OrderService{

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    // 문제점: 할인 정책을 변경하려면 클라이언트인 OrderServiceImpl의 코드를 이런식으로 고쳐야 한다

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

//    private MemberRepository memberRepository;
//    private DiscountPolicy discountPolicy;

    // final 있으면 무조건 생성자 있어야 함

//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
//
//    @Autowired
//    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
//        this.discountPolicy = discountPolicy;
//    }

//    @Autowired
//    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
//        System.out.println("memberRepository = " + memberRepository);
//        System.out.println("discountPolicy = " + discountPolicy);
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);
        // 단일 체계 원칙을 잘 지키고 설계된 것
        // 구현체는 Discount에 부 구조를 몰라도 실행 가능
        // Discount 로직이 바뀌면 여기가 아니라 DiscountPolicy 부분만 바꾸면 됨

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
```

- 최근에는 생성자를 딱 1개 두고, @Autowired 를 생략하는 방법을 주로 사용한다. 여기에 Lombok 라이브러리의 @RequiredArgsConstructor 함께 사용하면 기능은 다 제공하면서, 코드는 깔끔하게 사용할 수 있다.

## 조회할 빈이 2개 이상일 때의 문제점

FixDiscountPolicy에도 @Component를 붙여주고 Test를 돌려보면
`NoUniqueBeanDefinitionException: No qualifying bean of type 'hello.core.discount.DiscountPolicy' available: expected single matching bean but found 2: fixDiscountPolicy,rateDiscountPolicy`
오류가 발생한다

>이때 하위 타입으로 지정할 수 도 있지만, 하위 타입으로 지정하는 것은 DIP를 위배하고 유연성이 떨어진다. 그리고 이름만 다르고, 완전히 똑같은 타입의 스프링 빈이 2개 있을 때 해결이 안된다.스프링 빈을 수동 등록해서 문제를 해결해도 되지만, 의존 관계 자동 주입에서 해결하는 여러 방법이 있다.

## @Autowired 필드명 / @Qualifier / @Primary

조회 대상 빈이 2개 이상일 때 해결 방법
- @Autowired 필드 명 매칭
- @Qualifier @Qualifier끼리 매칭 > 빈 이름 매칭
- @Primary 사용

### @Autowired 필드명 매칭
타입 매칭을 시도하고, 이때 여러 빈이 있으면 필드 이름, 파라미터 이름으로 빈 이름을 추가
매칭한다.

```java
@Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy rateDiscountPolicy) {
        System.out.println("memberRepository = " + memberRepository);
        System.out.println("discountPolicy = " + rateDiscountPolicy);
        this.memberRepository = memberRepository;
        this.discountPolicy = rateDiscountPolicy;
    }
```

1. 타입 매칭
2. 타입 매칭의 결과가 2개 이상일 때 필드 명, 파라미터 명으로 빈 이름 매칭

### @Qualifier 사용
추가 구분자를 붙여주는 방법이다. 주입시 추가적인 방법을 제공하는 것이지 빈 이름을 변경하는 것은 아니다.

빈 등록시 @Qualifier를 붙여준다 (@Bean과도 사용 가능)

```java
package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy{

    private  int discountFixAmount = 1000; // 1000원 할인

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
```

```java
package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy{

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
}
```

주입 시에 @Qualifier를 붙여주고 등록한 이름을 적어준다
```java
@Autowired
    public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
        System.out.println("memberRepository = " + memberRepository);
        System.out.println("discountPolicy = " + discountPolicy);
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
```

1. @Qualifier끼리 매칭
2. 빈 이름 매칭
3. NoSuchBeanDefinitionException 예외 발생

### @Primary 사용
**은근 편하고 자주 사용**
우선순위를 정하는 방법이다. @Autowired 시에 여러 빈이 매칭되면 @Primary 가 우선권을 가진다.

#### @Primary와 @Qualifier
- @Qualifier는 모든 코드에 @Qualifier를 써줘야 하지만 @Primary는 그럴 필요가 없음
- 메인 데이터베이스 커넥션: @Primary 적용 > 편리하게 조회
- 서브 데이터베이스 커넥션: @Qualifier > 명시적으로 획득
- 우선순위: @Qualifier가 우선권 (@Primary는 기본값처럼 동작, @Qualifier는 매우 상세하게 동작하기 때문에 무조건 상세한 것에 우선순위가 있어서)

## Annotation 직접 만들기
`@Qualifier("mainDiscountPolicy")` 
이렇게 문자를 적으면 컴파일시 타입 체크가 안된다
다음과 같은 애노테이션을 만들어서 문제를 해결할 수 있다

```java
package hello.core.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {

}
```
- 이렇게 애노테이션을 만들어주고

```java
package hello.core.discount;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy{

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
}
```
이렇게 바꿔주면 됨

- @Primary로 해결 되면 그걸 쓰고, 안될 때 쓰기

>애노테이션에는 상속이라는 개념이 없다. 이렇게 여러 애노테이션을 모아서 사용하는 기능은 스프링이 지원해주는 기능이다. @Qulifier 뿐만 아니라 다른 애노테이션들도 함께 조합해서 사용할 수 있다. 단적으로 @Autowired도 재정의 할 수 있다. 물론 스프링이 제공하는 기능을 뚜렷한 목적 없이 무분별하게 재정의 하는 것은 유지보수에 더 혼란만 가중할 수 있다.

## 조회한 빈이 모두 필요할 때 (List/Map)

```java
package hello.core.autowired;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class AllBeanTest {

    @Test
    void findAllBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);
        int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);

        int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");
        assertThat(rateDiscountPrice).isEqualTo(2000);
    }

    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;

        @Autowired
        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
            /*
            policyMap = {fixDiscountPolicy=hello.core.discount.FixDiscountPolicy@7a220c9a, rateDiscountPolicy=hello.core.discount.RateDiscountPolicy@2421cc4}
            policies = [hello.core.discount.FixDiscountPolicy@7a220c9a, hello.core.discount.RateDiscountPolicy@2421cc4]
             */
        }

        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            return discountPolicy.discount(member,price);
        }
    }
}
```

- DiscountService는 Map으로 모든 DiscountPolicy 를 주입받는다. 이때 fixDiscountPolicy , rateDiscountPolicy 가 주입된다.
- discount () 메서드는 discountCode로 "fixDiscountPolicy"가 넘어오면 map에서 fixDiscountPolicy 스프링 빈을 찾아서 실행한다. 물론 “rateDiscountPolicy”가 넘어오면 rateDiscountPolicy 스프링 빈을 찾아서 실행한다.

## 자동, 수동의 올바른 실무 운영 기준

**스프링이 나오고 시간이 갈 수록 점점 자동을 선호하는 추세다.**

개발자 입장에서 스프링 빈을 하나 등록할 때 @Component 만 넣어주면 끝나는 일을 @Configuration 설정 정보에 가서 @Bean 을 적고, 객체를 생성하고, 주입할 대상을 일일이 적어주는 과정은 상당히 번거롭다.
또 관리할 빈이 많아서 설정 정보가 커지면 설정 정보를 관리하는 것 자체가 부담이 된다. 그리고 **결정적으로 자동 빈 등록을 사용해도 OCP, DIP를 지킬 수 있다.**

### 수동 빈 등록 사용 객체

애플리케이션은 크게 업무 로직과 기술 지원 로직으로 나눌 수 있다.
- 업무 로직 빈: 웹을 지원하는 컨트롤러, 핵심 비즈니스 로직이 있는 서비스, 데이터 계층의 로직을 처리하는 리포지토리등이 모두 업무 로직이다. 보통 비즈니스 요구사항을 개발할 때 추가되거나 변경된다.
- 기술 지원 빈: 기술적인 문제나 공통 관심사(AOP)를 처리할 때 주로 사용된다. 데이터베이스 연결이나, 공통 로그 처리 처럼 업무 로직을 지원하기 위한 하부 기술이나 공통 기술들이다.
- 업무 로직은 유사한 패턴이 있어서 자동 기능을 적극 사용하여 문제가 어디서 발생했는지 명확하게 파악 가능
- 기술 지원 로직들은 수동 빈 등록 사용을 해서 명확하게 드러내는 것이 좋다
- 수동 빈으로 등록하거나 또는 자동으로하면 특정 패키지에 같이 묶어두는게 좋다
```java 
@Configuration
  public class DiscountPolicyConfig {
      @Bean
      public DiscountPolicy rateDiscountPolicy() {
          return new RateDiscountPolicy();
      }
      @Bean
      public DiscountPolicy fixDiscountPolicy() {
          return new FixDiscountPolicy();
      }}
```
>편리한 자동 기능을 기본으로 사용하자    
>직접 등록하는 기술 지원 객체는 수동 등록   
>다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고민해보자
