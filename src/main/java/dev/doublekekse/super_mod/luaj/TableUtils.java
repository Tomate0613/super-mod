package dev.doublekekse.super_mod.luaj;

import dev.doublekekse.super_mod.SuperProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class TableUtils {
    public static LuaTable positionTable(BlockPos pos) {
        LuaTable table = new LuaTable();

        table.set("x", pos.getX());
        table.set("y", pos.getY());
        table.set("z", pos.getZ());

        return table;
    }

    public static LuaTable positionTable(Vec3 pos) {
        LuaTable table = new LuaTable();

        table.set("x", pos.x);
        table.set("y", pos.y);
        table.set("z", pos.z);

        return table;
    }

    public static LuaTable playerTable(Player player) {
        LuaTable table = new LuaTable();

        table.set("name", player.getDisplayName().getString());
        table.set("position", positionTable(player.position()));
        table.set("is_shift_key_down", LuaValue.valueOf(player.isShiftKeyDown()));

        return table;
    }

    public static LuaValue profileTable(SuperProfile profile) {
        if (profile == null) {
            return LuaValue.NIL;
        }

        LuaTable table = new LuaTable();

        table.set("area", profile.area.toString());
        table.set("itemUsageInfluence", profile.itemUsageInfluence);
        table.set("jumpingInfluence", profile.jumpingInfluence);
        table.set("offset", profile.offset);
        table.set("rotInfluence", profile.rotInfluence);
        table.set("speedInfluence", profile.speedInfluence);

        return table;
    }

    public static LuaTable objectiveTable(Objective objective) {
        LuaTable table = new LuaTable();

        var scores = objective.getScoreboard().listPlayerScores(objective);

        var scoresList = new LuaValue[scores.size()];

        int i = 0;
        for (var score : scores) {
            var scoreTable = new LuaTable();

            scoreTable.set("owner", score.owner());
            scoreTable.set("value", score.value());

            scoresList[i] = scoreTable;
            i++;
        }

        table.set("name", objective.getName());
        table.set("render_type", objective.getRenderType().name());
        table.set("criteria", objective.getCriteria().getName());
        table.set("scores", LuaTable.listOf(scoresList));

        return table;
    }
}
