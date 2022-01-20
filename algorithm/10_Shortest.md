```java
package section1;

import java.util.Scanner;

public class Shortest {
    public int[] solution (String str, char t) {
        int[] answer = new int[str.length()];
        int p = 1000;

        for(int i = 0 ; i < str.length() ; i++) {
            if(str.charAt(i)==t) {
                p=0;
                answer[i]=p;
            } else {
                p++;
                answer[i]=p;
            }
        }

        p=1000;
        for(int i = str.length()-1; i >= 0 ; i--) {
            if(str.charAt(i)==t) {
                p=0;
            } else {
                p++;
                answer[i]=Math.min(answer[i], p);
            }
        }

//        String[] array = str.split(" ");
//        char[] chars = array[0].toCharArray();
//        for(int i = 0 ; i < chars.length ; i++) {
//            if(chars[i]==array[1].charAt(0)) {
//                System.out.println(i);
//            }
//        }
//        for(char c : array[0].toCharArray()) {
//            System.out.println(c);
//            if(c==array[1].indexOf(0)) {
//
//            }
//        }
//        for(int i = 0 ; i < array[0].length() ; i++) {
//            System.out.println(array[0].indexOf(array[1]));
//        }


        return answer;
    }

    public static void main(String[] args) {
        Shortest main = new Shortest();
        Scanner scanner = new Scanner(System.in);

        String str = scanner.next();
        char c = scanner.next().charAt(0);

        for (int x : main.solution(str, c)) {
            System.out.print(x+" ");
        }

    }
}
```
