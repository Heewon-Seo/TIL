# Section 1 - 웹 애플리케이션의 이해

![image](https://user-images.githubusercontent.com/87118337/146912924-155a1685-d304-4810-9ffa-31d99c1852e5.png)

## 웹 서버
- HTTP 기반으로 동작
- 정적 리소스 제공 (html, css, js, image)
- 예) APACHE

## 웹 애플리케이션 서버(WAS)
- HTTP 기반으로 동작
- 웹서버 기능 포함
- 동적 HTML, HTTP API(JSON)
- 서블릿, JSP, 스프링 MVC
- 예) 톰캣

## 둘의 차이는?
- 웹서버는 정적 리소스(파일), WAS는 애플리케이션 로직을 실행하는 서버
- 자바는 서블릿 컨테이너 기능을 제공하면 WAS
- WAS는 애플리케이션 코드를 실행하는데 더 특화됨

## 웹 시스템 구성
- 최소 WAS(정적 리소스, 애플리케이션 로직 모두 제공) & DB
- WAS가 너무 많은 역할을 담당해서 서버 과부하 우려
- 가장 비싼 애플리케이션 로직이 정적 리소스 때문에 수행이 어려울 수 있음
- WAS 장애시 오류 화면도 노출 불가

### WEB, WAS, DB
- 정적 리소스는 웹서버가 처리
- 웹 서버는 애플리케이션 로직 같은 동적인 처리가 필요하면 WAS에 요청을 위임
- WAS는 중요한 애플리케이션 로직만 처리
- 장점: 
  - 정적 리소스만 제공하는 웹 서버는 잘 안죽음
  - 애플리케이션 로직이 동작하는 WAS 서버는 잘 죽음
  - WAS, DB 장애시 WEB 서버가 오류 화면 제공 가능

## 서블릿

- HTTP 요청 정보를 편리하게 사용할 수 있는 HttpServletRequest
- HTTP 응답 정보를 편리하게 제공할 수 있는 HttpServletResponse
- 개발자가 HTTP 스펙을 서블릿 덕분에 매우 편리하게 사용할 수 있다

