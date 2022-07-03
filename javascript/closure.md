# Closure



* ## What is Closure?
* #### Reference
  * https://youtu.be/tpl2oXQkGZs
* ### 어휘적 환경 (Lexical Environment)
* ```javascript
      
      let one;
      one = 1;
      
      function addOne(num) {
        console.log(one + num);
      }
      
      addOne(5);
  ```
  * 자바스크립트는 어휘적 환경을 갖는다. 위에서부터 아래로 실행되는 순서를 보면
    * #### Line 1
      * 변수들이 `Lexical 환경`에 올라간다 (외부 Lexical 환경)
      * let으로 선언된 변수도 호이스팅 되기 때문에 `one`과 `addOne`이 올라가게 된다
      * 이 때, `one`은 초기화 되지 않은 상태로 올라간다 (= 사용불가)
      * 함수는 바로 초기화되기 때문에 사용 가능
    * #### Line 2
      * `one`은 `undefined` 상태
      * 사용을 해도 에러가 발생하지 않음
    * #### Line 3
      * `one`에 `1`이 할당됨
    * #### Line 9
      * 함수 `addOne(5)`가 실행됨
      * 그 순간, 새로운 `Lexical 환경`이 만들어지고 (내부 Lexical 환경)
      * `num`에 `5`가 저장됨
    * **내부 Lexical 환경은 외부 Lexical 환경을 참조한다**
* ```javascript
      function makeAdder(x) {
        return function(y) {
          return x + y;
        }
      }
      
      const add3 = makeAdder(3);
      console.log(add3(2));
  ```
  * 한가지 더 예시를 보자
    * #### 전역 Lexical 환경
      * `makeAddr` 함수가 초기화
      * `add3`는 초기화되지 않은 상태
    * #### Line 7
      * 여기가 실행될 때, `makeAdder`가 실행된다
      * 그러면서, `makeAdder`의 `Lexical 환경`이 만들어진다
      * 여기서 전달 받은 `x=3` 값이 들어간다
    * #### Line 8
      * 여기가 실행되면 `Line 2`부터 `Line 4`까지가 실행된다
      * 이 때, 또 `익명함수`의 `Lexical 환경`이 만들어진다
      * 이번엔 이 안에 `y=2`가 들어간다
    * #### x + y를 하는 과정
      *
        1. 처음에는 먼저 `익명함수 Lexical 환경`에서 변수를 찾는다
        2. `y`는 있는데 `x`가 없다
        3. 참조하는 `makeAdder Lexical 환경`에 간다
        4. `x`를 찾음
  * #### 정리
    * `익명함수`는 `y`를 가지고 있고 상위함수인 `makeAdder`의 `x`에 접근할 수 있다
    * `add3` 함수가 생성된 이후에도 상위함수인 `makeAdder`의 `x`에 접근할 수 있다
    * 이런 것을 `Closure`라고 한다
    * **Closure**
      * 함수와 렉시컬 환경의 조함
      * 함수가 생성될 당시의 외부 변수를 기억
      * 생성 이후에도 계속 접근 가능한 기능
* ```javascript
      function makeCounter() {
        let num = 0; // 은닉화
        
        return function() {
          return num++;
        }
      }
      
      let counter = makeCounter();
      
      console.log(counter()); // 0
      console.log(counter()); // 1
      console.log(counter()); // 2
  ```
  * 여기서 `counter()`를 호출하면, `내부 함수`에서 `외부 함수`에 접근하게 된다
  * `let counter`는 생성된 이후에, 계속 값을 기억하고 있는 것
  * 여기서 `console.log`로 찍힌 결과값들을 수정할 수 있을까?
  * 불가능하다
  * 즉, `은닉화`에 성공한 것
*
*
