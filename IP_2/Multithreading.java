class Hi extends Thread {
    public void run(){
        for(int i = 0; i < 10; i++){
            System.out.println("Hi");
            try {
                Thread.sleep(1000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class Huku {

}

public class Multithreading {
    public static void main(String[] args){
        
    }
}
