class Bank {
    int balance;
    public Bank(int balance){
        this.balance = balance;
    }
    public void showBalance(){
        System.out.println("Balance: " + this.balance);
    }
    public void deposit(int amount){
        this.balance += amount;
        System.out.println("The amount is deposited successfully!");
    }
    public void withdraw (int amount){
        this.balance -= amount;
        System.out.println("The amount is withdraw successfully!");
    }
}

class HDFC extends Bank {
    public HDFC(int balance){
        super(balance);
    }
}

class SBI extends Bank {
    public SBI(int balance){
        super(balance);
    }
    public void withdraw(int amount){
        this.balance -= (amount + 50);
    }
}

class BankOOPs {
    public static void main(String[] args){
        // Bank acc1 = new Bank(1000);
        // acc1.showBalance();
        // acc1.deposit(500);
        // acc1.showBalance();

        SBI acc2 = new SBI(10000);
        acc2.showBalance();
        acc2.deposit(500);
        acc2.showBalance();
        acc2.withdraw(50);
        acc2.showBalance();
    }
}