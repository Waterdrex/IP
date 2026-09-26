class Calc {
    int add(int a, int b){
        return a + b;
    }
    int add(int a, int b, int c){
        return a + b + c;
    }
}

public class MethodOverloading {
    public static void main(String[] args){
        Calc calc = new Calc();
    }
}
