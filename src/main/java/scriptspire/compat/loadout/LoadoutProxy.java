package scriptspire.compat.loadout;

import org.luaj.vm2.LuaValue;
import scriptspire.script.CustomLib;

public class LoadoutProxy extends CustomLib {
    @Override
    public String getLibName() {
        return "loadout";
    }

    @Override
    public void addMethods(LuaValue env) {

    }
}
