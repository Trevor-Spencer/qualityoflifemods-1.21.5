package net.gamma.qualityoflife.widget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;
import static net.gamma.qualityoflife.QualityofLifeMods.MODCONFIGFOLDER;
import static net.gamma.qualityoflife.util.CursorUtils.setCursor;
import static net.gamma.qualityoflife.util.WidgetUtils.*;

public class CustomWidget extends AbstractWidget {
    private final BooleanSupplier toRender;
    public static int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
    public static int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
    public static int PADDING = 4;
    public static final double MIN_NORMALIZED_WIDTH = 0.1;
    public static final double MIN_NORMALIZED_HEIGHT = 0.1;
    public double normalizedX;
    public double normalizedY;
    public double normalizedWidth;
    public double normalizedHeight;
    public String filename;

    public double mouseOffsetX;
    public double mouseOffsetY;
    public boolean isDragging = false;

    public int RESIZE_MARGIN = 6;
    public boolean isResizing = false;
    public enum RESIZE_DIRECTION {NONE, UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT}
    public RESIZE_DIRECTION resizeDirection = RESIZE_DIRECTION.NONE;

    public CustomWidget(int x, int y, int width, int height, Component message, String file, BooleanSupplier toRender) {
        super(x, y, width, height, message);
        screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        normalizedX = getNormalized(x, screenWidth);
        normalizedY = getNormalized(y, screenHeight);
        normalizedWidth = getNormalized(width, screenWidth);
        normalizedHeight = getNormalized(height, screenHeight);
        filename = file;
        this.toRender = toRender;
    }

    public static CustomWidget readIn(String filename, Component message, BooleanSupplier toRender)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        double nX;
        double nY;
        double nWidth;
        double nHeight;
        String debugStatement;
        Path filePath = MODCONFIGFOLDER.resolve(filename);
        if(!Files.exists(filePath))
        {
            debugStatement = String.format("[QUALITYOFLIFE] Filepath not found: %s", filePath);
            LOGGER.warn(debugStatement);
            nX = 0;
            nY = 0;
            nWidth = MIN_NORMALIZED_WIDTH;
            nHeight = MIN_NORMALIZED_HEIGHT;
        }
        else
        {
            try
            {
                JsonNode jsonNode = objectMapper.readTree(filePath.toFile());
                nX = jsonNode.has("normalizedX") ? jsonNode.get("normalizedX").asDouble() : 0d;
                nY = jsonNode.has("normalizedY") ? jsonNode.get("normalizedY").asDouble() : 0d;
                nWidth = jsonNode.has("normalizedWidth") ? jsonNode.get("normalizedWidth").asDouble() : MIN_NORMALIZED_WIDTH;
                nHeight = jsonNode.has("normalizedHeight") ? jsonNode.get("normalizedHeight").asDouble() : MIN_NORMALIZED_HEIGHT;

                nWidth = Math.max(nWidth, MIN_NORMALIZED_WIDTH);
                nHeight = Math.max(nHeight, MIN_NORMALIZED_HEIGHT);

                debugStatement = String.format("[QUALITYOFLIFE] Finished Reading file: %s", filePath);
                LOGGER.info(debugStatement);
            } catch(IOException exception)
            {
                nX = 0;
                nY = 0;
                nWidth = MIN_NORMALIZED_WIDTH;
                nHeight = MIN_NORMALIZED_HEIGHT;

                debugStatement = String.format("[QUALITYOFLIFE] Unable to parse file: %s", filePath);
                LOGGER.info(debugStatement);
            }
        }
        screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int x = getReal(nX, screenWidth);
        int y = getReal(nY, screenHeight);
        int width = getReal(nWidth, screenWidth);
        int height = getReal(nHeight, screenHeight);

        return new CustomWidget(x, y, width, height, message, filename, toRender);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(!toRender.getAsBoolean()) {active = false; return;}
        active = true;
        screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int realX = getReal(normalizedX, screenWidth);
        int realY = getReal(normalizedY, screenHeight);
        int realWidth = getReal(normalizedWidth, screenWidth);
        int realHeight = getReal(normalizedHeight, screenHeight);

