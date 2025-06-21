package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Vector;

import static net.gamma.qualityoflife.Config.MOVEMENT_ACTIVE;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MovementVisualClientEvent {
    public static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisualbase_gui.png");
    public static final ResourceLocation GUI_W_PRESS = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisualwpress_gui.png");
    public static final ResourceLocation GUI_A_PRESS = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisualapress_gui.png");
    public static final ResourceLocation GUI_S_PRESS = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisualspress_gui.png");
    public static final ResourceLocation GUI_D_PRESS = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisualdpress_gui.png");
    public static final ResourceLocation GUI_R_MOUSE_PRESS = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisualrmousepress_gui.png");
    public static final ResourceLocation GUI_L_MOUSE_PRESS = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/gui/movementvisual/movementvisuallmousepress_gui.png");

    public static final int imageWidth = 100;
    public static final int imageHeight = 100;
    public static final int drawImageWidth = imageWidth/2;
    public static final int drawImageHeight = imageHeight/2;
    public static int x;
    public static int y;

    public static boolean wKeyPressed = false;
    public static boolean aKeyPressed = false;
    public static boolean sKeyPressed = false;
    public static boolean dKeyPressed = false;
    public static boolean rMousePressed = false;
    public static boolean lMousePressed = false;
    public static boolean applyRMousePress = true;
    public static boolean applyLMousePress = true;

    public static Vector<Integer> clicks = new Vector<>();

    @SubscribeEvent
    public static void keyPress(InputEvent.Key event)
    {
        if(MOVEMENT_ACTIVE.get())
        {
            if(Minecraft.getInstance().level != null)
            {
                if(Minecraft.getInstance().screen == null)
                {
                    if(GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS)
                    {
                        wKeyPressed = true;
                    }
                    else
                    {
                        wKeyPressed = false;
                    }
                    if(GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS)
                    {
                        aKeyPressed = true;
                    }
                    else
                    {
                        aKeyPressed = false;
                    }
                    if(GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS)
                    {
                        sKeyPressed = true;
                    }
                    else
                    {
                        sKeyPressed = false;
                    }
                    if(GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS)
                    {
                        dKeyPressed = true;
                    }
                    else
                    {
                        dKeyPressed = false;
                    }
                }

            }
        }

    }

    @SubscribeEvent
    public static void mouseClick(InputEvent.MouseButton.Post event)
    {
        if(MOVEMENT_ACTIVE.get())
        {
            if(Minecraft.getInstance().level != null)
            {
                if(Minecraft.getInstance().screen == null)
                {
                    if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS)
                    {
                        rMousePressed = true;
                        if(applyRMousePress)
                        {
                            applyRMousePress = false;

                        }
                    }
                    else
                    {
                        rMousePressed = false;
                        applyRMousePress = true;
                    }
                    if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS)
                    {
                        lMousePressed = true;
                        if(applyLMousePress)
                        {
                            clicks.add(Minecraft.getInstance().player.tickCount);
                            applyLMousePress = false;
                        }
                    }
                    else
                    {
                        lMousePressed = false;
                        applyLMousePress = true;
                    }
                }
            }

        }


    }
}

