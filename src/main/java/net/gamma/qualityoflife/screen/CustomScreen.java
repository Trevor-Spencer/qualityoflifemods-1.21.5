package net.gamma.qualityoflife.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Checkbox;

import static net.gamma.qualityoflife.Config.*;

public class CustomScreen extends Screen {
    public CustomScreen() {
        super(Component.literal("Modify Mods Screen"));
    }

    @Override
    protected void init()
    {
        int screenWidth = this.width;
        int screenHeight = this.height;
        int screenPadding = 30;
        int totalBoxes = 6;
        int columnCount = 3;
        int totalPixelsForColumnsAndPadding = screenWidth - 2*screenPadding;
        int totalPixelsForRowsAndPadding = screenHeight - 2*screenPadding;
        int columnsWidth = totalPixelsForColumnsAndPadding / columnCount;
        int columnMaxWidth = columnsWidth;
        int rowsHeight = totalPixelsForRowsAndPadding / (totalBoxes/columnCount);
        int rowMaxWidth = rowsHeight;

        GridLayout layout = new GridLayout(0, 0);
        layout.columnSpacing(10);
        layout.rowSpacing(10);
        Checkbox checkboxFullbright = Checkbox.builder(Component.literal("Turn on Fullbright"), Minecraft.getInstance().font)
                .maxWidth(columnMaxWidth)
                .selected(FULLBRIGHT_ACTIVE.get())
                .onValueChange(((checkbox1, value) -> FULLBRIGHT_ACTIVE.set(value)))
                .build();
        Checkbox checkboxCoordinates = Checkbox.builder(Component.literal("Turn on Coordinates"), Minecraft.getInstance().font)
                .maxWidth(columnMaxWidth)
                .selected(COORDINATES_ACTIVE.get())
                .onValueChange(((checkbox1, value) -> COORDINATES_ACTIVE.set(value)))
                .build();
        Checkbox checkboxZoom = Checkbox.builder(Component.literal("Turn on Zoom"), Minecraft.getInstance().font)
                .maxWidth(columnMaxWidth)
                .selected(ZOOM_ACTIVE.get())
                .onValueChange(((checkbox1, value) -> ZOOM_ACTIVE.set(value)))
                .build();
        Checkbox checkboxMovement = Checkbox.builder(Component.literal("Turn on Movement Visuals"), Minecraft.getInstance().font)
                .maxWidth(columnMaxWidth)
                .selected(MOVEMENT_ACTIVE.get())
                .onValueChange(((checkbox1, value) -> MOVEMENT_ACTIVE.set(value)))
                .build();
        Checkbox checkboxSlayer = Checkbox.builder(Component.literal("Turn on Slayer Tracker"), Minecraft.getInstance().font)
                .maxWidth(columnMaxWidth)
                .selected(SLAYER_ACTIVE.get())
                .onValueChange(((checkbox1, value) -> SLAYER_ACTIVE.set(value)))
                .build();
        Checkbox checkboxHoppity = Checkbox.builder(Component.literal("Turn on Hoppity Tracker"), Minecraft.getInstance().font)
                .maxWidth(columnMaxWidth)
                .selected(HOPPITY_ACTIVE.get())
                .onValueChange(((checkbox1, value) -> HOPPITY_ACTIVE.set(value)))
                .build();

        layout.addChild(checkboxFullbright, 0, 0);
        layout.addChild(checkboxCoordinates, 0, 1);
        layout.addChild(checkboxZoom, 0, 2);
        layout.addChild(checkboxMovement, 1, 0);
        layout.addChild(checkboxSlayer, 1 ,1);
        layout.addChild(checkboxHoppity, 1, 2);
        layout.arrangeElements();

        int layoutWidth = layout.getWidth();
        int layoutHeight = layout.getHeight();
        layout.setPosition((this.width - layoutWidth) / 2, (this.height - layoutHeight) / 2);

        layout.visitWidgets(this::addRenderableWidget);
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
