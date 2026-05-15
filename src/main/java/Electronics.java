public class Electronics extends Product {
    private int voltage;

    public Electronics(String id, String name, int price, int voltage){
        super(id, name, price);
        this.voltage = voltage;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();
        System.out.println("[전자제품 상세 내역]");
        System.out.println("필요 전압: " + voltage);
    }

    public void powerOn(){
        String name = getName();
        System.out.println(name + " 전원을 켭니다.");
    }
}
