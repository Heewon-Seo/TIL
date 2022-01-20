```java
package section1;

import java.util.Scanner;

public class Zip {

    public String solution(String str) {
        String answer = "";
        int number = 0;

        for(int i = 0; i < str.length() ; i++) {
            if(i < str.length()-1 && str.charAt(i)==str.charAt(i+1)) {
                number++;
            } else if (number!=0){
                answer += str.charAt(i);
                answer += number+1;
                number = 0;
            } else {
                answer += str.charAt(i);
            }

        }

        return answer;
    }

    public static void main(String[] args) {
        Zip main = new Zip();
        Scanner scanner = new Scanner(System.in);

        System.out.println(main.solution(scanner.next()));
    }
}
```
