```java
package section1;

import java.util.Locale;
import java.util.Scanner;

public class CheckString {

    public String solution (String str) {
        String answer = "";

        for(int i = 0 ; i < str.length() ; i++) {
//            System.out.println(str.charAt(i));
//            System.out.println(str.charAt(str.length()-1-i));
            if(str.toLowerCase().charAt(i) == str.toLowerCase().charAt(str.length()-1-i)) {
                answer = "YES";
            } else {
                answer = "NO";
                break;
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        CheckString main = new CheckString();
        Scanner scanner = new Scanner(System.in);

        System.out.println(main.solution(scanner.next()));
    }
}
```

