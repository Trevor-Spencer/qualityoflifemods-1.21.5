package net.gamma.qualityoflife.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyMapping ZOOM_KEY;
    public static KeyMapping MODIFY_MOD_KEY;
    public static KeyMapping LOCK_KEY;

    public static void register(RegisterKeyMappingsEvent event)
    {
        ZOOM_KEY = new KeyMapping("key.qualityoflifemods.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "key.categories.qualityoflifemods");
        MODIFY_MOD_KEY = new KeyMapping("key.qualityoflifemods.modifymod",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.qualityoflifemods");
        LOCK_KEY = new KeyMapping("key.qualityoflifemods.lock",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "key.categories.qualityoflifemods"
                );

        event.register(ZOOM_KEY);
        event.register(MODIFY_MOD_KEY);
        event.register(LOCK_KEY);

    }
}
