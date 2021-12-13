# Section 5 - 싱글톤 컨테이너

- 스프링 없는 순수 DI 컨테이터인 AppConfig는 요청을 할 때마다 객체를 새로 생성
- 메모리 낭비가 너무 심함
- 해결방안: 해당 객체가 딱 1개 생성 후 이걸 공유하도록 하면 됨

```java
package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SingletonTest {

    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();

        // 1. 조회: 호출할 때마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();

        // 2. 조회: 호출할 때마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();

        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);
        /*
        memberService1 = hello.core.member.MemberServiceImpl@351d0846
        memberService2 = hello.core.member.MemberServiceImpl@77e4c80f

        -> 이렇게 메모리에 계속 생성이 되어서 객체가 올라감
         */

        // memberService1 != memberService2
        Assertions.assertThat(memberService1).isNotSameAs(memberService2);
    }
}
```

```java
package hello.core.singleton;

public class SingletonService {

    private static final SingletonService instance = new SingletonService();
    // 자기 자신을 내부에 private static으로 가지고 있음
    // 이해 안가면 static 자바 기본 공부해 볼 것
    // 바로 생성해서 여기 안에만 들어가 있음

    public static SingletonService getInstance() {
        return instance;
    } // 이걸로만 조회 가능 (그리고 항상 같은 인스턴스를 반환)

    // 외부에서 new 키워드를 사용한 객체 생성을 못하게 막는다 (private 생성자)
    private SingletonService() {

    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }

    // 이렇게 해두면 아무데서도 new 해서 생성할 수 없음

//    public static void main(String[] args) {
//        SingletonService singletonService = new SingletonService();
//        // 이렇게 계속 만들수 있기 때문에
//    }
}
```

### 싱글톤 패턴의 문제점
- 코드 자체가 많이 들어감
- 클라이언트가 구체 클래스에 의존해야 함 > DIP 위반
- OCP 원칙도 위반할 가능성이 높음

**스프링 프레임워크는 이 단점들을 모조리 보완해줌**

## 싱글톤 컨테이너
스프링 컨테이너는 싱글톤 패턴을 적용하지 않아도 객체 인스턴스를 싱글톤으로 관리해줌

```java
    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer() {
//        AppConfig appConfig = new AppConfig();

        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        // 1. 조회: 호출할 때마다 객체를 생성
        MemberService memberService1 = ac.getBean("memberService",MemberService.class);

        // 2. 조회: 호출할 때마다 객체를 생성
        MemberService memberService2 = ac.getBean("memberService",MemberService.class);

        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        // memberService1 != memberService2
        assertThat(memberService1).isSameAs(memberService2);
        /*
        memberService1 = hello.core.member.MemberServiceImpl@63eef88a
        memberService2 = hello.core.member.MemberServiceImpl@63eef88a

        -> 똑같은 객체
         */
    }
```

![Screenshot_2021-12-13 22 34 05_KYfMCJ](https://user-images.githubusercontent.com/87118337/145822030-de5c97d3-0016-4651-86ad-d24ac87e6347.png)

스프링 컨테이너 덕분에 고객의 요청이 올 때마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유해서 효율적으로 사용할 수 있다

## 싱글톤 방식의 주의점

### 예제

```java
package hello.core.singleton;

public class StatefulService {

    private int price; // 상태를 유지하는 필드

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price; // 여기가 문제!
    }

    public int getPrice() {
        return price;
    }
}
```

```java
package hello.core.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        // ThreadA: A 사용자 10000원 주문
        statefulService1.order("userA", 10000);
        // ThreadB: B 사용자 20000원 주문
        statefulService1.order("userB", 20000);

        // ThreadA: 사용자A 주문 금액 조회
        int price = statefulService1.getPrice();

        // A가 주문하고 기다리는 사이에 B가 와서 주문한 상황
        System.out.println("price = " + price);
        /*
        name = userA price = 10000
        name = userB price = 20000
        price = 20000
         */

        Assertions.assertThat(statefulService1.getPrice()).isEqualTo(20000);
        // 20000원으로 아예 바껴버림 > 망함



    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}
```
- SatatefulService의 price 필드는 공유되는 필드인데, 특정 클라이언트가 값을 변경해버리는 것이 문제점
- 공유필드는 항상 조심 > 스프링 빈은 항상 무상태(stateless)로 설계하자!!

#### 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.

```java
package hello.core.singleton;

public class StatefulService {

//    private int price; // 상태를 유지하는 필드

    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
//        this.price = price; // 여기가 문제!
        return price;
    }

//    public int getPrice() {
//        return price;
//    }
}
```

이런식으로 지역변수화 시켜주면 해결됨

## @Configuration & Singleton

- memberService 빈을 만드는 코드를 보면 memberRepository() 를 호출한다. 
- 이 메서드를 호출하면 new MemoryMemberRepository() 를 호출한다. 
- orderService 빈을 만드는 코드도 동일하게 memberRepository() 를 호출한다.
- 이 메서드를 호출하면 new MemoryMemberRepository() 를 호출한다.

결과적으로 각각 다른 2개의 MemoryMemberRepository 가 생성되면서 싱글톤이 깨지는 것 처럼 보인다.
스프링 컨테이너는 이 문제를 어떻게 해결할까?

```java
package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationSingletonTest {

    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);

        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = memberService.getMemberRepository();

        System.out.println("memberService -> memberRepository = " + memberRepository1);
        System.out.println("orderService -> memberRepository = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        /*
        memberService -> memberRepository = hello.core.member.MemoryMemberRepository@63eef88a
        orderService -> memberRepository = hello.core.member.MemoryMemberRepository@63eef88a
        memberRepository = hello.core.member.MemoryMemberRepository@63eef88a

        다 같은 것
        -> 왜?
         */

        assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);


    }
}
```

## @Configuration & 바이트 코드 조작의 마법

순수한 클래스라면 다음과 같이 출력되어야 한다.   
`class hello.core.AppConfig`
그런데 예상과는 다르게 클래스 명에 xxxCGLIB가 붙으면서 상당히 복잡해진 것을 볼 수 있다.   
이것은 내가 만든 클래스가 아니라 스프링이 CGLIB라는 바이트코드 조작 라이브러리를 사용해서.  
AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고,   
그 다른 클래스를 스프링 빈으로 등록한 것이다!   

@Configuration을 빼면 이렇게 안되고 싱글톤이 깨짐
-> 항상 @Configuration을 사용하도록 하자




