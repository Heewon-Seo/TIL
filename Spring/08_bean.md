# Section 8 - 빈 생명주기 콜백

## 빈 생명주기 콜백 시작

데이터베이스 커넥션 풀이나, 네트워크 소켓처럼 애플리케이션 시작 시점에 필요한 연결을 미리 해두고, 애플리케이션 종료 시점에 연결을 모두 종료하는 작업을 진행하려면, 객체의 초기화와 종료 작업이 필요하다.

### 가상의 커넥션 풀

```java
package hello.core.lifecycle;

public class NetworkClient {

    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
        connect();
        call("초기화 연결 메시지");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 서비스 시작시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void call(String message) {
        System.out.println("call: " + url  + " message= " + message);
    }

    // 서비스 종료 시 호출
    public void disconnect() {
        System.out.println("close: " + url);
    }

}
```
### 테스트 코드
```java
package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
        // 기존에 쓰던 ApplicationContext에서는 close를 지원해주지 않기 때문에 ConfigurableApplicationContext를 불러와야 한다
        // public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable

    }

    @Configuration
    static class LifeCycleConfig {
        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
}
```

스프링 빈은 간단하게 다음과 같은 라이프사이클을 가진다. 
**객체 생성 > 의존관계 주입**

- 초기화 작업은 의존관계 주입까지 끝나야 가능
- 이 시점을 알 기위해서 의존관계 주입이 완료 되면 스프링 빈에게 콜백 메서드를 통해서 초기화 시점을 알려주는 기능이 있음
- 또한 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 줌

>스프링 빈의 이벤트 라이프사이클
**스프링컨테이너생성 > 스프링빈생성 > 의존관계주입 > 초기화콜백 > 사용 > 소멸전콜백 > 스프링 종료**

#### 객체 생성(new)과 초기화는 분리!!
- 생성자: 필수 정보(파라미터)를 받고 메모리 할당, 객체 생성
- 초기화: 이렇게 생성된 값을 활용해서 외부 커넥션 연결 등 무거운 작업
- 생성자 안에서 무거운 초기화 작업을 같이 하는 것보다는 생성과 초기화를 나누는 것이 유지보수 관점에서 좋음
- 그러나 초기화 작업이 내부 값들만 약간 변경 하는 단순한 경우에는 생성자에서 한번에 다 처리

그래서 어떻게 하나요?
- 인터페이스(InitializingBean, DisposableBean)
- 설정 정보에 초기화 메서드, 종료 메서드 지정
- @PostConstruct, @PreDestroy 애노테이션 지원

## 인터페이스 Initializing Bean & DisposableBean

```java
package hello.core.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetworkClient implements InitializingBean, DisposableBean {

    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 서비스 시작시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void call(String message) {
        System.out.println("call: " + url  + " message= " + message);
    }

    // 서비스 종료 시 호출
    public void disconnect() {
        System.out.println("close: " + url);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 의존관계 주입이 끝나면 호출되는 것
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메시지");
    }

    @Override
    public void destroy() throws Exception {
        // Bean이 종료 될 때 호출되는 것
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
    
    /*
    생성자 호출, url = null
    NetworkClient.afterPropertiesSet
    connect: http://hello-spring.dev
    call: http://hello-spring.dev message= 초기화 연결 메시지
    01:29:03.898 [main] DEBUG org.springframework.context.annotation.AnnotationConfigApplicationContext - Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@2145b572, started on Thu Dec 16 01:29:03 KST 2021
    NetworkClient.destroy
    close: http://hello-spring.dev
     */
}
```

#### 이 인터페이스들의 단점
- 이 인터페이스는 스프링 전용 인터페이스다. 해당 코드가 스프링 전용 인터페이스에 의존한다.
- 초기화, 소멸 메서드의 이름을 변경할 수 없다.
- 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없다.

## 빈 등록 초기화 및 소멸 메서드

### NetworkClient
```java
public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }

    public void close() {
        System.out.println("NetworkClient.close");
        disconnect();
    }
```

### 테스트 코드
```java
@Configuration
    static class LifeCycleConfig {
        @Bean(initMethod = "init", destroyMethod = "close")
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
```
- 이런식으로 하면 커스텀 가능
- destroyMethod는 기본값이 추론(inferred)으로 되어있어서 메서드명이 `close` 혹은 `shutdown` 이면 따로 적어주지 않아도 잘 동작
- 추론 기능이 싫을 경우 `""` 공백 값을 주면 됨

## Annotation @PostConstruct & @PreDestroy

이걸 쓰시면 됩니다

### NetworkClient
```java
    @PostConstruct
    public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }

    @PreDestroy
    public void close() {
        System.out.println("NetworkClient.close");
        disconnect();
    }
```
- 이렇게 애노테이션만 붙여주면 됨
- 패키지가 javax 이므로 스프링 종속이 아니라 자바에서 정식으로 지원하는 기능
- 유일한 단점은 외부 라이브러리에는 적용하지 못한다는 것이다.
- 외부 라이브러리를 초기화, 종료 해야 하면 *@Bean의 기능(2번째꺼)* 을 사용하자
