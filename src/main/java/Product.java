public class Product {
    private String id;
    private String name;
    private int price;

    public Product(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void displayInfo() {
        System.out.println("[상품 내역]");
        System.out.println("id: " + id);
        System.out.println("이름: " + name);
        System.out.println("가격: " + price + " 원");
    }

    public void buy() {
        System.out.println("[상품 구매]");
        System.out.println(name + "을 구매하셨습니다. 가격: " + price + " 원");
    }

    public String getName(){
        return name;
    }

    public int getPrice(){
        return price;
    }
}
