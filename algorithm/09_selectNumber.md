```java
package section1;

import java.awt.desktop.ScreenSleepListener;
import java.util.Scanner;

public class SelectNumber {
    public int solution(String str) {
        int answer = 0;
        String number = "";

        for(int i = 0; i < str.length() ; i++) {

            if(Character.isDigit(str.charAt(i))) {
                number += str.charAt(i);
            }
        }

        answer = Integer.parseInt(number);


        return answer;
    }

    public static void main(String[] args) {
        SelectNumber main = new SelectNumber();
        Scanner scanner = new Scanner(System.in);

        System.out.println(main.solution(scanner.next()));
    }
}
```
