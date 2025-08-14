package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.List;

import static net.gamma.qualityoflife.Config.*;
import static net.gamma.qualityoflife.util.DisplayUtils.*;
import static net.gamma.qualityoflife.widget.ManagerWidget.COORDINATESWIDGET;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CoordinatesClientEvent {
    //Variables for coordinates
    private static Vec3 oldPos = null;
    private static String[] coordinates = new String[] {"X: ", "Y: ", "Z: "};
    private static String stringBiome = "Biome: ";

    private static final String TITLE = "COORDINATES";
    private static final int TITLECOLOR = 0xFFFFFFFF;
    private static final int TEXTCOLOR = 0xFFFFFFFF;
    private static final int BACKGROUNDCOLOR = 0x805C5C5C;

    //Function for updating coordinates when player moves
    @SubscribeEvent
    private static void onMove(ClientTickEvent.Post event)
    {
        if(COORDINATES_ACTIVE.get())
        {
            if(Minecraft.getInstance().player == null || Minecraft.getInstance().level == null)
            {
                return;
            }
            Player player = Minecraft.getInstance().player;
            Vec3 pos = new Vec3(player.getX(),player.getY(),player.getZ());
            if(oldPos == null || oldPos.distanceTo(pos) > 0.001)
            {
                oldPos = pos;
                String stringPos = String.format("X: %.3f,Y: %.3f,Z: %.3f" , pos.x,pos.y,pos.z);
                coordinates = stringPos.split(",");
                Holder<Biome> biomeHolder = player.level().getBiome(player.blockPosition());
                stringBiome = String.format("Biome: %s", biomeHolder.getKey().location().getPath());
            }
        }
    }

    @SubscribeEvent
    private static void renderGUI(RenderGuiEvent.Post event)
    {
        if(COORDINATES_ACTIVE.get())
        {
            if(Minecraft.getInstance().level == null){return;}
            if(COORDINATESWIDGET == null){return;}
            GuiGraphics graphics = event.getGuiGraphics();
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

            List<String> strings = List.of(coordinates[0], coordinates[1], coordinates[2], stringBiome);

            renderContent(graphics,
                    strings, TEXTCOLOR, TITLE, TITLECOLOR, BACKGROUNDCOLOR,
                    screenWidth, screenHeight, COORDINATESWIDGET);
        }
    }
}
