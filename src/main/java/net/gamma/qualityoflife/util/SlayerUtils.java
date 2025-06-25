package net.gamma.qualityoflife.util;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import org.slf4j.Logger;
import static net.gamma.qualityoflife.event.SlayerTrackerClientEvent.updateWorld;

public class SlayerUtils {
    public static void logMobs(int boss_id, int name_id, int spawnedBy_id, int timeRemaining_id, boolean advanced)
    {
        Level level = Minecraft.getInstance().level;
        Logger LOGGER = LogUtils.getLogger();
        if(!advanced)
        {
            LOGGER.info("---[BOSS FOUND INFO]---");
            LOGGER.info("BossID: " + boss_id);
            LOGGER.info("NameID: " + name_id + " NameComponent: " + level.getEntity(name_id).getCustomName().getString());
            LOGGER.info("SpawnedByID: " + spawnedBy_id + " NameComponent: " + level.getEntity(spawnedBy_id).getCustomName().getString());
            LOGGER.info("TimeID: " + timeRemaining_id + " NameComponent: " + level.getEntity(timeRemaining_id).getCustomName().getString());
        }
        else
        {
            LOGGER.info("---[MINI FOUND INFO]---");
            LOGGER.info("BossID: " + boss_id);
            LOGGER.info("NameID: " + name_id + " NameComponent: " + level.getEntity(name_id).getCustomName().getString());
            LOGGER.info("TimeID: " + timeRemaining_id + " NameComponent: " + level.getEntity(timeRemaining_id).getCustomName().getString());
        }
    }

    public static boolean checkOnSkyblock()
    {
        updateWorld = false;
        Objective obj = Minecraft.getInstance().level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
        if(obj != null) {
            return obj.getName().contains("SBScoreboard");
        }
        return false;
    }

}
