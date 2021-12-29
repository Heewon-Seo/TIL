한 개의 문장이 주어지면 그 문장 속에서 가장 긴 단어를 출력하는 프로그램을 작성하세요.

문장속의 각 단어는 공백으로 구분됩니다.

```java
package section1;

import java.util.Scanner;

public class LongestWord {
    public String solution(String str) {
        String answer = "";

        String[] array = str.split(" ");

        for(String word : array) {
            if(answer.length() < word.length()) {
                answer = word;
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        LongestWord l = new LongestWord();
        Scanner scanner = new Scanner(System.in);

        String str = scanner.nextLine();

        System.out.println(l.solution(str));

    }
}
```
