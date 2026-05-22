public class Tshirt extends Clothes{
    private String neckType;

    public Tshirt(String id, String name, int price, String size, String material, String neckType) {
        super(id, name, price, size, material);
        this.neckType = neckType;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();
        System.out.println("[티셔츠 상세 내역]");
        System.out.println("넥 타입: " + neckType);
    }
}
