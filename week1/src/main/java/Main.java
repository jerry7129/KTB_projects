import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Product> productList = new ArrayList<>();

        productList.add(new Laptop("L001", "맥북 프로 16", 3500000, 220, "M3 Max", "64GB"));
        productList.add(new Smartphone("S001", "아이폰 15", 1250000, 110, "SKT", "010-1234-5678"));
        productList.add(new Tshirt("T001", "오버핏 그래픽 티셔츠", 35000, "XL", "면 100%", "라운드 넥"));
        productList.add(new Pants("P001", "와이드 데님 팬츠", 59000, "L", "데님", 105));

        while (true) {
            System.out.println("===== 쇼핑몰 관리 프로그램 =====");
            System.out.println("1. 전체 상품 정보 보기");
            System.out.println("2. 상품 일괄 구매");
            System.out.println("3. 기기 특수 기능 실행");
            System.out.println("0. 프로그램 종료");
            System.out.print("메뉴를 선택하세요: ");

            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n[전체 상품 정보 목록]");
                    for (Product p : productList) {
                        p.displayInfo();
                        System.out.println("--------------------");
                    }
                    break;
                case 2:
                    System.out.println("\n[전체 상품 구매 처리]");
                    for (Product p : productList) {
                        p.buy();
                    }
                    System.out.println();
                    break;
                case 3:
                    System.out.println("\n[기기 기능 실행]");
                    for (Product p : productList) {
                        if (p instanceof Electronics) {
                            ((Electronics) p).powerOn();
                            if (p instanceof Laptop) {
                                ((Laptop) p).playGame();
                            } else if (p instanceof Smartphone) {
                                ((Smartphone) p).phoneCall();
                            }
                        }
                    }
                    System.out.println();
                    break;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택해주세요.\n");
            }
        }

        scanner.close();
    }
}