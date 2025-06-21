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
                    x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - drawImageWidth;
                    y = 50;
                    guiGraphics.fill(x, 50, x + drawImageWidth, 50 + drawImageHeight, 0x805C5C5C);
                    guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_BASE, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);
                    if(clicks.size() == 0)
                    {
                        String text = String.format("CPS: %d", clicks.size());
                        int boxWidth = drawImageWidth / 3;
                        int boxHeight = drawImageHeight / 3;
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
                        guiGraphics.drawString(Minecraft.getInstance().font, text, (x + boxWidth/2f - textWidth*scale/2f)/scale, (y + boxHeight/2f - textHeight*scale/2f)/scale, 0xFFFFFF, false);
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
                        int boxWidth = drawImageWidth / 3;
                        int boxHeight = drawImageHeight / 3;
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
                        guiGraphics.drawString(Minecraft.getInstance().font, text, (x + boxWidth/2f - textWidth*scale/2f)/scale, (y + boxHeight/2f - textHeight*scale/2f)/scale, 0xFFFFFF, false);
                        guiGraphics.pose().popPose();
                    }
                    if(wKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_W_PRESS, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);
                    }
                    if(aKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_A_PRESS, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);
                    }
                    if(sKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_S_PRESS, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);
                    }
                    if(dKeyPressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_D_PRESS, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);
                    }
                    if(rMousePressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_R_MOUSE_PRESS, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);

                    }
                    if(lMousePressed)
                    {
                        guiGraphics.blit(RenderType.GUI_TEXTURED, GUI_L_MOUSE_PRESS, x, 50, 0f, 0f, drawImageWidth, drawImageHeight, drawImageWidth, drawImageHeight);

                    }
                }
            }

        }

    }
}
