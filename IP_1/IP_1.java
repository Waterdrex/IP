import java.util.*;

public class IP_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a first number: ");
        int a = sc.nextInt();
        System.out.print("Enter a second number: ");
        int b = sc.nextInt();
        System.out.print("Enter a third number: ");
        int c = sc.nextInt();
        System.out.printf("The greatest number is: %d", ((a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c)));
        sc.close();
    }
}

class Main{
    public static void main(String[] args) {
        for(int i = 1; i <= 30; i++){
            System.out.println((i % 15 == 0) ? "FizzBuzz" : (i % 5 == 0) ? "Buzz" : (i % 3 == 0) ? "Fizz" : i);
        }
    }
}