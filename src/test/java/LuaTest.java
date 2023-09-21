import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;

public class LuaTest {
    public static <T> void assertEquals(T a, T b) {
        Assertions.assertEquals(a, b);
    }

    public static void log(Object... args) {
        for (Object arg : args)
            System.out.println(arg);
    }

    @Test
    public void simpleTest() {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load("print(2) return 3");
        log(chunk.call());
    }

    @Test
    public void wrapperTest() {
        Globals globals = JsePlatform.standardGlobals();

        globals.set("A", new A());
        globals.set("C", CoerceJavaToLua.coerce(new C()));
//        String code = "clz = luajava.bindClass('java.lang.Integer') print(clz) A(clz)";
//        globals.load(code).call();
    }

    public static class A extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            log(varargs.narg());
            return NIL;
        }
    }

    public static class C {
        public ArrayList<String> list = new ArrayList<>();

        public C() {
            list.add("a");
            list.add("b");
            list.add("c");
            list.add("d");
        }

        public static void test1(String a) {
            log(a);
        }

        public static void test2(String[] args) {
            for (String arg : args) {
                log(arg);
            }
        }

        public static void test3(LuaFunction func) {
            func.call(LuaValue.valueOf(3));
        }
    }

    public static class D {
        public int val = 5;
    }
}
