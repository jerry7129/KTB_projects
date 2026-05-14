public class IceMagician extends Magician{
    int iceMagicAttackDamage = 45;
    int iceMagicAttackMp = 200;

    void iceMagicAttack(){
        int mp = getMp();
        System.out.println("얼음 마법을 시전합니다. 데미지: " + iceMagicAttackDamage);
        mp -= iceMagicAttackMp;
        setMp(mp);
        System.out.println("현재 남은 마나: " + mp);
    }
}
