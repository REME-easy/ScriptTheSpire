package scriptspire.script.proxy;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.StsProxy;
import scriptspire.card.GeneratedCard;
import scriptspire.modcore.ScriptTheSpire;
import scriptspire.script.effect.AddCardEffect;

public class AddCard extends TwoArgFunction {
    public static AddCardEffect CURRENT;

    @Override
    public LuaValue call(LuaValue id, LuaValue cardBuilder) {
        CURRENT = new AddCardEffect(id.tojstring());
//        CURRENT.card = (GeneratedCard) CoerceLuaToJava.coerce(cardBuilder.call(CoerceJavaToLua.coerce(new GeneratedCard(id.tojstring()))), GeneratedCard.class);
        CURRENT.card = (GeneratedCard) CoerceLuaToJava.coerce(StsProxy.createCard(id).arg(1), GeneratedCard.class);
        CURRENT.card.damage = CURRENT.card.baseDamage;
        CURRENT.card.block = CURRENT.card.baseBlock;
        CURRENT.card.magicNumber = CURRENT.card.baseMagicNumber;
        CURRENT.card.costForTurn = CURRENT.card.cost;
        ScriptTheSpire.SCRIPT.currentScript.effects.put(String.format("card:%s", id), CURRENT);
        CURRENT = null;
        return NONE;
    }
}
