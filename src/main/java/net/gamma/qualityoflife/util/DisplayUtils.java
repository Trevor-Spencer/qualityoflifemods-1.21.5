package net.gamma.qualityoflife.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gamma.qualityoflife.widget.CustomWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.util.ArrayList;
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

    public static void renderContentMayor(GuiGraphics graphics,
                                          List<Component> lines, int bodyColor, String title, int titleColor, int backgroundColor,
                                          int screenWidth, int screenHeight, CustomWidget widget) {
        Font font = Minecraft.getInstance().font;
        float titleHeight = font.lineHeight;

        int realWidth = getReal(widget.normalizedWidth, screenWidth) - 2 * PIXELBORDER - 2 * CustomWidget.PADDING;
        List<Component> wrappedLines = new ArrayList<>();
        Component indent = Component.literal(" ");

        for (Component line : lines) {
            if(line == null){continue;}
            List<Component> pieces = line.toFlatList();
            boolean firstLine = true;
            List<Component> currentLine = new ArrayList<>();
            int currentWidth = 0;

            for (Component piece : pieces) {
                String text = piece.getString();
                Style style = piece.getStyle();

                int pieceWidth = font.width(piece);

                if (currentWidth + pieceWidth <= realWidth) {
                    currentLine.add(piece);
                    currentWidth += pieceWidth;
                } else {
                    int remainingWidth = realWidth - currentWidth;
                    int splitIndex = getSmallestWidth(text, font, remainingWidth);
                    int lastSpace = text.lastIndexOf(' ', splitIndex);
                    if (lastSpace > 0) splitIndex = lastSpace + 1;

                    String partText = text.substring(0, splitIndex).trim();
                    Component partComp = Component.literal(partText).withStyle(style);
                    currentLine.add(firstLine ? partComp : indent.copy().append(partComp));
                    MutableComponent combined = Component.empty();
                    for (Component c : currentLine) {
                        combined = combined.append(c);
                    }
                    wrappedLines.add(combined);

                    text = text.substring(splitIndex).trim();
                    if (!text.isEmpty()) {
                        currentLine = new ArrayList<>();
                        currentLine.add(indent.copy().append(Component.literal(text).withStyle(style)));
                        currentWidth = font.width(currentLine.getFirst());
                    } else {
                        currentLine = new ArrayList<>();
                        currentWidth = 0;
                    }

                    firstLine = false;
                }
            }

            if (!currentLine.isEmpty()) {
                MutableComponent combined = Component.empty();
                for (Component c : currentLine) {
                    combined = combined.append(c);
                }
                wrappedLines.add(combined);
            }
        }

        float bodyHeight = font.lineHeight * wrappedLines.size();
        int boxHeight = getReal(widget.normalizedHeight, screenHeight);
        int availableSpace = boxHeight - 3 * PIXELBORDER - 4 * CustomWidget.PADDING;
        int totalTextHeight = (int) (titleHeight + bodyHeight);

        float totalScale = totalTextHeight > availableSpace ? (float) availableSpace / totalTextHeight : 1.0f;
        float bodyHeightScale = Math.min(1.0f, totalScale);
        float titleHeightScale = Math.min(1.0f, totalScale);

        drawBackgroundBorder(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, widget.normalizedHeight, titleHeightScale, backgroundColor, hue);
        drawTextTitle(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, titleHeightScale,
                font, title, titleColor);
        drawTextMayorBody(graphics,
                screenWidth, screenHeight, widget.normalizedX, widget.normalizedY,
                widget.normalizedWidth, titleHeightScale, bodyHeightScale,
                font, wrappedLines, bodyColor);
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

    public static void drawTextMayorBody(GuiGraphics graphics,
                                         int screenWidth, int screenHeight, double normalizedX, double normalizedY,
                                         double normalizedWidth, float titleHeightScale, float bodyHeightScale,
                                         Font font, List<Component> components, int defaultColor) {
        int widgetX = getReal(normalizedX, screenWidth);
        int widgetY = getReal(normalizedY, screenHeight);

        int dividerY = getDividerY(widgetY, titleHeightScale);

        int textBoxX = widgetX + PIXELBORDER + CustomWidget.PADDING;
        int textBoxY = dividerY + PIXELBORDER + CustomWidget.PADDING;

        int realWidth = getReal(normalizedWidth, screenWidth) - 2 * PIXELBORDER - 2 * CustomWidget.PADDING;

        int maxWidth = 0;
        for (Component comp : components) {
            int lineWidth = font.width(comp);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        float scaleWidth = 1.0f;
        if (maxWidth > realWidth) {
            scaleWidth = (float) realWidth / maxWidth;
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(scaleWidth, bodyHeightScale, 1.0f);

        float dX = textBoxX / scaleWidth;
        int displayX = (int) dX;
        for (int i = 0; i < components.size(); i++) {
            int displayY = (int)((textBoxY + (i * font.lineHeight) * bodyHeightScale) / bodyHeightScale);
            Component comp = components.get(i);

            graphics.drawString(font, comp, displayX, displayY, defaultColor, false);
        }
        pose.popPose();
    }

    public static int getSmallestWidth(String line, Font font, int realWidth)
    {
        for(int i = 0; i < realWidth; i++)
        {
            String temp = line.substring(0,i);
            if(font.width(temp) > realWidth)
            {
                return i-1;
            }
        }
        return font.width(line);
    }
}
