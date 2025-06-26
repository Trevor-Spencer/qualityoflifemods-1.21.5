package net.gamma.qualityoflife.widget;

import net.minecraft.network.chat.Component;

import static net.gamma.qualityoflife.Config.COORDINATES_ACTIVE;
import static net.gamma.qualityoflife.Config.MOVEMENT_ACTIVE;

public class ManagerWidget {
    public static CustomWidget COORDINATESWIDGET = CustomWidget.readIn("coordinatesWidget.json", Component.literal("Coordinate Display"), () -> COORDINATES_ACTIVE.get());
    public static CustomWidget MOVEMENTVISUALWIDGET = CustomWidget.readIn("movementVisualWidget.json", Component.literal("Movement Visual Display"), () -> MOVEMENT_ACTIVE.get());
}
