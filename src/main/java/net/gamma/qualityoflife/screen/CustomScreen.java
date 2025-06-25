package net.gamma.qualityoflife.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.gamma.qualityoflife.widget.ManagerWidget.COORDINATESWIDGET;
import static net.gamma.qualityoflife.widget.ManagerWidget.MOVEMENTVISUALWIDGET;

public class CustomScreen extends Screen {
    public CustomScreen() {
        super(Component.literal("Modify Mods Screen"));
    }

    @Override
    protected void init()
    {
        super.init();
        addRenderableWidget(COORDINATESWIDGET);
        addRenderableWidget(MOVEMENTVISUALWIDGET);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

}
