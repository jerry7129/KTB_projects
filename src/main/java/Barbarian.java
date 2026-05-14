public class Barbarian extends Warrior {
    int axeAttackDamage = 30;
    int axeAttackStamina = 3;

    void axeAttack() {
        int stamina = getStamina();
        System.out.println("도끼 공격을 시전합니다. 데미지: " + axeAttackDamage);
        stamina -= axeAttackStamina;
        setStamina(stamina);
        System.out.println("현재 남은 기력: " + stamina);
    }
}
