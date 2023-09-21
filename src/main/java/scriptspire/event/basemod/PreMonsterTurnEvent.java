package scriptspire.event.basemod;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PreMonsterTurnEvent {
    public AbstractMonster monster;

    //  will this monster take turn
    public boolean takeTurn;
}
