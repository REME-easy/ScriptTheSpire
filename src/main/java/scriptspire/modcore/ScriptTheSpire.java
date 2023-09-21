package scriptspire.modcore;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scriptspire.event.EventManager;
import scriptspire.event.basemod.*;
import scriptspire.script.ScriptManager;

import java.util.ArrayList;

@SpireInitializer
public class ScriptTheSpire implements PostInitializeSubscriber, ImGuiSubscriber, OnCardUseSubscriber, StartGameSubscriber,
        OnPlayerTurnStartSubscriber, OnPlayerDamagedSubscriber, OnPlayerLoseBlockSubscriber,
        OnPlayerTurnStartPostDrawSubscriber, OnPowersModifiedSubscriber, OnStartBattleSubscriber, PostBattleSubscriber,
        PostCampfireSubscriber, PostCreateShopPotionSubscriber, PostCreateShopRelicSubscriber,
        PostCreateStartingDeckSubscriber, PostDeathSubscriber, PostDrawSubscriber, PostDungeonInitializeSubscriber,
        PostEnergyRechargeSubscriber, PostExhaustSubscriber, PostPotionUseSubscriber, PostPowerApplySubscriber,
        PreMonsterTurnSubscriber, PrePotionUseSubscriber, PreStartGameSubscriber, PotionGetSubscriber,
        RelicGetSubscriber, StartActSubscriber {

    public static final ScriptManager SCRIPT = new ScriptManager();
    public static final EventManager EVENT = new EventManager();
    public static final ScriptPanel PANEL = new ScriptPanel();

    public static final Logger LOGGER = LogManager.getLogger(ScriptTheSpire.class);

    public static void initialize() {
        ScriptTheSpire mod = new ScriptTheSpire();
        BaseMod.subscribe(mod);
    }

    @Override
    public void receivePostInitialize() {
        EVENT.initialize();
        SCRIPT.initialize();
        PANEL.initialize();
        EVENT.publish(PostInitializeEvent.class, new PostInitializeEvent());
    }

    @Override
    public void receiveImGui() {
        PANEL.receiveImGui();
    }

    @Override
    public void receiveCardUsed(AbstractCard card) {
        OnCardUseEvent event = new OnCardUseEvent();
        event.card = card;
        EVENT.publish(OnCardUseEvent.class, event);
    }

    @Override
    public void receiveStartGame() {
        EVENT.publish(StartGameEvent.class, new StartGameEvent());
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        EVENT.publish(OnPlayerTurnStartEvent.class, new OnPlayerTurnStartEvent());
    }

    @Override
    public void receivePostCreateStartingDeck(AbstractPlayer.PlayerClass playerClass, CardGroup cardGroup) {
        PostCreateStartingDeckEvent event = new PostCreateStartingDeckEvent();
        event.playerClass = playerClass;
        event.cardGroup = cardGroup;
        EVENT.publish(PostCreateStartingDeckEvent.class, event);
    }

    @Override
    public int receiveOnPlayerDamaged(int i, DamageInfo damageInfo) {
        OnPlayerDamagedEvent event = new OnPlayerDamagedEvent();
        event.amount = i;
        event.damageInfo = damageInfo;
        EVENT.publish(OnPlayerDamagedEvent.class, event);
        return event.amount;
    }

    @Override
    public int receiveOnPlayerLoseBlock(int i) {
        OnPlayerLoseBlockEvent event = new OnPlayerLoseBlockEvent();
        event.amount = i;
        EVENT.publish(OnPlayerLoseBlockEvent.class, event);
        return event.amount;
    }

    @Override
    public void receiveOnPlayerTurnStartPostDraw() {
        EVENT.publish(OnPlayerTurnStartPostDrawEvent.class, new OnPlayerTurnStartPostDrawEvent());
    }

    @Override
    public void receivePowersModified() {
        EVENT.publish(OnPowersModifiedEvent.class, new OnPowersModifiedEvent());
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        AbstractCard specialStrike = CardLibrary.getCard("SpecialStrike");
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(specialStrike));
        OnBattleStartEvent event = new OnBattleStartEvent();
        event.room = abstractRoom;
        EVENT.publish(OnBattleStartEvent.class, event);
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        PostBattleEvent event = new PostBattleEvent();
        event.room = abstractRoom;
        EVENT.publish(PostBattleEvent.class, event);
    }

    @Override
    public boolean receivePostCampfire() {
        return false;
    }

    @Override
    public void receiveCreateShopPotions(ArrayList<StorePotion> arrayList, ShopScreen shopScreen) {
        PostCreateShopPotionEvent event = new PostCreateShopPotionEvent();
        event.potions = arrayList;
        event.shopScreen = shopScreen;
        EVENT.publish(PostCreateShopPotionEvent.class, event);
    }

    @Override
    public void receiveCreateShopRelics(ArrayList<StoreRelic> arrayList, ShopScreen shopScreen) {
        PostCreateShopRelicEvent event = new PostCreateShopRelicEvent();
        event.relics = arrayList;
        event.shopScreen = shopScreen;
        EVENT.publish(PostCreateShopRelicEvent.class, event);
    }

    @Override
    public void receivePostDeath() {
        EVENT.publish(PostDeathEvent.class, new PostDeathEvent());
    }

    @Override
    public void receivePostDraw(AbstractCard abstractCard) {
        PostDrawEvent event = new PostDrawEvent();
        event.card = abstractCard;
        EVENT.publish(PostDrawEvent.class, event);
    }

    @Override
    public void receivePostDungeonInitialize() {
        EVENT.publish(PostDungeonInitializeEvent.class, new PostDungeonInitializeEvent());
    }

    @Override
    public void receivePostEnergyRecharge() {
        EVENT.publish(PostEnergyRechargeEvent.class, new PostEnergyRechargeEvent());
    }

    @Override
    public void receivePostExhaust(AbstractCard abstractCard) {
        PostExhaustEvent event = new PostExhaustEvent();
        event.card = abstractCard;
        EVENT.publish(PostExhaustEvent.class, event);
    }

    @Override
    public void receivePostPotionUse(AbstractPotion abstractPotion) {
        PostPotionUseEvent event = new PostPotionUseEvent();
        event.potion = abstractPotion;
        EVENT.publish(PostPotionUseEvent.class, event);
    }

    @Override
    public void receivePostPowerApplySubscriber(AbstractPower abstractPower, AbstractCreature abstractCreature, AbstractCreature abstractCreature1) {
        PostPowerApplyEvent event = new PostPowerApplyEvent();
        event.power = abstractPower;
        event.target = abstractCreature;
        event.source = abstractCreature1;
        EVENT.publish(PostPowerApplyEvent.class, event);
    }

    @Override
    public void receivePotionGet(AbstractPotion abstractPotion) {
        PotionGetEvent event = new PotionGetEvent();
        event.potion = abstractPotion;
        EVENT.publish(PotionGetEvent.class, event);
    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster abstractMonster) {
        PreMonsterTurnEvent event = new PreMonsterTurnEvent();
        event.monster = abstractMonster;
        EVENT.publish(PreMonsterTurnEvent.class, event);
        return event.takeTurn;
    }

    @Override
    public void receivePrePotionUse(AbstractPotion abstractPotion) {
        PrePotionUseEvent event = new PrePotionUseEvent();
        event.potion = abstractPotion;
        EVENT.publish(PrePotionUseEvent.class, event);
    }

    @Override
    public void receivePreStartGame() {
        EVENT.publish(PreStartGameEvent.class, new PreStartGameEvent());
    }

    @Override
    public void receiveRelicGet(AbstractRelic abstractRelic) {
        RelicGetEvent event = new RelicGetEvent();
        event.relic = abstractRelic;
        EVENT.publish(RelicGetEvent.class, event);
    }

    @Override
    public void receiveStartAct() {
        EVENT.publish(StartActEvent.class, new StartActEvent());
    }
}