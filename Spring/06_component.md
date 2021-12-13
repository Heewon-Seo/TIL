# Section 6 - 컴포넌트 스캔

## 컴포넌트 스캔 & 의존관계 자동 주입 시작하기

- 구현체에다가 @Component 붙여주기
- @Autowired : 생성자에 붙여주면 MemberRepository에 맞는 애들을 찾아와서 의존성 주입을 자동으로 해준다

>참고: @Configuration 이 컴포넌트 스캔의 대상이 된 이유도 @Configuration 소스코드를 열어보면 @Component 애노테이션이 붙어있기 때문이다.

### 1. @ComponentScan
![Screenshot_2021-12-14 02 36 53_RvZiTC](https://user-images.githubusercontent.com/87118337/145861165-f33112df-a880-4528-a18b-c0e48c142b98.png)

- `@ComponentScan`은 `@Component`가 붙은 모든 클래스를 스프링 빈으로 등록
- 스프링 빈 기본 이름: 클래스명(맨 앞글자 소문자)

### 2. @Autowired 의존관계 자동 주입
![Screenshot_2021-12-14 02 39 34_fVyD8R](https://user-images.githubusercontent.com/87118337/145861397-9eed3a4c-1488-41e3-a425-e6496a1ca8de.png)

- 생성자에 지정하면, 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입
- 타입이 같은 빈을 찾아서 주입
- getBean(MemberRepository.class)와 동일
- 생성자에 파라미터가 많아도 다 찾아서 자동 주입 가능

## 탐색 위치와 기본 스캔 대상

```java
package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "hello.core",
        // 요 위치에서부터 찾아서 들어가는 것임
        basePackageClasses =AutoAppConfig.class,
        // 여기서부터 찾는 것
        // 지정하지 않으면? -> ComponentScan을 붙인 클래스의 패키지 부터 하위 패키지를 검색
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
// AppConfig 자동 스캔 안되도록 설정
public class AutoAppConfig {
}
```
### 권장하는 방법
개인적으로 즐겨 사용하는 방법은 패키지 위치를 지정하지 않고,
설정 정보 클래스의 위치를 프로젝트 최상단에 두는 것이다.
최근 스프링 부트도 이 방법을 기본으로 제공한다.

>참고로 스프링 부트를 사용하면 스프링 부트의 대표 시작 정보인 @SpringBootApplication 를 이 프로젝트 시작 루트 위치에 두는 것이 관례이다. (그리고 이 설정안에 바로 @ComponentScan 이 들어있다!)

### 컴포넌트 스캔 기본 대상

- @Component : 컴포넌트 스캔에서 사용
- @Controlller : 스프링 MVC 컨트롤러에서 사용 - 스프링 MVC 컨트롤러로 인식
- @Service : 스프링 비즈니스 로직에서 사용 - 개발자들이 핵심 비즈니스 로직이 여기에 있겠구나 라고 인식하는데 도움이 됨
- @Repository : 스프링 데이터 접근 계층에서 사용 - 스프링 데이터 접근 계층으로 인식하고 데이터 계층의 예외를 스프링 예외로 변환해줌
- @Configuration : 스프링 설정 정보에서 사용 - 스프링 설정 정보로 인식, 스프링 빈이 싱글톤을 유지하도록 추가 처리 해줌

> 참고: useDefaultFilters 옵션은 기본으로 켜져있는데, 이 옵션을 끄면 기본 스캔 대상들이 제외된다. 그냥 이런 옵션이 있구나 정도 알고 넘어가자.

## 필터

- includeFilters : 컴포넌트 스캔 대상을 추가로 지정한다.
- excludeFilters : 컴포넌트 스캔에서 제외할 대상을 지정한다.

```java
package hello.core.scan.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.context.annotation.ComponentScan.*;

public class ComponentFilterAppConfigTest {

    @Test
    void filterScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class);
        assertThat(beanA).isNotNull();

//        BeanB beanB = ac.getBean("beanB", BeanB.class);
        assertThrows(NoSuchBeanDefinitionException.class, (() -> ac.getBean("beanB",BeanB.class)));
    }

    @Configuration
    @ComponentScan(
            includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
            excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class))
    static class ComponentFilterAppConfig {

    }
}
```

### FilterType 옵션
- ANNOTATION: 기본값, 애노테이션을 인식해서 동작한다. ex) org.example.SomeAnnotation
- ASSIGNABLE_TYPE: 지정한 타입과 자식 타입을 인식해서 동작한다. ex) org.example.SomeClass
- ASPECTJ: AspectJ 패턴 사용 ex) org.example..*Service+
- REGEX: 정규 표현식 ex) org\.example\.Default.*
- CUSTOM: TypeFilter 이라는 인터페이스를 구현해서 처리 ex) org.example.MyTypeFilter

>참고: @Component 면 충분하기 때문에, includeFilters 를 사용할 일은 거의 없다. excludeFilters 는 여러가지 이유로 간혹 사용할 때가 있지만 많지는 않다.
> 특히 최근 스프링 부트는 컴포넌트 스캔을 기본으로 제공하는데, 개인적으로는 옵션을 변경하면서 사용하기 보다는 스프링의 기본 설정에 최대한 맞추어 사용하는 것을 권장하고, 선호하는 편이다.

## 중복 등록과 충돌

- 수동 빈 > 자동 빈 
- 최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 기본 값을 바꾸었다.
- 에러 내용
` Consider renaming one of the beans or enabling overriding by setting
spring.main.allow-bean-definition-overriding=true`
- 원래 스프링에서는 allow-bean-definition-overriding이 false인데 스프링 부트에서는 true가 기본값
- 왜 이렇게 했을까? > 명확하지 않은 것은 하지 않는게 좋기 때문에 (애매한 상황을 만들지 말아라)
