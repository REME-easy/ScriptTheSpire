package scriptspire.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import scriptspire.utils.TriConsumer;

import java.util.function.Consumer;

public class GeneratedCard extends CustomCard {
    private CardStrings cardStrings;
    public int maxUpgradeTime = 1;

    private Consumer<LuaValue> onUpgrade = a -> {
    };
    private TriConsumer<LuaValue, LuaValue, LuaValue> onUse = (a, b, c) -> {
    };
    private Consumer<LuaValue> onTookDamage = a -> {
    };
    private Consumer<LuaValue> onDrawn = a -> {
    };
    private Consumer<LuaValue> onManualDiscard = a -> {
    };
    private Consumer<LuaValue> onRetained = a -> {
    };
    private Consumer<LuaValue> onScry = a -> {
    };
    private Consumer<LuaValue> onExhausted = a -> {
    };
    private Consumer<LuaValue> onApplyPower;

    public GeneratedCard(String id) {
        super(id, "", (String) null, -1, "", CardType.SKILL, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.NONE);
    }

    public void setLocale(String name, String desc, String upgradeDesc, String[] ext) {
        this.cardStrings = new CardStrings();
        this.cardStrings.NAME = name;
        this.cardStrings.DESCRIPTION = desc;
        this.cardStrings.UPGRADE_DESCRIPTION = desc;
        this.cardStrings.EXTENDED_DESCRIPTION = ext;
    }

    public void setCardType(String type) {
        this.type = CardType.valueOf(type);
    }

    public void setCardColor(String color) {
        this.color = CardColor.valueOf(color);
    }

    public void setCardRarity(String rarity) {
        this.rarity = CardRarity.valueOf(rarity);
    }

    public void setCardTarget(String target) {
        this.target = CardTarget.valueOf(target);
    }

    public void setOnUpgrade(LuaFunction onUpgrade) {
        this.onUpgrade = onUpgrade::call;
    }

    public void setOnUse(LuaFunction onUse) {
        this.onUse = onUse::call;
    }

    public void setOnTookDamage(LuaFunction onTookDamage) {
        this.onTookDamage = onTookDamage::call;
    }

    public void setOnDrawn(LuaFunction onDrawn) {
        this.onDrawn = onDrawn::call;
    }

    public void setOnManualDiscard(LuaFunction onManualDiscard) {
        this.onManualDiscard = onManualDiscard::call;
    }

    public void setOnRetained(LuaFunction onRetained) {
        this.onRetained = onRetained::call;
    }

    public void setOnScry(LuaFunction onScry) {
        this.onScry = onScry::call;
    }

    public void setOnExhausted(LuaFunction onExhausted) {
        this.onExhausted = onExhausted::call;
    }

    public void setOnApplyPower(LuaFunction onApplyPower) {
        this.onApplyPower = onApplyPower::call;
    }

    @Override
    public void upgrade() {
        if (timesUpgraded < maxUpgradeTime) {
            this.timesUpgraded++;
            this.upgraded = true;
            this.name = this.maxUpgradeTime == 1 ? String.format("%s+", cardStrings.NAME) : String.format("%s+%d", cardStrings.NAME, timesUpgraded);
            onUpgrade.accept(CoerceJavaToLua.coerce(this));
            if (cardStrings.UPGRADE_DESCRIPTION.isEmpty()) return;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        onUse.accept(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(p), CoerceJavaToLua.coerce(m));
    }

    @Override
    public void applyPowers() {
        if (onApplyPower != null) {
            onApplyPower.accept(CoerceJavaToLua.coerce(this));
            return;
        }

        super.applyPowers();
    }

    @Override
    public void tookDamage() {
        onTookDamage.accept(CoerceJavaToLua.coerce(this));
    }

    @Override
    public void triggerWhenDrawn() {
        onDrawn.accept(CoerceJavaToLua.coerce(this));
    }

    @Override
    public void triggerOnManualDiscard() {
        onManualDiscard.accept(CoerceJavaToLua.coerce(this));
    }

    @Override
    public void triggerOnScry() {
        onScry.accept(CoerceJavaToLua.coerce(this));
    }

    @Override
    public void onRetained() {
        onRetained.accept(CoerceJavaToLua.coerce(this));
    }

    @Override
    public void triggerOnExhaust() {
        onExhausted.accept(CoerceJavaToLua.coerce(this));
    }

    @Override
    public AbstractCard makeCopy() {
        GeneratedCard card = new GeneratedCard(cardID);
        card.cardID = cardID;
        card.baseDamage = card.damage = baseDamage;
        card.baseBlock = card.block = baseBlock;
        card.baseMagicNumber = card.magicNumber = magicNumber;
        card.exhaust = exhaust;
        card.selfRetain = selfRetain;
        card.name = name;
        card.rawDescription = rawDescription;
        card.type = type;
        card.rarity = rarity;
        card.target = target;
        card.color = color;
        card.onExhausted = onExhausted;
        card.onRetained = onRetained;
        card.onScry = onScry;
        card.onDrawn = onDrawn;
        card.onUpgrade = onUpgrade;
        card.onApplyPower = onApplyPower;
        card.onManualDiscard = onManualDiscard;
        card.onTookDamage = onTookDamage;
        card.cardStrings = cardStrings;
        card.initializeDescription();
        card.initializeTitle();
        return card;
    }
}