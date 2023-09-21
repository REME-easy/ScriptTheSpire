package scriptspire.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public abstract class CustomLib extends TwoArgFunction {
    public abstract String getLibName();

    public abstract void addMethods(LuaValue env);

    @Override
    public LuaValue call(LuaValue mod, LuaValue env) {
        LuaValue library = new LuaTable();
        addMethods(library);
        env.set(getLibName(), library);
        env.get("package").get("loaded").set(getLibName(), library);
        return library;
    }

    protected void addMethod(LuaValue env, Class<? extends LuaValue> clz) {
        try {
            env.set(clz.getSimpleName(), clz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
