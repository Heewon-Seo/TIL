
# Section 2 - 서블릿

```java
package hello.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // service method 호출됨
        System.out.println("HelloServlet.service");

        String username = req.getParameter("username");
        System.out.println("username = " + username);

        resp.setContentType("text/plain"); // header
        resp.setCharacterEncoding("utf-8"); // header
        resp.getWriter().write("hello " + username); // body
    }
}
```
method 단축키: ⌃O

>HelloServlet.service
>req = org.apache.catalina.connector.RequestFacade@2ce486d5
>resp = org.apache.catalina.connector.ResponseFacade@6b3be0b4

![image](https://user-images.githubusercontent.com/87118337/147088297-25a70988-092e-434a-a965-ef9818d6ebbf.png)

### HTTP 요청 로그로 확인

`logging.level.org.apache.coyote.http11=debug`
이거를 `application.properties`에 추가

요청해보면 서버가 받은 HTTP 요청 메시지를 출력하는 것을 확인할 수 있다.
개발단계에만 쓸것! (성능저하 이슈 발생 가능)

### 서블릿 컨테이너 동작 방식
![image](https://user-images.githubusercontent.com/87118337/147088771-9577b500-efe0-4623-bf21-7b9133f43e18.png)

## HttpServletRequest

```java
package hello.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getMethod();

        printStartLine(req);
        printHeaders(req);
        printHeaderUtils(req);

    }

    private void printStartLine(HttpServletRequest req) {
        System.out.println("--- REQUEST-LINE - start ---");
        System.out.println("request.getMethod() = " + req.getMethod()); //GET
        System.out.println("request.getProtocal() = " + req.getProtocol()); //HTTP/1.1
        System.out.println("request.getScheme() = " + req.getScheme()); //http
        // http://localhost:8080/request-header
        System.out.println("request.getRequestURL() = " + req.getRequestURL());
        // /request-test
        System.out.println("request.getRequestURI() = " + req.getRequestURI());
        //username=hi
        System.out.println("request.getQueryString() = " +
                req.getQueryString());
        System.out.println("request.isSecure() = " + req.isSecure()); //https 사용 유무
        System.out.println("--- REQUEST-LINE - end ---");
        System.out.println();
    }

    private void printHeaders(HttpServletRequest request) {
        System.out.println("--- Headers - start ---");

//        Enumeration<String> headerNames = request.getHeaderNames();

//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println(headerName + ": " + headerName);
//        }

        request.getHeaderNames().asIterator().forEachRemaining(headerName -> System.out.println(headerName + ": " + headerName));
        // 간결한 방식

        System.out.println("--- Headers - end ---");
        System.out.println();
    }

    //Header 편리한 조회
    private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- Header 편의 조회 start ---");
        System.out.println("[Host 편의 조회]");
        System.out.println("request.getServerName() = " + request.getServerName()); //Host 헤더 System.out.println("request.getServerPort() = " +
        System.out.println("requestl.getServerPort() = " + request.getServerPort()); //Host 헤더 System.out.println();
        System.out.println("[Accept-Language 편의 조회]");
        request.getLocales().asIterator()
                .forEachRemaining(locale -> System.out.println("locale = " + locale));
        System.out.println("request.getLocale() = " + request.getLocale());
        System.out.println();
        System.out.println("[cookie 편의 조회]"); if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            } }
        System.out.println();
        System.out.println("[Content 편의 조회]");
        System.out.println("request.getContentType() = " + request.getContentType());
        System.out.println("request.getContentLength() = " +    request.getContentLength());
        System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());
        System.out.println("--- Header 편의 조회 end ---");
        System.out.println();
    }
}
```

```
Host: localhost:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: " Not A;Brand";v="99", "Chromium";v="96", "Google Chrome";v="96"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "macOS"
DNT: 1
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
Cookie: Webstorm-d12afbe9=8a4504a3-10ca-4200-8107-2691e35b233c

]
--- REQUEST-LINE - start ---
request.getMethod() = GET
request.getProtocal() = HTTP/1.1
request.getScheme() = http
request.getRequestURL() = http://localhost:8080/request-header
request.getRequestURI() = /request-header
request.getQueryString() = username=hello
request.isSecure() = false
--- REQUEST-LINE - end ---

--- Headers - start ---
host: host
connection: connection
cache-control: cache-control
sec-ch-ua: sec-ch-ua
sec-ch-ua-mobile: sec-ch-ua-mobile
sec-ch-ua-platform: sec-ch-ua-platform
dnt: dnt
upgrade-insecure-requests: upgrade-insecure-requests
user-agent: user-agent
accept: accept
sec-fetch-site: sec-fetch-site
sec-fetch-mode: sec-fetch-mode
sec-fetch-user: sec-fetch-user
sec-fetch-dest: sec-fetch-dest
accept-encoding: accept-encoding
accept-language: accept-language
cookie: cookie
--- Headers - end ---

--- Header 편의 조회 start ---
[Host 편의 조회]
request.getServerName() = localhost
requestl.getServerPort() = 8080
[Accept-Language 편의 조회]
locale = ko_KR
locale = ko
locale = en_US
locale = en
request.getLocale() = ko_KR

[cookie 편의 조회]
Webstorm-d12afbe9: 8a4504a3-10ca-4200-8107-2691e35b233c

[Content 편의 조회]
request.getContentType() = null
request.getContentLength() = -1
request.getCharacterEncoding() = UTF-8
--- Header 편의 조회 end ---
```

## HTTP 요청 데이터

