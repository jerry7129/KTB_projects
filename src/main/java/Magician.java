public class Magician extends Human {
    int mp = 500;
    int normalMagicAttackDamage = 5;
    int normalMagicAttackMp = 50;

    @Override
    public void status() {
        int hp = getHp();
        System.out.println("직업: 마법사");
        System.out.println("현재 체력: " + hp);
        System.out.println("현재 마나: " + mp);
    }

    void normalMagicAttack() {
        System.out.println("일반 마법 공격을 시전합니다. 데미지: " + normalMagicAttackDamage);
        mp -= normalMagicAttackMp;
        System.out.println("현재 남은 마나: " + mp);
    }

    int getMp() {
        return mp;
    }

    void setMp(int mp) {
        this.mp = mp;
    }
}
