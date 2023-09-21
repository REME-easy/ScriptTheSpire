package scriptspire.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import org.luaj.vm2.LuaFunction;

public class GeneratedAction extends AbstractGameAction {
    public LuaFunction function;

    public GeneratedAction(LuaFunction function) {
        this.function = function;
    }

    @Override
    public void update() {
        this.function.call();
        this.isDone = true;
    }
}
