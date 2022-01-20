```java
package section1;

import java.util.Scanner;

public class Password {

    public String solution(int num, String str) {
        String answer = "";

        // 7개로 나눠서 해석해야됨
        for(int i = 0 ; i < num ; i++) {
            String tmp = str.substring(0,7).replace('#','1').replace('*','0');
            
            // 2진수를 10진수로 바꿔줌
            int number = Integer.parseInt(tmp,2);

            // 아스키코드를 char로 형변환
            answer += (char) number;

            // 나머지 문자열
            str = str.substring(7);

        }

        return answer;
    }

    public static void main(String[] args) {
        Password main = new Password();
        Scanner scanner = new Scanner(System.in);

        int num = scanner.nextInt();
        String str = scanner.next();

        System.out.println(main.solution(num,str));
    }
}
```
