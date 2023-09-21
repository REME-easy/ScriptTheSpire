package scriptspire.event;

import scriptspire.modcore.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

public class EventHandler<T> implements IEventHandler {
    public String name;
    public HashSet<Consumer<T>> functions = new HashSet<>();

    public void addListener(Consumer<T> func) {
        functions.add(func);
    }

    public void removeListener(Consumer<T> func) {
        functions.remove(func);
    }

    public void publish(T arg) {
        ArrayList<Consumer<T>> toRemove = new ArrayList<>();
        for (Consumer<T> func : functions) {
            try {
                func.accept(arg);
            } catch (Exception ex) {
                Utils.error("%s: cannot execute a subscribe and it will be removed", name);
                Utils.log(ex.getMessage());
                Arrays.stream(ex.getStackTrace()).forEach(Utils::log);
                toRemove.add(func);
            }
        }

        toRemove.forEach(this::removeListener);
        Utils.log("publish event %s, size: %d", name, functions.size());
    }
}
