package scriptspire.modcore;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import scriptspire.script.AbstractScriptEffect;
import scriptspire.script.Script;
import scriptspire.script.effect.DevCommandEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class ScriptPanel {
    public ImString scriptContent;
    public ImString scriptName;
    public String selectedScript = "";
    public String selectedCommand = "";
    public HashMap<String, Object> variables = new HashMap<>();

    public void initialize() {
        ArrayList<String> scripts = new ArrayList<>(ScriptTheSpire.SCRIPT.allScripts.keySet());
        Collections.sort(scripts);
        selectedScript = scripts.get(0);
        scriptContent = new ImString(ScriptTheSpire.SCRIPT.allScripts.get(selectedScript).code);
        scriptName = new ImString();

        ArrayList<String> cmds = new ArrayList<>(ScriptTheSpire.SCRIPT.devCommands.keySet());
        Collections.sort(cmds);
        if (!cmds.isEmpty())
            selectedCommand = cmds.get(0);
    }

    public void receiveImGui() {
        scriptWindow();
        consoleWindow();
    }

    private void scriptWindow() {
        if (ImGui.begin("Scripts")) {

            int flags = ImGuiComboFlags.PopupAlignLeft;
            if (ImGui.beginCombo("script", selectedScript, flags)) {
                for (String script : ScriptTheSpire.SCRIPT.allScripts.keySet()) {
                    boolean isSelected = selectedScript.equals(script);
                    if (ImGui.selectable(script, isSelected)) {
                        selectedScript = script;
                        Script s = ScriptTheSpire.SCRIPT.allScripts.get(selectedScript);
                        scriptContent = new ImString(s.code);
                        Optional<AbstractScriptEffect> cmd = s.effects.values().stream().filter(e -> e instanceof DevCommandEffect).findFirst();
                        cmd.ifPresent(abstractScriptEffect -> selectedCommand = ((DevCommandEffect) abstractScriptEffect).name);
                        Utils.log("select " + script);
                    }
                    if (isSelected) {
                        ImGui.setItemDefaultFocus();
                    }
                }
                ImGui.endCombo();
            }

            ImGui.separator();

            Script currentScript = ScriptTheSpire.SCRIPT.allScripts.get(selectedScript);

            float x = ImGui.getCursorPosX();
            float y = ImGui.getCursorPosY();
            float w = ImGui.getWindowWidth();
            float h = ImGui.getWindowHeight();
            float px = ImGui.getStyle().getWindowPaddingX();
            float py = ImGui.getStyle().getWindowPaddingY();
            float lh = ImGui.getFrameHeightWithSpacing();

            ImVec2 size = new ImVec2();
            ImGui.calcTextSize(size, scriptContent.get());

            int flags2 = ImGuiWindowFlags.HorizontalScrollbar | ImGuiWindowFlags.AlwaysVerticalScrollbar;
            int flags3 = ImGuiInputTextFlags.AllowTabInput | ImGuiInputTextFlags.CtrlEnterForNewLine | ImGuiInputTextFlags.ReadOnly;
            ImGui.beginChild("code", w - x - px, h - y - py - lh * 2, false, flags2);
            ImGui.inputTextMultiline("##code", scriptContent, Math.max(size.x + 50.0f, w), h - y - py - lh, flags3);
            ImGui.endChild();

            if (ImGui.button("delete")) {
                Script script = ScriptTheSpire.SCRIPT.allScripts.get(selectedScript);
                ScriptTheSpire.SCRIPT.delete(new File(script.path));
                Utils.log("deleted");
            }

            ImGui.inputText("script name", scriptName);
            ImGui.sameLine();

            if (!scriptName.isEmpty() && ImGui.button("new")) {
                ScriptTheSpire.SCRIPT.createNewScript(scriptName.get(), String.valueOf(scriptContent.get()));

                selectedScript = scriptName.get();
                Script s = ScriptTheSpire.SCRIPT.allScripts.get(selectedScript);
                scriptContent = new ImString(s.code);
                Optional<AbstractScriptEffect> cmd = s.effects.values().stream().filter(e -> e instanceof DevCommandEffect).findFirst();
                cmd.ifPresent(abstractScriptEffect -> selectedCommand = ((DevCommandEffect) abstractScriptEffect).name);
                Utils.log("select " + selectedScript);
                scriptName.set("");
            }
        }
        ImGui.end();
    }

    private void consoleWindow() {
        if (ImGui.begin("console")) {
            if (ImGui.beginCombo("commands", selectedCommand)) {
                for (DevCommandEffect cmd : ScriptTheSpire.SCRIPT.devCommands.values()) {
                    boolean isSelected = selectedCommand.equals(cmd.name);
                    if (ImGui.selectable(cmd.name, isSelected)) {
                        selectedCommand = cmd.name;
                        Utils.log("select " + cmd.name);
                    }
                    if (isSelected) {
                        ImGui.setItemDefaultFocus();
                    }
                }
                ImGui.endCombo();
            }

            paramsLayout();
            if (ImGui.button("execute")) {
                try {
                    DevCommandEffect cmd = ScriptTheSpire.SCRIPT.devCommands.get(selectedCommand);
                    switch (cmd.requiredValues.size()) {
                        case 0:
                            cmd.action.call();
                            break;
                        case 1:
                            LuaValue v1 = paramFromJavaToLua(cmd, cmd.requiredValues.get(0));
                            cmd.action.call(v1);
                            break;
                        case 2:
                            LuaValue v21 = paramFromJavaToLua(cmd, cmd.requiredValues.get(0));
                            LuaValue v22 = paramFromJavaToLua(cmd, cmd.requiredValues.get(1));
                            cmd.action.call(v21, v22);
                            break;
                        case 3:
                            LuaValue v31 = paramFromJavaToLua(cmd, cmd.requiredValues.get(0));
                            LuaValue v32 = paramFromJavaToLua(cmd, cmd.requiredValues.get(1));
                            LuaValue v33 = paramFromJavaToLua(cmd, cmd.requiredValues.get(2));
                            cmd.action.call(v31, v32, v33);
                            break;
                        default:
                    }
                } catch (LuaError ex) {
                    Utils.error(ex.getMessage());
                }

                Utils.log("execute");
            }
        }
        ImGui.end();
    }

    private LuaValue paramFromJavaToLua(DevCommandEffect cmd, DevCommandEffect.RequiredValue val) {
        String id = String.format("%s:%s", cmd.name, val.name);
        if (!variables.containsKey(id)) {
            return LuaValue.NONE;
        }

        if (val.clz.startsWith("int")) {
            ImInt v = (ImInt) variables.get(id);
            return LuaNumber.valueOf(v.get());
        }

        return LuaValue.NONE;
    }

    private void paramsLayout() {
        if (selectedCommand.isEmpty()) return;
        DevCommandEffect cmd = ScriptTheSpire.SCRIPT.devCommands.get(selectedCommand);
        if (cmd == null) return;
//        ImGui.text(String.format("params: %d", cmd.requiredValues.size()));
        for (DevCommandEffect.RequiredValue val : cmd.requiredValues) {
            String id = String.format("%s:%s", cmd.name, val.name);
            if (val.clz.startsWith("int")) {
                if (!variables.containsKey(id)) {
                    variables.put(id, new ImInt(val.defaultValue.toint()));
                }
                ImGui.inputInt(val.name, (ImInt) variables.get(id));
            }
        }
    }

    private void tip(String tip) {
        ImGui.textDisabled("(?)");
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(tip);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }
}
