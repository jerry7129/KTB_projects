public class Clothes extends Product{
    private String size;
    private String material;

    public Clothes(String id, String name, int price, String size, String material) {
        super(id, name, price);
        this.size = size;
        this.material = material;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();
        System.out.println("[옷 상세 내역]");
        System.out.println("사이즈: " + size);
        System.out.println("재료: " + material);
    }
}
