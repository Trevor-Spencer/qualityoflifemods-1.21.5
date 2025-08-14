package net.gamma.qualityoflife.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gamma.qualityoflife.widget.CustomWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.List;

import static net.gamma.qualityoflife.event.SkyblockClientEvent.hue;
import static net.gamma.qualityoflife.util.WidgetUtils.getReal;

public class DisplayUtils {
    public static final int PIXELBORDER = 1;

    public static void renderContent(GuiGraphics graphics,
                                     List<String> strings, int bodyColor, String title, int titleColor, int backgroundColor,
                                     int screenWidth, int screenHeight, CustomWidget widget)
    {
        Font font = Minecraft.getInstance().font;
        float titleHeight = font.lineHeight;
        float bodyHeight = font.lineHeight * strings.size();
        int boxHeight = getReal(widget.normalizedHeight, screenHeight);
        int availableSpace = boxHeight - 3*PIXELBORDER - 4*CustomWidget.PADDING;
        int totalTextHeight = (int)(titleHeight + bodyHeight);

        float bodyHeightScale;
        float titleHeightScale;
        float totalScale = 1.0f;
        if(totalTextHeight > availableSpace)
        {
            totalScale = (float) availableSpace / totalTextHeight;
        }
        bodyHeightScale = Math.min(1.0f, totalScale);
        titleHeightScale = Math.min(1.0f, totalScale);

        drawBackgroundBorder(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, widget.normalizedHeight, titleHeightScale, backgroundColor, hue);
        drawTextTitle(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, titleHeightScale,
                Minecraft.getInstance().font, title, titleColor);
        drawTextBody(graphics,
                screenWidth, screenHeight,widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, titleHeightScale, bodyHeightScale,
                Minecraft.getInstance().font, strings, bodyColor);
    }
    public static void renderContentSpecial(GuiGraphics graphics,
                                            List<String> strings, int bodyColor, String title, int titleColor,
                                            int screenWidth, int screenHeight, CustomWidget widget)
    {
        Font font = Minecraft.getInstance().font;
        float titleHeight = font.lineHeight;
        float bodyHeight = font.lineHeight * strings.size();
        int boxHeight = getReal(widget.normalizedHeight, screenHeight);
        int availableSpace = boxHeight - 3*PIXELBORDER - 4*CustomWidget.PADDING;
        int totalTextHeight = (int)(titleHeight + bodyHeight);

        float bodyHeightScale;
        float titleHeightScale;
        float totalScale = 1.0f;
        if(totalTextHeight > availableSpace)
        {
            totalScale = (float) availableSpace / totalTextHeight;
        }
        bodyHeightScale = Math.min(1.0f, totalScale);
        titleHeightScale = Math.min(1.0f, totalScale);

        drawBorder(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, widget.normalizedHeight, titleHeightScale, hue);
        drawTextTitle(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, titleHeightScale,
                Minecraft.getInstance().font, title, titleColor);
        drawTextBody(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, titleHeightScale, bodyHeightScale,
                Minecraft.getInstance().font, strings, bodyColor);
    }

    public static int getDividerY(int realY, float titleHeightScale)
    {
        int scaledTitleHeight = (int)(Minecraft.getInstance().font.lineHeight * titleHeightScale);
        return realY + PIXELBORDER + 2* CustomWidget.PADDING + scaledTitleHeight;
    }

    public static void drawBackgroundBorder(GuiGraphics graphics,
                                      int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                      double normalizedWidth, double normalizedHeight, float titleHeightScale, int color, float hue
    )
    {
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth);
        int realHeight = getReal(normalizedHeight, screenHeight);

        int dividerY = getDividerY(realY, titleHeightScale);

        Color tempColor = Color.getHSBColor(hue, 1.0f, 1.0f);
        int borderColor = (tempColor.getRed() << 16) | (tempColor.getGreen() << 8) | tempColor.getBlue();

