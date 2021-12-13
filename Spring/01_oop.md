# 스프링 핵심 원리 - 기본편

## 스프링부트란?

스프링부트는 스프링 프레임워크 및 여러 확장 기능을 잘 쓸 수 있도록 연결해 주는 툴 > 즉, 스프링 프레임워크 없이는 스프링 부트를 쓸 수 없다!

## 좋은 객체 지향 프로그래밍이란?

객체 지향 프로그래밍은 유연하고 변경이 용이하다
> 레고 블럭 조립하듯이, 키보드, 마우스 갈아끼우듯이, 컴포넌트를 쉽고 유연하게 갈아끼우면서 할 수 있는 개발 방법 (=다형성!)

### 다형성

내가 자동차 종류를 바꿔도 운전자는 여전히 운전이 가능하다
왜냐면 자동차 역할에 대해서만 의존하고 있기 때문에 운전자(클라이언트)는
내부적으로 자동차 구조를 몰라도 운전자는 그대로 운전을 할 수 있다

자동차 역할만 구현하면 대상을 바꾸지 않고 (클라이언트에 영향을 주지 않고)
새로운 것을 배울 필요 없이 운전이 가능하다

### 역할과 구현을 분리

- 역할 = 인터페이스
- 구현 = 인터페이스를 구현한 클래스 (구현 객체)

객체 설계 시 역할(인터페이스)을 먼저 부여하고 구현

### 자바 언어의 다형성 (Overriding)

다형성으로 인터페이스를 구현한 객체를 실행 시점에 유연하게 변경할 수 있음
클라이언트에 영향을 주지 않고 변경 가능
**인터페이스를 안정적으로 잘 설계하는 것이 진짜 중요**

### 왜 스프링 강의인데 자꾸 객체 지향?

스프링은 다형성을 극대화해서 이용할 수 있게 도와준다
스프링을 사용하면 구현을 편리하게 변경할 수 있다

## 좋은 객체 지향 설계의 5가지 원칙 (SOLID)

클린코드로 유명한 로버트 마틴이 좋은 객체 지향 설계의 5가지 원칙을 정리

### SRP(Single Responsibility Principle) - 단일 책임 원칙

- 한 클래스는 하나의 책임만 가져야 한다
- 중요한 기준은 변경 - 변경이 있을 때 파급 효과가 적으면 단일 책임 원칙을 잘 따른 것임

### OCP(Open/Closed Principle) - 개방-폐쇄 원칙 (중요!)

- 확장에는 열려있으나 변경에는 닫혀있게

### LSP(Liskov Substitution Principle) - 리스코프 치환 원칙

- 인터페이스 규약을 무조건 맞춰야 한다
- 하위 클래스는 인터페이스 규약을 다 지켜야 한다

### ISP(Interface Segregation Principle) - 인터페이스 분리 원칙

- 특정 클라이언트를 위한 인터페이스 여러개가 범용 인터페이스 하나보다 나음
- 덩어리가 크면 구현하기 어려움 / 반대로 작으면 구현하기 쉬움
  
### DIP(Dependency Inversion Principle) - 의존 관계 역전 원칙

- 추상화에 의존해야지 구체화에 의존하면 안된다
- 구현 클래스에 의존하지 말고 인터페이스에 의존하라는 뜻
- 클라이언트가 구현 클래스를 직접 선택하면 DIP를 위반한 것!!

### 정리

- 객체 지향의 핵심은 다형성
- 다형성만으로는 OCP, DIP를 지킬 수 없다
