package net.gamma.qualityoflife.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyMapping ZOOM_KEY;

    public static void register(RegisterKeyMappingsEvent event)
    {
        ZOOM_KEY = new KeyMapping("key.qualityoflifemods.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "key.categories.qualityoflifemods");

        event.register(ZOOM_KEY);

    }
}
