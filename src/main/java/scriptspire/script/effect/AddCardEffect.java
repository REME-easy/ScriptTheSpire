package scriptspire.script.effect;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.EverythingFix;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import scriptspire.card.GeneratedCard;
import scriptspire.script.AbstractScriptEffect;

public class AddCardEffect extends AbstractScriptEffect {
    public String id;
    public GeneratedCard card;

    public AddCardEffect(String id) {
        this.id  = id;
    }

    @Override
    public void onEnable() {
        card.cardID = id;
        CardLibrary.add(card);
        CardGroup group = EverythingFix.Fields.cardGroupMap.get(card.color);
        if (group != null) {
            group.addToBottom(card);
        }
    }

    @Override
    public void onDisable() {
        CardLibrary.cards.remove(id);
        CardGroup group = EverythingFix.Fields.cardGroupMap.get(card.color);
        if (group != null) {
            group.removeCard(card);
        }

        switch (card.color) {
            case RED:
                CardLibrary.redCards--;
                break;
            case GREEN:
                CardLibrary.greenCards--;
                break;
            case BLUE:
                CardLibrary.blueCards--;
                break;
            case PURPLE:
                CardLibrary.purpleCards--;
                break;
            case COLORLESS:
                CardLibrary.colorlessCards--;
                break;
            case CURSE:
                CardLibrary.curseCards--;
                break;
            default:
                BaseMod.decrementCardCount(card.color);
        }
        CardLibrary.totalCardCount--;
    }
}
