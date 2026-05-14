import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Human player = null;

        while (player == null) {
            System.out.println("========== 직업 선택 ==========");
            System.out.println("1. 무직 (Human)");
            System.out.println("2. 바바리안 (Barbarian)");
            System.out.println("3. 기사 (Knight)");
            System.out.println("4. 화염 마법사 (Fire Magician)");
            System.out.println("5. 얼음 마법사 (Ice Magician)");
            System.out.println("0. 프로그램 종료");
            System.out.print("선택: ");

            int choice = scanner.nextInt();
            if (choice == 0) {
                System.out.println("프로그램을 종료합니다.");
                scanner.close();
                return;
            }

            switch (choice) {
                case 1: player = new Human(); break;
                case 2: player = new Barbarian(); break;
                case 3: player = new Knight(); break;
                case 4: player = new FireMagician(); break;
                case 5: player = new IceMagician(); break;
                default: System.out.println("잘못된 입력입니다. 다시 선택해주세요.\n");
            }
        }

        System.out.println("\n캐릭터가 생성되었습니다!");
        player.status();

        while (true) {
            System.out.println("\n========== 행동 선택 ==========");
            System.out.println("1. 상태창 확인");
            System.out.println("2. 일반 공격 (모든 직업 공통)");

            if (player instanceof Warrior) {
                System.out.println("3. 전사 기본 공격");
            } else if (player instanceof Magician) {
                System.out.println("3. 마법사 기본 공격");
            }

            if (player instanceof Barbarian) {
                System.out.println("4. 도끼 공격 (바바리안 전용)");
            } else if (player instanceof Knight) {
                System.out.println("4. 대검 공격 (기사 전용)");
            } else if (player instanceof FireMagician) {
                System.out.println("4. 화염 마법 (화염 마법사 전용)");
            } else if (player instanceof IceMagician) {
                System.out.println("4. 얼음 마법 (얼음 마법사 전용)");
            }

            System.out.println("0. 프로그램 종료");
            System.out.print("선택: ");

            int action = scanner.nextInt();

            if (action == 0) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            System.out.println("------------------------------");
            if (action == 1) {
                player.status();
            } else if (action == 2) {
                player.normalAttack();
            } else if (action == 3) {
                if (player instanceof Warrior) {
                    ((Warrior) player).normalSwordAttack();
                } else if (player instanceof Magician) {
                    ((Magician) player).normalMagicAttack();
                } else {
                    System.out.println("해당 직업은 사용할 수 없는 스킬입니다.");
                }
            } else if (action == 4) {
                if (player instanceof Barbarian) {
                    ((Barbarian) player).axeAttack();
                } else if (player instanceof Knight) {
                    ((Knight) player).greatSwordAttack();
                } else if (player instanceof FireMagician) {
                    ((FireMagician) player).fireMagicAttack();
                } else if (player instanceof IceMagician) {
                    ((IceMagician) player).iceMagicAttack();
                } else {
                    System.out.println("해당 직업은 사용할 수 없는 스킬입니다.");
                }
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }

        scanner.close();
    }
}