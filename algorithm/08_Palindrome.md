```java
package section1;

import java.util.Locale;
import java.util.Scanner;

public class Palindrome {
    public String solution (String str) {
        String answer = "";
        String newString = "";

        for (int i = 0 ; i < str.length() ; i++) {

            if(Character.isAlphabetic(str.charAt(i))) {
                newString += str.toLowerCase().charAt(i);
            }
        }

        for (int j = 0 ; j < newString.length() ; j++) {
            if(newString.charAt(j)==newString.charAt(newString.length()-1-j)) {
                answer = "YES";
            } else {
                answer = "NO";
                break;
            }
        }


        return answer;
    }

    public static void main(String[] args) {
        Palindrome main = new Palindrome();
        Scanner scanner = new Scanner(System.in);

        System.out.println(main.solution(scanner.nextLine()));
    }
}
```
