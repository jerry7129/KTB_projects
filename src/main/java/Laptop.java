public class Laptop extends Electronics{
    private String cpuName;
    private String ramSize;

    public Laptop(String id, String name, int price, int voltage, String cpuName, String ramSize) {
        super(id, name, price, voltage);
        this.cpuName = cpuName;
        this.ramSize = ramSize;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("[노트북 상세 내역]");
        System.out.println("cpuName: " + cpuName);
        System.out.println("ramSize: " + ramSize);
    }

    public void playGame() {
        String name = getName();
        System.out.println(name + "에 게임을 실행합니다.");
    }
}
