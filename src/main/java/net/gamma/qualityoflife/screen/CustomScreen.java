package net.gamma.qualityoflife.screen;

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
        addRenderableWidget(COORDINATESWIDGET);
        addRenderableWidget(MOVEMENTVISUALWIDGET);
        addRenderableWidget(HOPPITYWIDGET);
        addRenderableWidget(SLAYERWIDGET);
        addRenderableWidget(BEACONWIDGET);
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
        COORDINATESWIDGET.writeJson("coordinatesWidget.json");
        MOVEMENTVISUALWIDGET.writeJson("movementVisualWidget.json");
        HOPPITYWIDGET.writeJson("hoppityWidget.json");
        SLAYERWIDGET.writeJson("slayerWidget.json");
        BEACONWIDGET.writeJson("beaconWidget.json");
        screenOpen = false;
        super.onClose();
    }
}
