package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Collection;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SkyblockClientEvent {
    private static boolean updateWorld = true;
    public static boolean onSkyblock = false;
    public static boolean onGarden = false;

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
    public static void checkOnGarden()
    {
        Objective obj = Minecraft.getInstance().level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
        if(obj == null) {return;}
        Collection<PlayerTeam> teams = Minecraft.getInstance().level.getScoreboard().getPlayerTeams();
        for(PlayerTeam team : teams)
        {
            String scoreboardLine = String.format("%s%s",team.getPlayerPrefix().getString(), team.getPlayerSuffix().getString());
            if(scoreboardLine.contains("The Garden"))
            {
                onGarden = true;
                return;
            }
        }
        onGarden = false;
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
    private static void tick(ClientTickEvent.Post event)
    {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null){return;}
        if(!Minecraft.getInstance().level.isClientSide){return;}
        if(scanTick < SCANDELAY)
        {
            scanTick++;
            return;
        }
        scanTick = 0;
        if(updateWorld)
        {
            checkOnSkyblock();

        }
        checkOnGarden();
    }
}
