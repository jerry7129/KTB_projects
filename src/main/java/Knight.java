public class Knight extends Warrior{
    int greatSwordAttackDamage = 20;
    int greatSwordAttackStamina = 2;

    void greatSwordAttack(){
        int stamina = getStamina();
        System.out.println("대검 공격을 시전합니다. 데미지: " + greatSwordAttackDamage);
        stamina -= greatSwordAttackStamina;
        setStamina(stamina);
        System.out.println("현재 남은 기력: " + stamina);
    }
}