![image](https://user-images.githubusercontent.com/87118337/146914865-64dd8cd6-ee48-441f-97f8-62d265a09b41.png)

### 서블릿 컨테이너
- 톰캣처럼 서블릿을 지원하는 WAS를 서블릿 컨테이너라고 함
- 서블릿 객체를 생성, 초기화, 호출, 종료하는 생명주기 관리
- 서블릿 객체는 싱글톤으로 관리됨
  - 요청이 올 때마다 계속 객체 생성하는 것은 비효율
  - 최초 로딩 > 객체를 미리 만들어두고 재활용
  - 모든 고객 요청은 동일한 객체 인스턴스에 접근
  - **공유 변수 사용 주의**
  - 컨테이너 종료시 같이 종료됨
- JSP도 서블릿으로 변환 되어서 사용됨
- **동시 요청을 위한 멀티 쓰레드 처리 지원**

## 동시 요청 - 멀티 쓰레드
- 애플리케이션 코드를 하나하나 순차적으로 실행하는 것이 쓰레드
- 메인 메서드를 처음 실행하면 main이라는 이름의 쓰레드가 실행됨
- 쓰레드는 한번에 하나만 코드 라인만 수행
- 동시 처리가 필요하면 쓰레드를 추가로 생성

![image](https://user-images.githubusercontent.com/87118337/146921434-d09ec0ac-8e87-41cb-a44b-3276e98c4a49.png)
- 이렇게 되면 문제가 둘다 죽음

![image](https://user-images.githubusercontent.com/87118337/146921488-63894c11-a7db-4ab5-8aa7-c7eb437ecc2e.png)
- 이렇게 요청마다 쓰레드 생성을 해서 해결
- 문제점:
  - 쓰레드 생성 비용이 매우 비쌈
  - 쓰레드 생성하면 응답 속도가 늦어짐
  - 컨텍스트 스위칭 비용이 발생
  - 요청이 너무 많이 오면 CPU, 메모리 임계점을 넘어서 서버가 죽을 수 있음

![image](https://user-images.githubusercontent.com/87118337/146921859-819b61e8-de9a-4ef1-8106-6ef46252cc49.png)
![image](https://user-images.githubusercontent.com/87118337/146921821-b79f0b51-f403-4056-9043-db86844b13b8.png)
- 이렇게 하면 요청마다 쓰레드 생성을 하는 것의 단점을 보완함
- 필요한 쓰레드를 쓰레드 풀에 보관, 관리
- 생성 가능 쓰레드의 최대치 관리 (톰캣: 200개 기본)
- 쓰레드가 필요하면 이미 생성된 쓰레드를 풀에서 꺼내서 사용
- 사용 종료 후 풀에 반납
- 모두 사용중이면 요청을 거절 or 특정 숫자만큼만 대기하도록 설정
- 장점:
  - 미리 생성된 쓰레드로 쓰레드의 생성 종료 비용(CPU) 절약, 응담 시간 빠름
  - 생성 가능 쓰레드의 최대치 존재 > 너무 많은 요청이 들어와도 기존 요청 안전하게 처리 가능

### 최대치를 너무 낮게 설정했을 경우

![image](https://user-images.githubusercontent.com/87118337/146922354-81a0e4c5-38f9-45df-933f-f7d4ce5f17c9.png)
- 동시 요청이 많을 시, 리소스는 여유롭지만 클라이언트는 금방 응답 지연됨

### 너무 높게 설정했을 경우
- 동시 요청이 많을 시, 서버 다운

### 적정 숫자는 어떻게 찾나?
- 어플리케이션 로직의 복잡도, CPU, 메모리, IO 리소스 상황에 따라 모두 다르다
- 최대한 실제 서비스와 유사하게 성능 테스트를 시도해봐야 한다 (아파치 ab, 제이미터, nGrinder 사용)

### WAS의 멀티 쓰레드 지원
- 멀티 쓰레드는 WAS가 알아서 처리
- 개발자가 코드를 신경쓰지 않아도 된다
- 멀티 쓰레드 환경이므로 싱글톤 객체(서블릿, 스프링 빈)는 주의해서 사용해야 함


## HTML, HTTP API, CSR, SSR

### HTTP API
- HTML이 아니라 데이터 전달
- 주로 JSON 형식 사용
- 다양한 시스템에서 호출
- 데이터만 주고 받음 (UI 화면이 필요하면 클라이언트가 별도로 처리)
- 앱, 웹 클라이언트, 서버 to 서버

![image](https://user-images.githubusercontent.com/87118337/146923281-09bde107-9c4e-4b49-8be1-eb9cfff27963.png)

- UI 클라이언트 접점
  - 앱 클라이언트(아이폰, 안드로이드, PC 앱)
  - 웹 브라우저에서 자바스크립트를 통한 HTTP API 호출
  - React, Vue.js 같은 웹 클라이언트
- Server to Server
  - 주문 서버 > 결제 서버
  - 기업 간 데이터 통신

### SSR(서버 사이드 렌더링), CSR(클라이언트 사이드 렌더링)

![image](https://user-images.githubusercontent.com/87118337/146923605-e52ead90-3112-472b-8197-950b9d6ff2c7.png)
- HTML 최종 결과를 서버에서 만들어서 웹 브라우저에 전달
- 주로 정적 화면에 사용
- JSP, 타임리프 (백엔드 개발자)

![image](https://user-images.githubusercontent.com/87118337/146923727-8a6d5b3a-b422-48b9-866e-c96008da1188.png)
- HTML 결과를 자바스크립트를 사용해 웹 브라우저에서 동적으로 생성해서 적용
- 주로 동적 화면에 사용 (웹 환경을 마치 앱처럼 필요한 부분부분 변경 가능)
- 예) 구글 지도, Gmail, 구글 캘린더
- React, Vue.js (프론트엔드 개발자)

> React, Vue.js를 CSR + SSR 동시에 지원하는 웹 프레임워크도 있음
> SSR을 사용하더라도, 자바스크립트를 사용해서 화면 일부를 동적으로 변경 가능

> 백엔드 개발자의 웹 프론트엔드 기술 학습은 옵션
> 백엔드 개발자는 서버, DB, 인프라 등등 수 많은 백엔드 기술을 공부해야 한다.
> 웹 프론트엔드도 깊이있게 잘 하려면 숙련에 오랜 시간이 필요하다.


## 자바 백엔드 웹 기술의 역사

- 1997 / 서블릿
- 1999 / JSP
- 서블릿, JSP 조합 MVC 패턴 사용
- 2000년대 초 ~ 2010년 초 / MVC 프레임워크 
  - MVC 패턴 자동화, 다양한 기능 지원
- 현재 / 애노테이션 기반의 스프링 MVC, 스프링 부트
  - MVC 프레임워크 마무리
  - 스프링 부트에 서버 내장
  - 과거에는 서버에 WAS를 직접 설치, 소스는 War 파일을 만들어서 설치한 WAS에 배포
  - 스프링 부트는 빌드 결과(Jar)에 WAS 서버 포함 > 빌드 배포 단순화

### Web Servlet - Spring MVC

### Web Reactive - Spring WebFlux
- 완전한 비동기 Non Blcoking 처리
- 최소 쓰레드로 최대 성등 (고효율)
- 함수형 스타일 개발 > 동시처리 코드 효율화
- 서블릿 기술 사용 하지 않음
- 기술적 난이도 높음
- RDB 지원 부족
- 일반 MVC 쓰레드 모델도 충분히 빠름
- 실무에서 아직 많이 사용하지 않음

### 자바 뷰 템플릿 역사
- JSP는 속도가 느리고 기능이 부족
- 프리마커, 벨로시티는 속도 문제 해결, 다양한 기능
- 타임리프는 HTML 모양을 유지하면서 뷰 템플릿 적용 가능
  - 스프링 MVC와 강력한 기능 통합
  - 최선의 선택
