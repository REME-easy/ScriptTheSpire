package scriptspire.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import javassist.CtBehavior;
import scriptspire.event.builtin.*;
import scriptspire.modcore.ScriptTheSpire;

public class EventsPatch {
    @SpirePatches({
            @SpirePatch(clz = CardGroup.class, method = "addToTop"),
            @SpirePatch(clz = CardGroup.class, method = "addToBottom"),
            @SpirePatch(clz = CardGroup.class, method = "addToHand"),
            @SpirePatch(clz = CardGroup.class, method = "addToRandomSpot")
    })
    public static class AddToHandPatch {
        public static void Postfix(CardGroup $inst, AbstractCard c) {
            OnAddCardToHandEvent event = new OnAddCardToHandEvent();
            event.card = c;
            ScriptTheSpire.EVENT.publish(OnAddCardToHandEvent.class, event);
        }
    }

    public static void OnShuffle() {
        ScriptTheSpire.EVENT.publish(OnShuffleEvent.class, new OnShuffleEvent());
    }

    @SpirePatch(clz = ShuffleAllAction.class, method = "<ctor>")
    public static class OnShufflePatch1 {
        public static void Postfix(ShuffleAllAction $inst) {
            OnShuffle();
        }
    }

    @SpirePatch(clz = EmptyDeckShuffleAction.class, method = "<ctor>")
    public static class OnShufflePatch2 {
        public static void Postfix(EmptyDeckShuffleAction $inst) {
            OnShuffle();
        }
    }

    @SpirePatch(clz = ShuffleAction.class, method = "<ctor>", paramtypez = { CardGroup.class, boolean.class })
    public static class OnShufflePatch3 {
        public static void Postfix(ShuffleAction $inst, CardGroup theGroup, boolean trigger) {
            if (trigger) {
                OnShuffle();
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    public static class onOrbChannelPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractPlayer $inst, AbstractOrb orbToSet) {
            PostOrbChannelEvent event = new PostOrbChannelEvent();
            event.orb = orbToSet;
            ScriptTheSpire.EVENT.publish(PostOrbChannelEvent.class, event);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class,
                        "applyFocus");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "evokeOrb")
    public static class onOrbEvokePatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert1(AbstractPlayer $inst) {
            PreOrbEvokeEvent event = new PreOrbEvokeEvent();
            event.orb = $inst.orbs.get(0);
            ScriptTheSpire.EVENT.publish(PreOrbEvokeEvent.class, event);
        }

        @SpireInsertPatch(rloc = 9)
        public static void Insert2(AbstractPlayer $inst) {
            PostOrbEvokeEvent event = new PostOrbEvokeEvent();
            event.orb = $inst.orbs.get(0);
            ScriptTheSpire.EVENT.publish(PostOrbEvokeEvent.class, event);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class,
                        "onEvoke");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
}
