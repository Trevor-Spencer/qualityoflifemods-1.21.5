package net.gamma.qualityoflife.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.gamma.qualityoflife.Config.MOVEMENT_ACTIVE;
import static net.gamma.qualityoflife.event.MovementVisualClientEvent.*;
import static net.gamma.qualityoflife.util.WidgetUtils.getReal;
import static net.gamma.qualityoflife.widget.ManagerWidget.MOVEMENTVISUALWIDGET;

@Mixin(Gui.class)
public class RenderMovementMixin {

    @Inject(method = "renderScoreboardSidebar", at = @At("TAIL"))
    private static void renderVisual(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        if(MOVEMENT_ACTIVE.get())
        {
            if(Minecraft.getInstance().level != null)
            {
                if(!Minecraft.getInstance().getDebugOverlay().showDebugScreen())
                {
                    int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                    int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                    int realX = getReal(MOVEMENTVISUALWIDGET.normalizedX, screenWidth);
                    int realY = getReal(MOVEMENTVISUALWIDGET.normalizedY, screenHeight);
                    int realWidth = getReal(MOVEMENTVISUALWIDGET.normalizedWidth, screenWidth);
                    int realHeight = getReal(MOVEMENTVISUALWIDGET.normalizedHeight, screenHeight);
                    guiGraphics.fill(realX, realY, realX + realWidth, realY + realHeight, 0x805C5C5C);
                    guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_BASE, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);
                    if(clicks.isEmpty())
                    {
                        String text = "CPS: 0";
                        int boxWidth = realWidth / 3;
                        int boxHeight = realHeight / 3;
                        float scaleWidth = 1.0f;
                        float scaleHeight = 1.0f;
                        int textWidth = Minecraft.getInstance().font.width(text);
                        int textHeight = Minecraft.getInstance().font.lineHeight;
                        if(textWidth > boxWidth)
                        {
                            scaleWidth = (float) boxWidth / textWidth;
                        }
                        if(textHeight > boxHeight)
                        {
                            scaleHeight = (float) boxHeight / textHeight;
                        }
                        float scale = Math.min(scaleWidth, scaleHeight);
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().scale(scale, scale, 1.0f);
                        guiGraphics.drawString(Minecraft.getInstance().font, text, (realX + boxWidth/2f - textWidth*scale/2f)/scale, (realY + boxHeight/2f - textHeight*scale/2f)/scale, 0xFFFFFF, false);
                        guiGraphics.pose().popPose();
                    }
                    else
                    {
                        for(int i = 0; i < clicks.size(); i++)
                        {
                            if(clicks.elementAt(i) < Minecraft.getInstance().player.tickCount - 20)
                            {
                                clicks.remove(i);
                                i--;
                            }
                        }
                        String text = String.format("CPS: %d", clicks.size());
                        int boxWidth = realWidth / 3;
                        int boxHeight = realHeight / 3;
                        float scaleWidth = 1.0f;
                        float scaleHeight = 1.0f;
                        int textWidth = Minecraft.getInstance().font.width(text);
                        int textHeight = Minecraft.getInstance().font.lineHeight;
                        if(textWidth > boxWidth)
                        {
                            scaleWidth = (float) boxWidth / textWidth;
                        }
                        if(textHeight > boxHeight)
                        {
                            scaleHeight = (float) boxHeight / textHeight;
                        }
                        float scale = Math.min(scaleWidth, scaleHeight);
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().scale(scale, scale, 1.0f);
                        guiGraphics.drawString(Minecraft.getInstance().font, text, (realX + boxWidth/2f - textWidth*scale/2f)/scale, (realY + boxHeight/2f - textHeight*scale/2f)/scale, 0xFFFFFF, false);
                        guiGraphics.pose().popPose();
                    }
                    if(wKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_W_PRESS, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);
                    }
                    if(aKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_A_PRESS, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);
                    }
                    if(sKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_S_PRESS, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);
                    }
                    if(dKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_D_PRESS, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);
                    }
                    if(rMousePressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_R_MOUSE_PRESS, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);

                    }
                    if(lMousePressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_L_MOUSE_PRESS, realX, realY, 0f, 0f, realWidth, realHeight, realWidth, realHeight);

                    }
                }
            }

        }

    }
}
