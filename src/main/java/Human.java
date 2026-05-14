public class Human {
    private int normalAttackDamage = 2;
    private int hp = 100;

    public void status() {
        System.out.println("직업: 무직");
        System.out.println("현재 체력: " + hp);
    }

    public void normalAttack() {
        System.out.println("일반 공격을 시전합니다. 데미지: " + normalAttackDamage);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
