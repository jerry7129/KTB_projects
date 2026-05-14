public class FireMagician extends Magician{
    int fireMagicAttackDamage = 50;
    int fireMagicAttackMp = 250;

    void fireMagicAttack(){
        int mp = getMp();
        System.out.println("화염 마법을 시전합니다. 데미지: " + fireMagicAttackDamage);
        mp -= fireMagicAttackMp;
        setMp(mp);
        System.out.println("현재 남은 마나: " + mp);
    }
}
