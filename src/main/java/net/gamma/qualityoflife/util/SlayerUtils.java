package net.gamma.qualityoflife.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;

public class SlayerUtils {
    public static void logMobs(int boss_id, int name_id, int spawnedBy_id, int timeRemaining_id, boolean advanced)
    {
        Level level = Minecraft.getInstance().level;
        if(!advanced)
        {
            LOGGER.info("[QUALITYOFLIFE] ---[BOSS FOUND INFO]---");
            LOGGER.info("[QUALITYOFLIFE] BossID: " + boss_id);
            LOGGER.info("[QUALITYOFLIFE] NameID: " + name_id + " NameComponent: " + level.getEntity(name_id).getCustomName().getString());
            LOGGER.info("[QUALITYOFLIFE] SpawnedByID: " + spawnedBy_id + " NameComponent: " + level.getEntity(spawnedBy_id).getCustomName().getString());
            LOGGER.info("[QUALITYOFLIFE] TimeID: " + timeRemaining_id + " NameComponent: " + level.getEntity(timeRemaining_id).getCustomName().getString());
        }
        else
        {
            LOGGER.info("[QUALITYOFLIFE] ---[MINI FOUND INFO]---");
            LOGGER.info("[QUALITYOFLIFE] BossID: " + boss_id);
            LOGGER.info("[QUALITYOFLIFE] NameID: " + name_id + " NameComponent: " + level.getEntity(name_id).getCustomName().getString());
            LOGGER.info("[QUALITYOFLIFE] TimeID: " + timeRemaining_id + " NameComponent: " + level.getEntity(timeRemaining_id).getCustomName().getString());
        }
    }
}
