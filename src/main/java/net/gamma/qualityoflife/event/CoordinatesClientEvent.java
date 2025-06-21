package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import static net.gamma.qualityoflife.Config.COORDINATES_ACTIVE;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CoordinatesClientEvent {
    //Variables for coordinates
    private static Vec3 oldPos = null;
    private static String[] coordinates = new String[3];
    public static String stringBiome = "Biome: ";

    //Function for updating coordinates when player moves
    @SubscribeEvent
    private static void onMove(ClientTickEvent.Post event)
    {
        if(COORDINATES_ACTIVE.get())
        {
            if(Minecraft.getInstance().player == null)
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
            int x = 2;
            int y = 2;
            for(int i = 0; i < 3; i++) {
                event.getGuiGraphics().drawString(Minecraft.getInstance().font, coordinates[i], x, y + (i*9), 0xFFFFFF);
            }
            event.getGuiGraphics().drawString(Minecraft.getInstance().font, stringBiome, x, y+(3*9), 0xFFFFFF);
        }
    }
}
