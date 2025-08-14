package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.gamma.qualityoflife.screen.CustomScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import static net.gamma.qualityoflife.keybinding.ModKeyBinding.MODIFY_MOD_KEY;
import static net.gamma.qualityoflife.screen.CustomScreen.screenOpen;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ModifyModsClientEvent {
    @SubscribeEvent
    private static void keyInput(InputEvent.Key event)
    {
        if(Minecraft.getInstance().level != null)
        {
            if(GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), MODIFY_MOD_KEY.getKey().getValue()) == GLFW.GLFW_PRESS)
            {
                if(Minecraft.getInstance().screen != null){return;}
                if(!screenOpen)
                {
                    Minecraft.getInstance().setScreen(new CustomScreen());
                }
            }
        }

    }
}
