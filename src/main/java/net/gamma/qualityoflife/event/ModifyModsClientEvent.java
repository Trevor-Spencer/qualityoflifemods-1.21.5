package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.gamma.qualityoflife.screen.CustomScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ModifyModsClientEvent {
    private static boolean screenOpen = false;

    @SubscribeEvent
    private static void keyInput(InputEvent.Key event)
    {
        if(Minecraft.getInstance().level != null)
        {
            if(GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS)
            {
                if(screenOpen == false)
                {
                    Minecraft.getInstance().setScreen(new CustomScreen());
                }
            }
        }

    }
}
