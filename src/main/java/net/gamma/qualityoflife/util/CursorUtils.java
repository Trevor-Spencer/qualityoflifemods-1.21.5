package net.gamma.qualityoflife.util;

import net.minecraft.client.Minecraft;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class CursorUtils {
    private static final long WINDOW = Minecraft.getInstance().getWindow().getWindow();
    private static final Map<Integer, Long> CURSOR_MAP = Map.of(
            0, glfwCreateStandardCursor(GLFW_ARROW_CURSOR),
            1, glfwCreateStandardCursor(GLFW_RESIZE_NS_CURSOR),
            2, glfwCreateStandardCursor(GLFW_RESIZE_EW_CURSOR),
            3, glfwCreateStandardCursor(GLFW_RESIZE_NWSE_CURSOR),
            4,glfwCreateStandardCursor(GLFW_RESIZE_NESW_CURSOR));
    public static void setCursor(int cursorType)
    {
        glfwSetCursor(WINDOW, CURSOR_MAP.get(cursorType));
    }
}
