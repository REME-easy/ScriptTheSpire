package scriptspire.script.effect;

import org.luaj.vm2.LuaValue;
import scriptspire.modcore.ScriptTheSpire;
import scriptspire.script.AbstractScriptEffect;

import java.util.ArrayList;

public class DevCommandEffect extends AbstractScriptEffect {

    public String name;
    public ArrayList<RequiredValue> requiredValues = new ArrayList<>();
    public LuaValue action;

    public DevCommandEffect(String name) {
        this.name = name;
    }

    @Override
    public void onEnable() {
        ScriptTheSpire.SCRIPT.devCommands.put(name, this);
    }

    @Override
    public void onDisable() {
        ScriptTheSpire.SCRIPT.devCommands.remove(name);
    }

    public static class RequiredValue {
        public String name;
        public String clz;
        public LuaValue defaultValue;

        public RequiredValue(String name, String clz, LuaValue defaultValue) {
            this.name = name;
            this.clz = clz;
            this.defaultValue = defaultValue;
        }
    }
}
