package scriptspire.modcore;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class Utils {
    public static void log(Object s, Object... args) {
        ScriptTheSpire.LOGGER.info(String.format(s.toString(), args));
    }

    public static void error(Object s, Object... args) {
        ScriptTheSpire.LOGGER.error(String.format(s.toString(), args));
    }

    public static boolean inCombat() {
        return (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT);
    }
}
