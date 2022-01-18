```java
package section1;

import java.util.Scanner;

public class DeleteChar {

    public String solution(String str) {
        String answer = "";

        for(int i = 0 ; i < str.length() ; i++) {
//            System.out.println(str.charAt(i) + " " + str.indexOf(str.charAt(i)));
            if(str.indexOf(str.charAt(i)) == i) answer += str.charAt(i);
        }

        /*
        k 0
        s 1
        e 2
        k 0
        k 0
        s 1
        e 2
        t 7
         */



        return  answer;
    }

    public static void main(String[] args) {

        DeleteChar main = new DeleteChar();
        Scanner scanner = new Scanner(System.in);

        System.out.println(main.solution(scanner.next()));

    }
}
```
