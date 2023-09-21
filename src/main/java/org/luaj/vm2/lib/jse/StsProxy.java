package org.luaj.vm2.lib.jse;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import scriptspire.action.GeneratedAction;
import scriptspire.card.GeneratedCard;
import scriptspire.modcore.Utils;
import scriptspire.script.ClassCaches;

import java.net.URISyntaxException;

public class StsProxy extends VarArgFunction {
    static final int INIT = 0;
    static final int ADDTOBOT = 1;
    static final int ADDTOTOP = 2;
    static final int NEWCARD = 3;
    static final int NEWRELIC = 4;
    static final int NEWPOTION = 5;
    static final int NEWPOWER = 6;
    static final int NEWACTION = 7;
    static final int NEWDAMAGEINFO = 8;

    static final ClassCaches CLASS_CACHES;

    static {
        try {
            CLASS_CACHES = new ClassCaches();
        } catch (CannotCompileException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static final String[] NAMES = {
            "addToBot",
            "addToTop",
            "newCard",
            "newRelic",
            "newPotion",
            "newPower",
            "newAction",
            "newDamageInfo"
    };

    public Varargs invoke(Varargs args) {
        try {
            switch (opcode) {
                case INIT: {
                    // LuaValue modname = args.arg1();
                    LuaValue env = args.arg(2);
                    LuaTable t = new LuaTable();
                    bind(t, this.getClass(), NAMES, ADDTOBOT);
                    t.set("dungeon", JavaClass.forClass(AbstractDungeon.class));
                    env.set("sts", t);
                    if (!env.get("package").isnil()) env.get("package").get("loaded").set("sts", t);
                    return t;
                }
                case NEWCARD: {
                    AbstractCard card = CardLibrary.getCard(args.checkjstring(1));
                    if (card == null) return NONE;
                    return JavaClass.forClass(card.getClass()).getConstructor().invoke(args.subargs(2));
                }
                case NEWRELIC: {
                    AbstractRelic relic = RelicLibrary.getRelic(args.checkjstring(1));
                    if (relic == null) return NONE;
                    return JavaClass.forClass(relic.getClass()).getConstructor().invoke(args.subargs(2));
                }
                case NEWPOTION: {
                    AbstractPotion potion = PotionHelper.getPotion(args.checkjstring(1));
                    if (potion == null) return NONE;
                    return JavaClass.forClass(potion.getClass()).getConstructor().invoke(args.subargs(2));
                }
                case NEWPOWER: {
                    return JavaClass.forClass(BaseMod.getPowerClass(args.checkjstring(1))).getConstructor().invoke(args.subargs(2));
                }
                case NEWACTION: {
                    if (args.isstring(1)) {
                        return JavaClass.forClass(CLASS_CACHES.getAction(args.checkjstring(1))).getConstructor().invoke(args.subargs(2));
                    } else if (args.isfunction(1)) {
                        return CoerceJavaToLua.coerce(new GeneratedAction(args.checkfunction(1)));
                    }
                    return NIL;
                }
                case NEWDAMAGEINFO: {
                    if (args.narg() == 2) {
                        return JavaClass.forClass(DamageInfo.class).getConstructor().invoke(args.arg(1), args.arg(2));
                    } else if (args.narg() == 3) {
                        return JavaClass.forClass(DamageInfo.class).getConstructor().invoke(args.arg(1), args.arg(2), CoerceJavaToLua.coerce(DamageInfo.DamageType.valueOf(args.tojstring(3))));
                    }
                }
                case ADDTOBOT: {
//                    CoerceJavaToLua.coerce(AbstractDungeon.actionManager).get("addToBottom").call(args.arg(1));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) CoerceLuaToJava.coerce(args.arg(1), AbstractGameAction.class));
                    return NONE;
                }
                case ADDTOTOP: {
//                    CoerceJavaToLua.coerce(AbstractDungeon.actionManager).get("addToTop").call(args.arg(1));
                    AbstractDungeon.actionManager.addToTop((AbstractGameAction) CoerceLuaToJava.coerce(args.arg(1), AbstractGameAction.class));
                    return NONE;
                }
                default:
                    throw new LuaError("not yet supported: " + this);
            }
        } catch (LuaError e) {
            throw e;
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }

    public static Varargs createCard(LuaValue id) {
        Varargs invoke = JavaClass.forClass(GeneratedCard.class).getConstructor().invoke(id);
        Utils.log(invoke.type(1));
        return invoke;
    }
}
