package net.gamma.qualityoflife.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class SkyblockClientEvent {
    private static boolean updateWorld = true;
    public static boolean onSkyblock = false;

    private static final int SCANDELAY = 20;
    private static int scanTick = 0;

    public static void checkOnSkyblock()
    {
        Objective obj = Minecraft.getInstance().level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
        if(obj != null) {
            updateWorld = false;
            onSkyblock = obj.getName().contains("SBScoreboard");
            return;
        }
        onSkyblock = false;
        updateWorld = true;
    }

    @SubscribeEvent
    private static void joinWorld(EntityJoinLevelEvent event)
    {
        if(!event.getLevel().isClientSide){return;}
        if(event.getEntity() == Minecraft.getInstance().player)
        {
            updateWorld = true;
        }
    }

    @SubscribeEvent
    private static void tick(ClientTickEvent.Pre event)
    {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null){return;}
        if(!Minecraft.getInstance().level.isClientSide){return;}
        if(scanTick < SCANDELAY)
        {
            scanTick++;
            return;
        }
        scanTick = 0;
        if(updateWorld){checkOnSkyblock();}
    }
}
