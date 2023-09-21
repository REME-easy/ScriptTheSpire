package scriptspire.event.basemod;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PostPowerApplyEvent {
    public AbstractPower power;
    public AbstractCreature target;
    public AbstractCreature source;
}
