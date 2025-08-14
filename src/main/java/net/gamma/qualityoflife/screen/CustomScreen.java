package net.gamma.qualityoflife.screen;

import net.gamma.qualityoflife.widget.CustomWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.gamma.qualityoflife.widget.ManagerWidget.*;

public class CustomScreen extends Screen {
    public static boolean screenOpen = false;
    public CustomScreen() {
        super(Component.literal("Modify Mods Screen"));
    }

    @Override
    protected void init()
    {
        super.init();
        for(CustomWidget widget : WIDGETS)
        {
            addRenderableWidget(widget);
        }
        screenOpen = true;
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
    public void onClose() {
        writeJsons();
        screenOpen = false;
        super.onClose();
    }
}
