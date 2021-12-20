# Section 9 - Bean Scope

## 빈 스코프란?

빈이 존재할 수 있는 범위

### 지원 스코프 종류
- 싱글톤: 기본 스코프, 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프
- 프로토타입: 빈의 생성과 의존관계 주입까지만 관여하고 더는 관리하지 않는 매우 짧은 범위의 스코프
- 웹 관련 스코프
  - request: 웹 요청이 들어오고 나갈때까지 유지
  - session: 웹 세션이 생성되고 종류될 때까지 유지되는 스코프
  - application: 웹의 서블릿 컨텍스와 같은 범위로 유지되는 스코프

## 프로토타입 스코프
스프링은 일반적으로 싱글톤 빈을 사용하므로, 싱글톤 빈이 프로토타입 빈을 사용하게 된다. 그런데 싱글톤 빈은 생성 시점에만 의존관계 주입을 받기 때문에, 프로토타입 빈이 새로 생성되기는 하지만, 싱글톤 빈과 함께 계속 유지되는 것이 문제다.

```java
package hello.core.scope;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.assertj.core.api.Assertions.*;

public class SingletonWithPrototypeTest1 {
    
    @Test
    void prototypeFind() {

        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(2);
    }

    @Scope("singleton")
    @RequiredArgsConstructor
    static class ClientBean {
//        private final PrototypeBean prototypeBean; // 생성 시점에 주입
        private ObjectProvider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }
    
    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;


        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy " + this);
        }
    }
    
}
```

### ObjectFactory, ObjectProvider
- 의존관계를 외부에서 주입받는 것이 아니라 직접 필요한 의존관계를 찾는 것을 **Dependency Lookup(DL) - 의존관계 조회** 라고 함
- 이렇게 하면 전체를 ApplicationContext 전체를 주입받게 되므로 스프링 컨테이너 종속이 되고, 단위 테스트 어려워짐
- 우리는 지정한 Prototype Bean을 Container 대신에 찾아주는 DL 정도 기능만 필요함
- 이런 것을 해주는 것이 **Object Provider**
- 과거: ObjectFactory 현재: ObjectProvider
- `prototypeBeanProvider.getObject()`를 통해서 항상 새로운 프로토타입 빈이 생성
- `ObjectProvider.getObject`를 해주면 스프링 컨테이너를 통해 해당 빈을 찾아서 반환(DL)
- mock 코드를 만들기 더 쉬워짐!

> ObjectFactory: 기능이 단순, 별도의 라이브러리 필요 없음, 스프링에 의존
> ObjectProvider: ObjectFactory 상속, 옵션, 스트림 처리등 편의 기능이 많고, 별도의 라이브러리 필요 없음, 스프링에 의존

### JSR-330 Provider
- 단점:  javax.inject:javax.inject:1 라이브러리를 gradle에 추가해야 한다.

```java

package javax.inject;

/**
 * Provides instances of {@code T}. Typically implemented by an injector. For
 * any type {@code T} that can be injected, you can also inject
 * {@code Provider<T>}. Compared to injecting {@code T} directly, injecting
 * {@code Provider<T>} enables:
 *
 * <ul>
 *   <li>retrieving multiple instances.</li>
 *   <li>lazy or optional retrieval of an instance.</li>
 *   <li>breaking circular dependencies.</li>
 *   <li>abstracting scope so you can look up an instance in a smaller scope
 *      from an instance in a containing scope.</li>
 * </ul>
 *
 * <p>For example:
 *
 * <pre>
 *   class Car {
 *     &#064;Inject Car(Provider&lt;Seat> seatProvider) {
 *       Seat driver = seatProvider.get();
 *       Seat passenger = seatProvider.get();
 *       ...
 *     }
 *   }</pre>
 */
public interface Provider<T> {

    /**
     * Provides a fully-constructed and injected instance of {@code T}.
     *
     * @throws RuntimeException if the injector encounters an error while
     *  providing an instance. For example, if an injectable member on
     *  {@code T} throws an exception, the injector may wrap the exception
     *  and throw it to the caller of {@code get()}. Callers should not try
     *  to handle such exceptions as the behavior may vary across injector
     *  implementations and even different configurations of the same injector.
     */
    T get();
}
```

이런 라이브러리가 추가됨

```java
    @Scope("singleton")
    @RequiredArgsConstructor
    static class ClientBean {
//        private final PrototypeBean prototypeBean; // 생성 시점에 주입
//        private ObjectProvider<PrototypeBean> prototypeBeanProvider;
    private Provider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
//            PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
            PrototypeBean prototypeBean = prototypeBeanProvider.get();
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }
```
이렇게 코드를 바꿔줄 수 있음

`PrototypeBean.init hello.core.scope.SingletonWithPrototypeTest1$PrototypeBean@aba625`
`PrototypeBean.init hello.core.scope.SingletonWithPrototypeTest1$PrototypeBean@7c3fdb62`

똑같이 동작함

라이브러리 땡겨야 되는 거 빼고는 단순하고 심플해서 제일 좋음

#### 그러면 프로토타입은 언제 쓸까?
매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요하면 사용하면 된다. 그런데 실무에서 웹 애플리케이션을 개발해보면, 싱글톤 빈으로 대부분의 문제를 해결할 수 있기 때문에 프로토타입 빈을 직접적으로 사용하는 일은 매우 드물다.

#### 실무에서는 뭘 써야 하나?
실무에서 자바 표준인 JSR-330 Provider를 사용할 것인지, 아니면 스프링이 제공하는 ObjectProvider를 사용할 것인지 고민이 될 것이다. **ObjectProvider는 DL을 위한 편의 기능을 많이 제공해주고 스프링 외에 별도의 의존관계 추가가 필요 없기 때문에 편리하다**. 만약(정말 그럴일은 거의 없겠지만) 코드를 스프링이 아닌 다른 컨테이너에서도 사용할 수 있어야 한다면 JSR-330 Provider를 사용해야한다.

 스프링을 사용하다 보면 이 기능 뿐만 아니라 다른 기능들도 자바 표준과 스프링이 제공하는 기능이
겹칠때가 많이 있다. 대부분 스프링이 더 다양하고 편리한 기능을 제공해주기 때문에, **특별히 다른 컨테이너를 사용할 일이 없다면, 스프링이 제공하는 기능을 사용하면 된다**.

## 웹 스코프
- 웹 스코프는 웹 환경에서만 동작한다.
- 웹 스코프는 프로토타입과 다르게 스프링이 해당 스코프의 종료시점까지 관리한다. 따라서 종료 메서드가 호출된다.

### 종류
- request: HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프, 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고, 관리된다.
- session: HTTP Session과 동일한 생명주기를 가지는 스코프
- application: 서블릿 컨텍스트( ServletContext )와 동일한 생명주기를 가지는 스코프
- websocket: 웹 소켓과 동일한 생명주기를 가지는 스코프
