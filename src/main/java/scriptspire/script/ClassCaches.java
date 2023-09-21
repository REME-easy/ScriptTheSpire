package scriptspire.script;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.clapper.util.classutil.*;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.*;

public class ClassCaches {
    public ClassFinder finder;
    private final List<ClassFilter> filters = new ArrayList<>();
    private final ClassPool pool;

    private final Map<String, Class<?>> actions = new HashMap<>();

    public ClassCaches() throws CannotCompileException, URISyntaxException, ClassNotFoundException {
        this.finder = new ClassFinder();
        this.pool = Loader.getClassPool();
        this.finder.add(new File(Loader.STS_JAR));
        for (ModInfo info : Loader.MODINFOS) {
            this.finder.add(new File(info.jarURL.toURI()));
        }

        this.filters.add(new AutoAdd.NotPackageFilter("org.apache.logging.log4j"));

        Collection<CtClass> actionClasses = findClasses(AbstractGameAction.class);
        for (CtClass ctClass : actionClasses) {
            String name = ctClass.getSimpleName();
            if (actions.containsKey(name)) continue;
            actions.put(name, this.pool.getClassLoader().loadClass(ctClass.getName()));
        }
    }

    public Class<?> getAction(String id) {
        return actions.get(id);
    }

    private  <T> Collection<CtClass> findClasses(Class<T> type) {
        try {
            List<ClassFilter> tmp = new ArrayList<>();
            tmp.addAll(Arrays.asList(
                    new NotClassFilter(new InterfaceOnlyClassFilter()),
                    new NotClassFilter(new AbstractClassFilter()),
                    new ClassModifiersClassFilter(Modifier.PUBLIC)
            ));
            tmp.addAll(filters);
            ClassFilter filter = new AndClassFilter(tmp.toArray(new ClassFilter[0]));
            Collection<ClassInfo> foundClasses = new ArrayList<>();
            finder.findClasses(foundClasses, filter);

            Collection<CtClass> ret = new ArrayList<>();
            for (ClassInfo classInfo : foundClasses) {
//                Utils.log("try to find %s", classInfo.getClassName());
                CtClass ctClass = pool.get(classInfo.getClassName());

                CtClass ctSuperClass = ctClass;
                do {
                    if (ctSuperClass.getName().equals(type.getName())) {
                        ret.add(ctClass);
                        break;
                    }
                    ctSuperClass = ctSuperClass.getSuperclass();
                } while (ctSuperClass != null);
            }

            return ret;
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