        graphics.fill(realX, realY, realX + realWidth, realY + realHeight, color);
        graphics.hLine(realX, realX+realWidth-1, realY, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, dividerY, borderColor | 0xFF000000);
        graphics.vLine(realX, realY, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.vLine(realX+realWidth-1, realY, realY+realHeight-1, borderColor | 0xFF000000);
    }
    public static void drawBorder(GuiGraphics graphics,
                                            int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                            double normalizedWidth, double normalizedHeight, float titleHeightScale, float hue
    )
    {
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth);
        int realHeight = getReal(normalizedHeight, screenHeight);

        int dividerY = getDividerY(realY, titleHeightScale);

        Color tempColor = Color.getHSBColor(hue, 1.0f, 1.0f);
        int borderColor = (tempColor.getRed() << 16) | (tempColor.getGreen() << 8) | tempColor.getBlue();

        graphics.hLine(realX, realX+realWidth-1, realY, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.hLine(realX, realX+realWidth-1, dividerY, borderColor | 0xFF000000);
        graphics.vLine(realX, realY, realY+realHeight-1, borderColor | 0xFF000000);
        graphics.vLine(realX+realWidth-1, realY, realY+realHeight-1, borderColor | 0xFF000000);
    }

    public static void drawTextTitle(GuiGraphics graphics,
                                     int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                     double normalizedWidth, float titleHeightScale,
                                     Font font, String title, int color)
    {
        int widgetX = getReal(normalizedX, screenWidth);
        int widgetY = getReal(normalizedY, screenHeight);

        int dividerY = getDividerY(widgetY, titleHeightScale);

        int textBoxX = widgetX + PIXELBORDER + CustomWidget.PADDING;
        int textBoxY = widgetY + PIXELBORDER + CustomWidget.PADDING;

        int realWidth = getReal(normalizedWidth, screenWidth) - 2*PIXELBORDER - 2*CustomWidget.PADDING;
        int realHeight = dividerY - widgetY - PIXELBORDER - 2*CustomWidget.PADDING;
        int maxWidth = font.width(title);
        float scaleWidth = 1.0f;
        if(realWidth < 0 || realHeight < 0){return;}
        if(maxWidth > realWidth)
        {
            scaleWidth = (float) realWidth / maxWidth;
        }
        float scale = Math.min(scaleWidth, titleHeightScale);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(scale, scale, 1.0f);
        float displayX = (textBoxX + (realWidth - font.width(title)*scale)/2.0f)/scale;
        float displayY = (textBoxY + (realHeight - font.lineHeight*scale)/2.0f)/scale;
        graphics.drawString(font, title, displayX, displayY, color, false);
        pose.popPose();
    }

    public static void drawTextBody(GuiGraphics graphics,
                                    int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                    double normalizedWidth, float titleHeightScale, float bodyHeightScale,
                                    Font font, List<String> strings, int color)
    {
        int widgetX = getReal(normalizedX, screenWidth);
        int widgetY = getReal(normalizedY, screenHeight);

        int dividerY = getDividerY(widgetY, titleHeightScale);

        int textBoxX = widgetX + PIXELBORDER + CustomWidget.PADDING;
        int textBoxY = dividerY + PIXELBORDER + CustomWidget.PADDING;

        int realWidth = getReal(normalizedWidth, screenWidth) - 2*PIXELBORDER - 2*CustomWidget.PADDING;

        int maxWidth = 0;
        for(String line : strings)
        {
            int lineWidth = font.width(line);
            if(lineWidth > maxWidth)
            {
                maxWidth = lineWidth;
            }
        }
        float scaleWidth = 1.0f;
        if(maxWidth > realWidth)
        {
            scaleWidth = (float) realWidth / maxWidth;
        }
        float scale = Math.min(scaleWidth, bodyHeightScale);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(scale, scale, 1.0f);


        float displayX = textBoxX/scale;

        for(int i = 0; i < strings.size(); i++)
        {
            float displayY = (textBoxY + (i*font.lineHeight)*scale)/scale;
            graphics.drawString(font, strings.get(i), displayX, displayY, color, false);
        }
        pose.popPose();
    }


}
