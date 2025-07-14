package net.gamma.qualityoflife.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.extensions.IPoseStackExtension;

import java.util.List;

import static net.gamma.qualityoflife.util.WidgetUtils.getReal;

public class DisplayUtils {

    public static void drawInfo(GuiGraphics graphics,
                                int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                double normalizedWidth, double normalizedHeight,  int horizontalPadding, int verticalPadding,
                                Font font, List<String> strings, int color, boolean background, boolean isText)
    {


        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth);
        int realHeight = getReal(normalizedHeight, screenHeight);


        if(background)
        {
            graphics.fill(realX, realY, realX + realWidth, realY + realHeight, color);
        }
        if(isText)
        {
            int maxWidth = 0;
            for(String line : strings)
            {
                int lineWidth = font.width(line) + 2*horizontalPadding;
                if(lineWidth > maxWidth)
                {
                    maxWidth = lineWidth;
                }
            }
            int textHeight = strings.size() * font.lineHeight + 2*verticalPadding;
            float scaleWidth = 1.0f;
            float scaleHeight = 1.0f;
            if(maxWidth > realWidth)
            {
                scaleWidth = (float) realWidth / maxWidth;
            }
            if(textHeight > realHeight)
            {
                scaleHeight = (float) realHeight / textHeight;
            }
            float scale = Math.min(scaleWidth, scaleHeight);

            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.scale(scale, scale, 1.0f);
            float displayX = (realX + horizontalPadding*scale)/scale;
            for(int i = 0; i < strings.size(); i++)
            {
                float displayY = (realY + verticalPadding*scale + (i*font.lineHeight)*scale)/scale;
                graphics.drawString(font, strings.get(i), displayX, displayY, color, false);
            }
            pose.popPose();
        }


    }
}
