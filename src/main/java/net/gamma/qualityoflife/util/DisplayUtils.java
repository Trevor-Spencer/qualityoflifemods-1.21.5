package net.gamma.qualityoflife.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.List;

import static net.gamma.qualityoflife.util.WidgetUtils.getReal;

public class DisplayUtils {
    private static final int PIXELBORDER = 1;

    public static void drawBackgroundBorder(GuiGraphics graphics,
                                      int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                      double normalizedWidth, double normalizedHeight, int color, float hue
    )
    {
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth);
        int realHeight = getReal(normalizedHeight, screenHeight);

        Color tempColor = Color.getHSBColor(hue, 1.0f, 1.0f);
        int borderColor = (tempColor.getRed() << 16) | (tempColor.getGreen() << 8) | tempColor.getBlue();

        graphics.fill(realX, realY, realX + realWidth, realY + realHeight, color);
        graphics.hLine(realX, realX+realWidth-1, realY, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, realY+(realHeight/5)-1, borderColor | 0xFF000000);
        graphics.vLine(realX, realY, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.vLine(realX+realWidth-1, realY, realY+realHeight-1, borderColor | 0xFF000000);
    }
    public static void drawBorder(GuiGraphics graphics,
                                            int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                            double normalizedWidth, double normalizedHeight, float hue
    )
    {
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth);
        int realHeight = getReal(normalizedHeight, screenHeight);

        Color tempColor = Color.getHSBColor(hue, 1.0f, 1.0f);
        int borderColor = (tempColor.getRed() << 16) | (tempColor.getGreen() << 8) | tempColor.getBlue();

        graphics.hLine(realX, realX+realWidth-1, realY, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, realY+(realHeight/5)-1, borderColor | 0xFF000000);
        graphics.vLine(realX, realY, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.vLine(realX+realWidth-1, realY, realY+realHeight-1, borderColor | 0xFF000000);
    }

    public static void drawTextTitle(GuiGraphics graphics,
                                     int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                     double normalizedWidth, double normalizedHeight,  int horizontalPadding, int verticalPadding,
                                     Font font, String title, int color)
    {
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth) - 2*PIXELBORDER - 2*horizontalPadding;
        int realHeight = getReal(normalizedHeight, screenHeight)/5 - 2*PIXELBORDER - 2*verticalPadding;
        int maxWidth = font.width(title);
        int textHeight = font.lineHeight;
        float scaleWidth = 1.0f;
        float scaleHeight = 1.0f;
        if(realWidth < 0 || realHeight < 0){return;}
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
        int titleBoxX = realX + PIXELBORDER + horizontalPadding;
        int titleBoxY = realY + PIXELBORDER + verticalPadding;
        float displayX = (titleBoxX + (realWidth - font.width(title)*scale)/2.0f)/scale;
        float displayY = (titleBoxY + (realHeight - font.lineHeight*scale)/2.0f)/scale;
        graphics.drawString(font, title, displayX, displayY, color, false);
        pose.popPose();
    }

    public static void drawTextBody(GuiGraphics graphics,
                                    int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                    double normalizedWidth, double normalizedHeight,  int horizontalPadding, int verticalPadding,
                                    Font font, List<String> strings, int color)
    {
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth) - 2*PIXELBORDER - 2*horizontalPadding;
        int realHeight = getReal(normalizedHeight, screenHeight) - getReal(normalizedHeight, screenHeight)/5 - 2*PIXELBORDER - 2*verticalPadding;

        int maxWidth = 0;
        for(String line : strings)
        {
            int lineWidth = font.width(line);
            if(lineWidth > maxWidth)
            {
                maxWidth = lineWidth;
            }
        }
        int textHeight = strings.size() * font.lineHeight;
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
        int bodyBoxX = realX + PIXELBORDER + horizontalPadding;
        int bodyBoxY = realY + getReal(normalizedHeight, screenHeight)/5 + PIXELBORDER + verticalPadding;
        float displayX = bodyBoxX/scale;

        for(int i = 0; i < strings.size(); i++)
        {
            float displayY = (bodyBoxY + (i*font.lineHeight)*scale)/scale;
            graphics.drawString(font, strings.get(i), displayX, displayY, color, false);
        }
        pose.popPose();
    }


}
