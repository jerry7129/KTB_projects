public class Smartphone extends Electronics{
    private String carrier;
    private String phoneNumber;

    public Smartphone(String id, String name, int price, int voltage, String carrier, String phoneNumber) {
        super(id, name, price, voltage);
        this.carrier = carrier;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();
        System.out.println("[스마트폰 상세 내역]");
        System.out.println("통신사: " + carrier);
        System.out.println("전화번호: " + phoneNumber);
    }

    public void phoneCall(){
        System.out.println(phoneNumber + "로 누군가에게 전화를 겁니다.");
    }
}
