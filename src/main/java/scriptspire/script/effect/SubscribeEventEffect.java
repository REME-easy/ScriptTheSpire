package scriptspire.script.effect;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import scriptspire.modcore.ScriptTheSpire;
import scriptspire.script.AbstractScriptEffect;

public class SubscribeEventEffect extends AbstractScriptEffect {
    public String event;
    public LuaValue action;

    public Runnable unsubscriber;

    public SubscribeEventEffect(String event, LuaValue action) {
        this.event = event;
        this.action = action;
    }

    @Override
    public void onEnable() {
        unsubscriber = ScriptTheSpire.EVENT.subscribe(event, (LuaFunction) action);
    }

    @Override
    public void onDisable() {
        unsubscriber.run();
    }
}
