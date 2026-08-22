import java.util.*;
public class IP_2 {
    public static void main(String[] args) {
        Random random = new Random();
        Scanner sc = new Scanner(System.in);
        int num = random.nextInt(100) + 1;
        int moves = 10;
        System.out.println("Welcome to the Guess The Number Game!");
        while(moves > 0){
            System.out.print("Enter your guess(1-100): ");
            int guess = sc.nextInt();
            if(guess == num){
                System.out.println("Congrats! You guess the answer in " + (10 - moves) + " attempts!");
                break;
            } else if(guess < num){
                System.out.println("Sorry! The guessed number is low!");
            } else {
                System.out.println("Sorry! The guessed number is high!");                
            }
            moves--;
        }
        if(moves == 0){
            System.out.println("You lost the game!");
        }
        sc.close();
    }
}