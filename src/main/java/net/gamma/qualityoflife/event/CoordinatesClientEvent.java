package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import static net.gamma.qualityoflife.util.WidgetUtils.getReal;
import static net.gamma.qualityoflife.widget.ManagerWidget.COORDINATESWIDGET;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CoordinatesClientEvent {
    //Variables for coordinates
    private static Vec3 oldPos = null;
    private static String[] coordinates = new String[3];
    public static String stringBiome = "Biome: ";
    public static int HORIZONTALPADDING = 2;
    public static int VERTICALPADDING = 2;

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
            if(oldPos == null)
            {
                oldPos = pos;
                String stringPos = String.format("X: %.3f,Y: %.3f,Z: %.3f" , pos.x,pos.y,pos.z);
                coordinates = stringPos.split(",");
                Holder<Biome> biomeHolder = Minecraft.getInstance().player.level().getBiome(player.blockPosition());
                stringBiome = biomeHolder.getKey().location().getPath();
            }
            else if(oldPos.distanceTo(pos) > 0.001)
            {
                oldPos = pos;
                String stringPos = String.format("X: %.3f,Y: %.3f,Z: %.3f" , pos.x,pos.y,pos.z);
                coordinates = stringPos.split(",");
                Holder<Biome> biomeHolder = Minecraft.getInstance().player.level().getBiome(player.blockPosition());
                stringBiome = String.format("Biome: %s", biomeHolder.getKey().location().getPath());
            }
        }
    }

    //Function for rendering coordinates to top left of game screen
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
            int realX = getReal(COORDINATESWIDGET.normalizedX, screenWidth);
            int realY = getReal(COORDINATESWIDGET.normalizedY, screenHeight);
            int realWidth = getReal(COORDINATESWIDGET.normalizedWidth, screenWidth);
            int realHeight = getReal(COORDINATESWIDGET.normalizedHeight, screenHeight);
            float scaleWidth = 1.0f;
            float scaleHeight = 1.0f;
            Font font = Minecraft.getInstance().font;
            graphics.fill(realX, realY, realX + realWidth, realY + realHeight, 0x805C5C5C);
            List<Integer> textWidths = List.of(font.width(coordinates[0]) + 2*HORIZONTALPADDING, font.width(coordinates[1]) + 2*HORIZONTALPADDING,font.width(coordinates[2]) + 2*HORIZONTALPADDING, font.width(stringBiome) + 2*HORIZONTALPADDING);
            int maxWidth = textWidths.stream().max(Integer::compareTo).orElse(0);
            int textHeight = 4 * font.lineHeight + 2*VERTICALPADDING;
            if(textHeight > realHeight)
            {
                scaleHeight = (float) realHeight / textHeight;
            }
            if(maxWidth > realWidth)
            {
                scaleWidth = (float) realWidth / maxWidth;
            }
            float scale = Math.min(scaleWidth, scaleHeight);
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            for(int i = 0; i < 3; i++)
            {
                graphics.drawString(font, coordinates[i], (realX + HORIZONTALPADDING*scale)/scale, (realY + VERTICALPADDING*scale + (i*9)*scale)/scale, 0xFFFFFF, false);
            }
            graphics.drawString(font, stringBiome, (realX + HORIZONTALPADDING*scale)/scale, (realY + VERTICALPADDING*scale + (3*9)*scale)/scale, 0xFFFFFF, false);
            graphics.pose().popPose();
        }
    }
}
