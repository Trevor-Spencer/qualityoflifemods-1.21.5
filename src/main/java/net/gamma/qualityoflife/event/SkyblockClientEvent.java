package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Collection;

import static net.gamma.qualityoflife.QualityofLifeMods.DEBUGMODE;
import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SkyblockClientEvent {
    private static boolean updateWorld = true;
    public static boolean onSkyblock = false;
    public static boolean onGarden = false;
    public static boolean onGlacite = false;

    private static final int SCANDELAY = 20;
    private static int scanTick = 0;

    public static float hue = 0.0f;

    public static void checkOnSkyblock()
    {
        Level level = Minecraft.getInstance().level;
        if(level == null){return;}

        Objective obj = level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
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
        Level level = Minecraft.getInstance().level;
        if(level == null){return;}

        Scoreboard scoreboard = level.getScoreboard();
        if(scoreboard == null){return;}

        Objective obj = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if(obj == null) {return;}

        Collection<PlayerTeam> teams = scoreboard.getPlayerTeams();
        for(PlayerTeam team : teams)
        {
            String prefix = team.getPlayerPrefix().getString();
            String suffix = team.getPlayerSuffix().getString();
            String scoreboardLine = prefix+suffix;
            if(scoreboardLine.contains("The Garden"))
            {
                onGarden = true;
                return;
            }
        }
        onGarden = false;
    }
    public static void checkOnGlacite()
    {
        Level level = Minecraft.getInstance().level;
        if(level == null){return;}

        Scoreboard scoreboard = level.getScoreboard();
        if(scoreboard == null){return;}

        Objective obj = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if(obj == null) {return;}

        Collection<PlayerTeam> teams = scoreboard.getPlayerTeams();
        for(PlayerTeam team : teams)
        {
            String prefix = team.getPlayerPrefix().getString();
            String suffix = team.getPlayerSuffix().getString();
            String scoreboardLine = prefix+suffix;
            if(scoreboardLine.contains("Glacite") || scoreboardLine.contains("Base Camp"))
            {
                if(DEBUGMODE){LOGGER.info("[QUALITYOFLIFE] On Glacite True");}
                onGlacite = true;
                return;
            }
        }
        if(DEBUGMODE){LOGGER.info("[QUALITYOFLIFE] On Glacite False");}
        onGlacite = false;
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
        Level level = Minecraft.getInstance().level;
        if(level == null || Minecraft.getInstance().player == null){return;}
        if(!level.isClientSide){return;}
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
        checkOnGlacite();
        checkOnGarden();
    }

    @SubscribeEvent
    private static void render(RenderGuiEvent.Post event)
    {
        hue = (hue + 0.001f) % 1.0f;
    }
}
