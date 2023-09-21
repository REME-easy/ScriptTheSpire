package scriptspire.script.proxy;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import scriptspire.modcore.ScriptTheSpire;
import scriptspire.script.effect.DevCommandEffect;
import scriptspire.script.effect.DevCommandEffect.RequiredValue;

public class AddCommand extends TwoArgFunction {
    public static DevCommandEffect CURRENT;

    @Override
    public LuaValue call(LuaValue name, LuaValue func) {
        CURRENT = new DevCommandEffect(name.tojstring());
        CURRENT.action = func.call(new CommandBuilder());
        ScriptTheSpire.SCRIPT.currentScript.effects.put(String.format("command:%s", name.tojstring()), CURRENT);
        CURRENT = null;
        return NONE;
    }

    static class CommandBuilder extends LuaTable {
        public CommandBuilder() {
            super();
            set("addArg", new addArg());
        }
    }

    static class addArg extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue name, LuaValue type, LuaValue defaultValue) {
            if (!name.isstring() || !type.isstring()) {
                return NONE;
            }

            RequiredValue requiredValue = new RequiredValue(name.tojstring(), type.tojstring(), defaultValue);
            CURRENT.requiredValues.add(requiredValue);
            return NONE;
        }
    }
}
