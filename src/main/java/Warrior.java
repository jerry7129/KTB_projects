public class Warrior extends Human {
    int stamina = 10;
    int normalSwordAttackDamage = 5;
    int normalSwordAttackStamina = 1;

    @Override
    public void status() {
        int hp = getHp();
        System.out.println("직업: 전사");
        System.out.println("현재 체력: " + hp);
        System.out.println("현재 기력: " + stamina);
    }

    public void normalSwordAttack() {
        System.out.println("일반 전사 공격을 시전합니다. 데미지: " + normalSwordAttackDamage);
        stamina -= normalSwordAttackStamina;
        System.out.println("현재 남은 기력: " + stamina);
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }
}
