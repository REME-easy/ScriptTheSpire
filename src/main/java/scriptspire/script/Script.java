package scriptspire.script;

import java.util.HashMap;

public class Script {
    public String id;
    public String code;
    public String path = "";

    public HashMap<String, AbstractScriptEffect> effects = new HashMap<>();

    public boolean oneTime;
    public boolean autoRun;

    public Script(String id, String code) {
        this.id = id;
        this.code = code;
    }

    public void onEnable() {
        effects.values().forEach(AbstractScriptEffect::onEnable);
    }

    public void onDisable() {
        effects.values().forEach(AbstractScriptEffect::onDisable);
    }
}