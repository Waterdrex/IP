class Circle {
    int radius;

    public Circle(int r){
        this.radius = r;
    }

    public void area(){
        System.out.println("I am from Circle area with radius " + this.radius);
    }
}

class Rectangle {
    int length, width;
    public Rectangle(int length, int width){
        this.length = length;
        this.width = width;
    }
    public int area(){
        return this.length * this.width;
    }
    public int perimeter(){
        return 2*(this.length) + 2*(this.width);
    }
}

public class OOP {
    public static void main(String[] args) {
        Circle cobj1 = new Circle(5);
        cobj1.area();

        Circle cobj2 = new Circle(7);
        cobj2.area();

        Rectangle rect1 = new Rectangle(5, 2);
        System.out.printf("The area and perimeter of the rectangle is %d and %d", rect1.area(), rect1.perimeter());
        Rectangle rect2 = new Rectangle(4, 7);
        System.out.printf("The area and perimeter of the rectangle is %d and %d", rect2.area(), rect2.perimeter());
    }
}