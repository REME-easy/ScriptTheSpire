package scriptspire.script.proxy;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import scriptspire.modcore.ScriptTheSpire;
import scriptspire.script.Script;
import scriptspire.script.effect.SubscribeEventEffect;

public class AddListener extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue event, LuaValue func) {
        Script script = ScriptTheSpire.SCRIPT.currentScript;
        SubscribeEventEffect effect = new SubscribeEventEffect(event.tojstring(), func);
        script.effects.put(String.format("listener:%s", event.tojstring()), effect);
        return NONE;
    }
}
