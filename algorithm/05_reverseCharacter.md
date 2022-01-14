```java
package section1;

import jdk.jshell.Snippet;

import java.util.Scanner;

public class ReverseChart {

    public String solution (String str) {

        char[] array = str.toCharArray();
        int lt = 0;
        int rt = str.length()-1;

        while (lt<rt) { // 암기하세용
            if(!Character.isAlphabetic(array[lt])) {
                lt++;
            } else if(!Character.isAlphabetic(array[rt])) {
                rt--;
            } else {
                char tmp = array[lt];
                array[lt] = array[rt];
                array[rt] = tmp;
                lt++;
                rt--;
            }
        }

        String answer = String.valueOf(array);
        return answer;
    }

    public static void main(String[] args) {
        ReverseChart main = new ReverseChart();
        Scanner scan = new Scanner(System.in);

        String str = scan.next();

        System.out.println(main.solution(str));
    }
}
```
