package scriptspire.event;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import scriptspire.event.basemod.*;
import scriptspire.event.builtin.*;
import scriptspire.modcore.Utils;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EventManager {
    private final HashMap<String, Class<? >> registeredEvents = new HashMap<>();
    private final HashMap<Class<?>, IEventHandler> handlers = new HashMap<>();

    public void initialize() {
        addEventHandler(ExecuteScriptEvent.class);
        addBuiltInInterfaces();
        addBaseModInterfaces();
//        subscribe(StartGameEvent.class, e -> Utils.log("game start"));
    }

    private void addBuiltInInterfaces() {
        addEventHandler(OnAddCardToHandEvent.class);
        addEventHandler(OnShuffleEvent.class);
        addEventHandler(PostOrbChannelEvent.class);
        addEventHandler(PostOrbEvokeEvent.class);
        addEventHandler(PreOrbEvokeEvent.class);
    }

    private void addBaseModInterfaces() {
        addEventHandler(OnCardUseEvent.class);
        addEventHandler(StartGameEvent.class);
        addEventHandler(PostInitializeEvent.class);
        addEventHandler(OnBattleStartEvent.class);
        addEventHandler(OnPlayerDamagedEvent.class);
        addEventHandler(OnPlayerLoseBlockEvent.class);
        addEventHandler(OnPlayerTurnStartEvent.class);
        addEventHandler(OnPlayerTurnStartPostDrawEvent.class);
        addEventHandler(OnPowersModifiedEvent.class);
        addEventHandler(PostBattleEvent.class);
        addEventHandler(PostCreateShopPotionEvent.class);
        addEventHandler(PostCreateShopRelicEvent.class);
        addEventHandler(PostCreateStartingDeckEvent.class);
        addEventHandler(PostDeathEvent.class);
        addEventHandler(PostDrawEvent.class);
        addEventHandler(PostDungeonInitializeEvent.class);
        addEventHandler(PostEnergyRechargeEvent.class);
        addEventHandler(PostExhaustEvent.class);
        addEventHandler(PostPotionUseEvent.class);
        addEventHandler(PostPowerApplyEvent.class);
        addEventHandler(PotionGetEvent.class);
        addEventHandler(PreMonsterTurnEvent.class);
        addEventHandler(PrePotionUseEvent.class);
        addEventHandler(PreStartGameEvent.class);
        addEventHandler(RelicGetEvent.class);
        addEventHandler(StartActEvent.class);
        addEventHandler(StartGameEvent.class);
    }

    public <T > void addEventHandler(Class<T> clz) {
        EventHandler<T> handler = new EventHandler<>();
        handler.name = clz.getSimpleName();
        registeredEvents.put(clz.getSimpleName().replace("Event", ""), clz);
        handlers.put(clz, handler);
    }

    public <T > void removeEventHandler(Class<T> clz) {
        if (!handlers.containsKey(clz)) {
            return;
        }
        registeredEvents.remove(clz.getSimpleName().replace("Event", ""));
        handlers.remove(clz);
    }

    public void clear() {
        handlers.clear();
    }

    public <T > Runnable subscribe(Class<T> clz, Consumer<T> func) {
        if (!handlers.containsKey(clz)) {
            addEventHandler(clz);
        }

        EventHandler<T> handler = (EventHandler<T>) handlers.get(clz);
        handler.addListener(func);

        return () -> unsubscribe(clz, func);
    }

    public Runnable subscribe(String name, LuaFunction func) {
        if (!registeredEvents.containsKey(name)) {
            registeredEvents.keySet().forEach(Utils::log);
            Utils.log("cannot subscribe %s", name);
            return () -> {
            };
        }

        Class<? > clz = registeredEvents.get(name);
        return subscribe(clz, e -> {
            func.call(CoerceJavaToLua.coerce(e));
        });
    }

    public <T > void unsubscribe(Class<T> clz, Consumer<T> func) {
        if (!handlers.containsKey(clz)) {
            return;
        }

        EventHandler<T> handler = (EventHandler<T>) handlers.get(clz);
        handler.removeListener(func);
    }

    public <T > void publish(Class<T> clz, T args) {
        if (!handlers.containsKey(clz)) {
            addEventHandler(clz);
            return;
        }

        EventHandler<T> handler = (EventHandler<T>) handlers.get(clz);
        handler.publish(args);
    }
}
