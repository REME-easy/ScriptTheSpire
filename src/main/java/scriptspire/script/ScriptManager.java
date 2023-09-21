package scriptspire.script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.*;
import scriptspire.event.ExecuteScriptEvent;
import scriptspire.modcore.ScriptTheSpire;
import scriptspire.modcore.Utils;
import scriptspire.script.effect.DevCommandEffect;
import scriptspire.script.proxy.AddListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ScriptManager {
    public HashMap<String, Script> allScripts = new HashMap<>();
    public HashMap<String, DevCommandEffect> devCommands = new HashMap<>();

    public Globals env;
    public Script currentScript;
    public FileAlterationObserver observer;
    public FileAlterationMonitor monitor;
    public boolean isLoaded = false;

    public void initialize() {
        env = JsePlatform.standardGlobals();
        LoadState.install(env);
        LuaC.install(env);
        loadAll();
        isLoaded = true;
    }

    public void reload(File file) {
        try {
            String id = FilenameUtils.removeExtension(file.getName());
            String code = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (!allScripts.containsKey(id)) return;
            Script newScript = parseScript(id, code);
            if (newScript == null) return;
            Script script = allScripts.get(id);
            Utils.log("reload script %s", script.path);
            script.onDisable();
            newScript.onEnable();
            newScript.path = script.path;
            allScripts.replace(id, newScript);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNewScript(File file) {
        try {
            String id = FilenameUtils.removeExtension(file.getName());
            String code = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            Script script = parseScript(id, code);
            if (script == null) return;
            script.onEnable();
            script.path = file.getPath();
            allScripts.put(id, script);
            Utils.log("add script: %s", id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNewScript(String name, String code) {
        try {
            String path = System.getProperty("user.dir");
            String modPath = Paths.get(path, "mods", "scripts").toString();
            String p = Paths.get(modPath, String.format("%s.lua", name)).toString();
            File file = new File(p);
            if (file.createNewFile()) {
                FileUtils.writeByteArrayToFile(file, code.getBytes(StandardCharsets.UTF_8));
            }

            Script script = parseScript(name, code);
            if (script == null) return;
            script.onEnable();
            script.path = file.getPath();
            allScripts.put(name, script);
            Utils.log("add script: %s", name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(File file) {
        String id = FilenameUtils.removeExtension(file.getName());
        Script script = allScripts.get(id);
        if (script == null) return;
        script.onDisable();
        allScripts.remove(id);
    }

    public void loadAll() {
        try {
            // load custom scripts
            String path = System.getProperty("user.dir");
            String modPath = Paths.get(path, "mods", "scripts").toString();
            File dir = new File(modPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) return;

                // create built-in scripts
                String[] builtin = new String[]{
                        "gainGold", "playCardTrigger", "unlockAscension", "addTestCard"
                };

                for (String p : builtin) {
                    FileHandle handle = Gdx.files.internal(String.format("ScriptTheSpire/scripts/builtin/%s.lua", p));
                    String name = handle.nameWithoutExtension();
                    String code = handle.readString();
                    String newPath = Paths.get(modPath, String.format("%s.lua", name)).toString();
                    File file = new File(newPath);
                    Utils.log(newPath);
                    if (!file.createNewFile()) continue;
                    FileUtils.writeByteArrayToFile(file, code.getBytes(StandardCharsets.UTF_8));
                }
            }

            IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".lua");
            observer = new FileAlterationObserver(dir, fileFilter);
            observer.addListener(new ScriptFileListener());
            monitor = new FileAlterationMonitor(TimeUnit.SECONDS.toMillis(5), observer);
            monitor.start();

            String definitionPath = Paths.get(modPath, "definition.lua").toString();
            File definition = new File(definitionPath);
            if (!definition.exists()) {
                if (!definition.createNewFile()) return;
            }
            FileUtils.writeByteArrayToFile(definition, Gdx.files.internal("ScriptTheSpire/definition.lua").readBytes());

            for (File custom : FileUtils.listFiles(dir, new String[]{"lua"}, true)) {
                String name = FilenameUtils.removeExtension(custom.getName());
                if ("definition".equals(name)) continue;
                Script script = parseScript(name, FileUtils.readFileToString(custom, StandardCharsets.UTF_8));
                if (script == null) continue;
                script.onEnable();
                script.path = custom.getPath();
                allScripts.put(name, script);
                Utils.log("add script: %s", name);
            }
        } catch (IOException exception) {
            Utils.error(exception.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Script parseScript(String name, String code) {
        currentScript = new Script(name, code);
        if (!executeScript(code)) {
            currentScript = null;
            Utils.error("cannot parse script %s", name);
            return null;
        }

        Script res = currentScript;
        currentScript = null;
        return res;
    }

    public boolean executeScript(String code) {
        Globals userEnv = new Globals();
        userEnv.load(new JseBaseLib());
        userEnv.load(new PackageLib());
        userEnv.load(new Bit32Lib());
        userEnv.load(new TableLib());
        userEnv.load(new StringLib());
        userEnv.load(new JseMathLib());
        userEnv.load(new ScriptProxy());
        userEnv.load(new StsProxy());
//        userEnv.set("addCard", new AddCard());
//        userEnv.set("addCommand", new AddCommand());
        userEnv.set("addListener", new AddListener());

        ExecuteScriptEvent event = new ExecuteScriptEvent();
        event.userEnv = userEnv;
        ScriptTheSpire.EVENT.publish(ExecuteScriptEvent.class, event);

//        if (Loader.isModLoaded("loadout")) {
//            LoadoutUtils.install(userEnv);
//        }

        try {
            env.load(code, "main", userEnv).call();
            return true;
        } catch (Exception ex) {
            Utils.error("exception caught in: \n %s", code);
            Utils.log(ex.getMessage());
            Arrays.stream(ex.getStackTrace()).forEach(e -> Utils.log(e.toString()));
            return false;
        }
    }

    static class ScriptFileListener implements FileAlterationListener {

        @Override
        public void onDirectoryChange(File file) {

        }

        @Override
        public void onDirectoryCreate(File file) {

        }

        @Override
        public void onDirectoryDelete(File file) {

        }

        @Override
        public void onFileChange(File file) {
            if (!ScriptTheSpire.SCRIPT.isLoaded) return;
            Utils.log("script %s changed", file.getName());
            ScriptTheSpire.SCRIPT.reload(file);
        }

        @Override
        public void onFileCreate(File file) {
            if (!ScriptTheSpire.SCRIPT.isLoaded) return;
            Utils.log("script %s created", file.getName());
            ScriptTheSpire.SCRIPT.createNewScript(file);
        }

        @Override
        public void onFileDelete(File file) {
            if (!ScriptTheSpire.SCRIPT.isLoaded) return;
            Utils.log("script %s deleted", file.getName());
            ScriptTheSpire.SCRIPT.delete(file);
        }

        @Override
        public void onStart(FileAlterationObserver fileAlterationObserver) {

        }

        @Override
        public void onStop(FileAlterationObserver fileAlterationObserver) {

        }
    }
}