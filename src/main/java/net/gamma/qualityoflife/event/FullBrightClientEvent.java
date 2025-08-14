package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static net.gamma.qualityoflife.Config.FULLBRIGHT_ACTIVE;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class FullBrightClientEvent {
    private static boolean apply = true;
    @SubscribeEvent
    private static void clientTick(ClientTickEvent.Post event)
    {
        if(FULLBRIGHT_ACTIVE.get())
        {
            if(Minecraft.getInstance().level != null && apply)
            {
                Minecraft.getInstance().levelRenderer.allChanged();
                apply = false;
            }
        }
        else
        {
            if(Minecraft.getInstance().level != null && !apply)
            {
                Minecraft.getInstance().levelRenderer.allChanged();
                apply = true;
            }
        }
    }
}