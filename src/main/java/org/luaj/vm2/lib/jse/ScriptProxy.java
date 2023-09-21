package org.luaj.vm2.lib.jse;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ScriptProxy extends VarArgFunction {
    static final int INIT = 0;
    static final int BINDCLASS = 1;
    static final int NEWINSTANCE = 2;
    static final int CREATEPROXY = 3;

    static final String[] NAMES = {
            "bind",
            "new",
            "proxy",
    };

    static final int METHOD_MODIFIERS_VARARGS = 0x80;

    public Varargs invoke(Varargs args) {
        try {
            switch (opcode) {
                case INIT: {
                    // LuaValue modname = args.arg1();
                    LuaValue env = args.arg(2);
                    LuaTable t = new LuaTable();
                    bind(t, this.getClass(), NAMES, BINDCLASS);
                    env.set("java", t);
                    if (!env.get("package").isnil()) env.get("package").get("loaded").set("java", t);
                    return t;
                }
                case BINDCLASS: {
                    final Class<?> clazz = classForName(args.checkjstring(1));
                    return JavaClass.forClass(clazz);
                }
                case NEWINSTANCE:{
                    // get constructor
                    final LuaValue c = args.checkvalue(1);
                    final Class<?> clazz = classForName(c.tojstring());
                    final Varargs consargs = args.subargs(2);
                    return JavaClass.forClass(clazz).getConstructor().invoke(consargs);
                }
                case CREATEPROXY: {
                    final int niface = args.narg() - 1;
                    if (niface <= 0)
                        throw new LuaError("no interfaces");
                    final LuaValue lobj = args.checktable(niface + 1);

                    // get the interfaces
                    final Class<?>[] ifaces = new Class[niface];
                    for (int i = 0; i < niface; i++)
                        ifaces[i] = classForName(args.checkjstring(i + 1));

                    // create the invocation handler
                    InvocationHandler handler = new ProxyInvocationHandler(lobj);

                    // create the proxy object
                    Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), ifaces, handler);

                    // return the proxy
                    return LuaValue.userdataOf(proxy);
                }
                default:
                    throw new LuaError("not yet supported: " + this);
            }
        } catch (LuaError e) {
            throw e;
        }catch (Exception e) {
            throw new LuaError(e);
        }
    }

    protected Class<?> classForName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    private static final class ProxyInvocationHandler implements InvocationHandler {
        private final LuaValue lobj;

        private ProxyInvocationHandler(LuaValue lobj) {
            this.lobj = lobj;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            LuaValue func = lobj.get(name);
            if (func.isnil())
                return null;
            boolean isvarargs = ((method.getModifiers() & METHOD_MODIFIERS_VARARGS) != 0);
            int n = args != null ? args.length : 0;
            LuaValue[] v;
            if (isvarargs) {
                Object o = null;
                if (args != null) {
                    o = args[--n];
                }
                int m = Array.getLength(o);
                v = new LuaValue[n + m];
                for (int i = 0; i < n; i++)
                    v[i] = CoerceJavaToLua.coerce(args[i]);
                for (int i = 0; i < m; i++)
                    v[i + n] = CoerceJavaToLua.coerce(Array.get(o, i));
            } else {
                v = new LuaValue[n];
                for (int i = 0; i < n; i++)
                    v[i] = CoerceJavaToLua.coerce(args[i]);
            }
            LuaValue result = func.invoke(v).arg1();
            return CoerceLuaToJava.coerce(result, method.getReturnType());
        }
    }
}
