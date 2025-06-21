package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.gamma.qualityoflife.keybinding.ModKeyBinding;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import static net.gamma.qualityoflife.Config.ZOOM_ACTIVE;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ZoomClientEvent {
    //Variable for zoom
    private static float zoomFOV = 0.5f;

    //Function for updating zoom when player scrolls and hold zoom key
    @SubscribeEvent
    private static void scroll(InputEvent.MouseScrollingEvent event)
    {
        if(ZOOM_ACTIVE.get())
        {
            double delta = event.getScrollDeltaY();
            float zoomChange = 0.05f;
            float zoomMin = 0.1f;
            float zoomMax = 1.5f;
            if(ModKeyBinding.ZOOM_KEY.isDown())
            {
                if(delta > 0d) //Zoom In
                {
                    if(zoomFOV >= zoomMin + zoomChange)
                    {
                        zoomFOV -= zoomChange;
                    }
                    if(zoomFOV < zoomMin)
                    {
                        zoomFOV = zoomMin;
                    }
                }
                else if(delta < 0d) // Zoom Out
                {
                    if(zoomFOV <= zoomMax - zoomChange)
                    {
                        zoomFOV += zoomChange;
                    }
                    if(zoomFOV > zoomMax)
                    {
                        zoomFOV = zoomMax;
                    }
                }
                event.setCanceled(true);
            }
        }

    }

    //Function for updating FOV zoom when zoom key is held
    @SubscribeEvent
    private static void zoom(ComputeFovModifierEvent event)
    {
        if(ZOOM_ACTIVE.get())
        {
            if(ModKeyBinding.ZOOM_KEY.isDown())
            {
                event.setNewFovModifier(zoomFOV);
            }
            else
            {
                zoomFOV = 0.5f;
            }
        }
    }
}
