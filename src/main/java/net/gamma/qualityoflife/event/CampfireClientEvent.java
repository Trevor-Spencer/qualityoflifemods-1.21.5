package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.List;

import static net.gamma.qualityoflife.Config.CAMPFIRE_ACTIVE;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.*;
import static net.gamma.qualityoflife.util.DisplayUtils.*;
import static net.gamma.qualityoflife.widget.ManagerWidget.CAMPFIREWIDGET;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CampfireClientEvent {
    public static boolean placedCampfire = false;
    private static int timer = 0;
    private static final int TIMERMAX = 6000;
    private static boolean isActive = false;

    private static final String TITLE = "CAMPFIRE";
    private static final int TITLECOLOR = 0xFFFFFFFF;
    private static final int TEXTCOLOR = 0xFFFFFFFF;
    private static final int BACKGROUNDCOLOR = 0x805C5C5C;

    List<Block> BLOCKS;

    @SubscribeEvent
    private static void tick(ClientTickEvent.Post event)
    {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null){return;}
        if(!onSkyblock){return;}
        if(!CAMPFIRE_ACTIVE.get()){return;}
        if(timer >= TIMERMAX)
        {
            timer = 0;
            isActive = false;
            placedCampfire = false;
        }
        else if(isActive)
        {
            timer++;
        }
        else if(placedCampfire)
        {
            timer = 0;
            isActive = true;
            placedCampfire = false;
        }

    }

    private static String calcTimeRemaining()
    {
        int remTicks;
        int remMin;
        int remSec;

        remTicks = TIMERMAX - timer;
        remMin = (remTicks) / (20*60);
        remSec = (remTicks /20) % 60;

        return String.format("Time Remaining %d min %d sec", remMin, remSec);
    }

    @SubscribeEvent
    private static void render(RenderGuiEvent.Post event)
    {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null){return;}
        if(!onSkyblock){return;}
        if(!CAMPFIRE_ACTIVE.get()){return;}
        if(!onGlacite){return;}
        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        String active = String.format("Campfire Placed: %s", isActive);
        String remainingTime = calcTimeRemaining();

        List<String> strings = List.of(active, remainingTime);

        renderContent(graphics,
                strings, TEXTCOLOR, TITLE, TITLECOLOR, BACKGROUNDCOLOR,
                screenWidth, screenHeight, CAMPFIREWIDGET);
    }
}
