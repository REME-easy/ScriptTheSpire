package scriptspire.compat.loadout;

import org.luaj.vm2.Globals;

public class LoadoutUtils {
    public static void install(Globals env) {
        env.load(new LoadoutProxy());
    }
}