        setX(realX);
        setY(realY);
        setWidth(realWidth);
        setHeight(realHeight);
        guiGraphics.fill(realX, realY, realX + realWidth, realY + realHeight, 0x80FFFFFF);
        float scaleWidth = 1.0f;
        float scaleHeight = 1.0f;
        int textWidth = Minecraft.getInstance().font.width(getMessage().getString()) + PADDING*2;
        int textHeight = Minecraft.getInstance().font.lineHeight + PADDING*2;
        if(textHeight > realHeight)
        {
            scaleHeight = (float) realHeight / textHeight;
        }
        if(textWidth > realWidth)
        {
            scaleWidth = (float) realWidth / textWidth;
        }
        float scale = Math.min(scaleWidth, scaleHeight);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(Minecraft.getInstance().font, getMessage().getString(),(realX + PADDING*scale + (float)realWidth/2 - textWidth*scale/2)/scale,(realY + PADDING*scale + (float)realHeight/2 - textHeight*scale/2)/scale,0xFF0000, false);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        if(isNearLeftTopEdge(mouseX, getReal(normalizedX, screenWidth), mouseY, getReal(normalizedY, screenHeight), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.UPLEFT;
            setCursor(3);
            return true;
        }
        else if(isNearLeftBottomEdge(mouseX, getReal(normalizedX, screenWidth), mouseY, getReal(normalizedY, screenHeight), getReal(normalizedHeight, screenHeight), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.DOWNLEFT;
            setCursor(4);
            return true;
        }
        else if(isNearRightTopEdge(mouseX, getReal(normalizedX, screenWidth), getReal(normalizedWidth, screenWidth), mouseY, getReal(normalizedY, screenHeight), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.UPRIGHT;
            setCursor(4);
            return true;
        }
        else if(isNearRightBottomEdge(mouseX, getReal(normalizedX, screenWidth), getReal(normalizedWidth, screenWidth), mouseY, getReal(normalizedY, screenHeight), getReal(normalizedHeight, screenHeight), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.DOWNRIGHT;
            setCursor(3);
            return true;
        }
        else if(isNearLeftEdge(mouseX, getReal(normalizedX, screenWidth), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.LEFT;
            setCursor(2);
            return true;
        }
        else if(isNearRightEdge(mouseX, getReal(normalizedX, screenWidth), getReal(normalizedWidth, screenWidth), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.RIGHT;
            setCursor(2);
            return true;
        }
        else if(isNearTopEdge(mouseY, getReal(normalizedY, screenHeight), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.UP;
            setCursor(1);
            return true;
        }
        else if(isNearBottomEdge(mouseY, getReal(normalizedY, screenHeight), getReal(normalizedHeight, screenHeight), RESIZE_MARGIN))
        {
            isResizing = true;
            resizeDirection = RESIZE_DIRECTION.DOWN;
            setCursor(1);
            return true;
        }
        else if(isMouseOver(mouseX, mouseY))
        {
            isDragging = true;
            mouseOffsetX = this.getX() - mouseX;
            mouseOffsetY = this.getY() - mouseY;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= getReal(normalizedX, screenWidth) && mouseY >= getReal(normalizedY, screenHeight) && mouseX < (getReal(normalizedX, screenWidth) + getReal(normalizedWidth, screenWidth)) && mouseY < (getReal(normalizedY, screenHeight) + getReal(normalizedHeight, screenHeight));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        if(isResizing)
        {
            switch(resizeDirection)
            {
                case LEFT:
                {
                    setLeft(mouseX);
                    break;
                }
                case RIGHT:
                {
                    setRight(mouseX);
                    break;
                }
                case UP:
                {
                    setUp(mouseY);
                    break;
                }
                case DOWN:
                {
                    setDown(mouseY);
                    break;
                }
                case UPLEFT:
                {
                    setUp(mouseY);
                    setLeft(mouseX);
                    break;

                }
                case UPRIGHT:
                {
                    setUp(mouseY);
                    setRight(mouseX);
                    break;
                }
                case DOWNLEFT:
                {
                    setDown(mouseY);
                    setLeft(mouseX);
                    break;
                }
                case DOWNRIGHT:
                {
                    setDown(mouseY);
                    setRight(mouseX);
                    break;
                }
            }
            return true;
        }
        if(!isDragging){return false;}

        if(mouseX + mouseOffsetX < 0)
        {
            this.setX(0);
            normalizedX = 0d;
        }
        else if(mouseX + mouseOffsetX + getReal(normalizedWidth, screenWidth) > screenWidth)
        {
            int newX = screenWidth-getReal(normalizedWidth, screenWidth);
            this.setX(newX);
            normalizedX = getNormalized(newX, screenWidth);
        }
        else
        {
            int newX = (int)(mouseX + mouseOffsetX);
            this.setX(newX);
            normalizedX = getNormalized(newX, screenWidth);
        }
        if(mouseY + mouseOffsetY < 0)
        {
            this.setY(0);
            normalizedY = 0d;
        }
        else if(mouseY + mouseOffsetY + getReal(normalizedHeight, screenHeight) > screenHeight)
        {
            int newY = screenHeight-getReal(normalizedHeight, screenHeight);
            this.setY(newY);
            normalizedY = getNormalized(newY, screenHeight);
        }
        else
        {
            int newY = (int)(mouseY + mouseOffsetY);
            this.setY(newY);
            normalizedY = getNormalized(newY, screenHeight);
        }
        return true;
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        isResizing = false;
        mouseOffsetX = 0;
        mouseOffsetY = 0;
        setCursor(0);
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private void setUp(double mouseY)
    {
        int newY = (int)mouseY;
        int maxY = getY() + getHeight();
        int newHeight = maxY - newY;

        double newNormalizedHeight = getNormalized(newHeight, screenHeight);
        newNormalizedHeight = Math.max(newNormalizedHeight, MIN_NORMALIZED_HEIGHT);
        int finalHeight = getReal(newNormalizedHeight, screenHeight);

        newY = maxY - finalHeight;
        newY = Math.max(0, newY);

        setY(newY);
        setHeight(finalHeight);

        normalizedY = getNormalized(newY, screenHeight);
        normalizedHeight = newNormalizedHeight;
    }
    private void setDown(double mouseY)
    {
        int newY = (int)mouseY;
        int smallestY = getY();
        int newHeight = newY - smallestY;

        double newNormalizedHeight = getNormalized(newHeight, screenHeight);
        newNormalizedHeight = Math.max(newNormalizedHeight, MIN_NORMALIZED_HEIGHT);
        int finalHeight = getReal(newNormalizedHeight, screenHeight);
        finalHeight = Math.min(finalHeight, screenHeight - smallestY);

        setHeight(finalHeight);
        normalizedHeight = newNormalizedHeight;
    }
    private void setLeft(double mouseX)
    {
        int newX = (int)mouseX;
        int maxX = getX() + getWidth();
        int newWidth = maxX - newX;

        double newNormalizedWidth = getNormalized(newWidth, screenWidth);
        newNormalizedWidth = Math.max(newNormalizedWidth, MIN_NORMALIZED_WIDTH);
        int finalWidth = getReal(newNormalizedWidth, screenWidth);

        newX = maxX - finalWidth;
        newX = Math.max(0, newX);

        setX(newX);
        setWidth(finalWidth);
        normalizedX = getNormalized(newX, screenWidth);
        normalizedWidth = newNormalizedWidth;
    }
    private void setRight(double mouseX)
    {
        int newX = (int)mouseX;
        int smallestX = getX();
        int newWidth = newX - smallestX;

        double newNormalizedWidth = getNormalized(newWidth, screenWidth);
        newNormalizedWidth = Math.max(newNormalizedWidth, MIN_NORMALIZED_WIDTH);
        int finalWidth = getReal(newNormalizedWidth,screenWidth);
        finalWidth = Math.min(finalWidth, screenWidth - smallestX);

        setWidth(finalWidth);
        normalizedWidth = newNormalizedWidth;
    }

    public void writeJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("normalizedX", normalizedX);
        jsonNode.put("normalizedY", normalizedY);
        jsonNode.put("normalizedWidth", normalizedWidth);
        jsonNode.put("normalizedHeight", normalizedHeight);

        String debugStatement;
        try
        {
            Files.createDirectories(MODCONFIGFOLDER);
        } catch (IOException e) {
            debugStatement = String.format("[QUALITYOFLIFE] Unable to create config directory: %s", MODCONFIGFOLDER);
            LOGGER.warn(debugStatement);
        }

        Path filePath = MODCONFIGFOLDER.resolve(filename);
        try
        {
            objectMapper.writeValue(filePath.toFile(), jsonNode);
            debugStatement = String.format("[QUALITYOFLIFE] Finished Writing file: %s", filePath);
            LOGGER.info(debugStatement);
        } catch(IOException exception) {
            debugStatement = String.format("[QUALITYOFLIFE] Unable to write file: %s", filePath);
            LOGGER.info(debugStatement);
        }

    }
}
