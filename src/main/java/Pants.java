public class Pants extends Clothes{
    private int length;

    public Pants(String id, String name, int price, String size, String material, int length) {
        super(id, name, price, size, material);
        this.length = length;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();
        System.out.println("[바지 상세 내역]");
        System.out.println("길이: " + length);
    }
}
