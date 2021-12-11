# Section 2 - 스프링 핵심 원리 이해 1 - 예제 만들기

![Screenshot_2021-12-12 01 09 39_XxqSNl](https://user-images.githubusercontent.com/87118337/145683508-6db2badc-8a0a-40d7-a308-f5085bf40997.png)

도메인 협력 관계 > (개발자 구체화) > 클래스 다이어그램 (정적) > (구현체는 동적으로 결정 - 서버가 뜰 때 new 해가지고 뭐가 들어갈지 결정됨) > 객체 다이어그램 (new한 인스턴스끼리의 참조 - 동적)


### MemberServiceImpl

```java
package hello.core.member;

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    // 문제점 -> 추상화(MemberRepository)에도 의존하고 구체화(MemoryMemberRepository)에도 의존
    // 변경할 떄 문제가 됨 (DIP 위반)

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
```

### OrderServiceImpl

```java
package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);
        // 단일 체계 원칙을 잘 지키고 설계된 것
        // 구현체는 Discount에 내부 구조를 몰라도 실행 가능
        // Discount 로직이 바뀌면 여기가 아니라 DiscountPolicy 부분만 바꾸면 됨

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
```

### MemberServiceTest

```java

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    MemberService memberService = new MemberServiceImpl();

    @Test
    void join() {
        // given
        Member member = new Member(1L, "memberA", Grade.VIP);

        // when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then
        Assertions.assertThat(member).isEqualTo(findMember);
        // 똑같은지 테스트
        // 테스트 코드는 선택이 아닌 필수
    }
}
```

### OrderServiceTest

```java
package hello.core.order;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    MemberService memberService = new MemberServiceImpl();
    OrderService orderService = new OrderServiceImpl();

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
