package net.gamma.qualityoflife.widget;

import net.minecraft.network.chat.Component;

import static net.gamma.qualityoflife.Config.*;

public class ManagerWidget {
    public static CustomWidget COORDINATESWIDGET = CustomWidget.readIn("coordinatesWidget.json", Component.literal("Coordinate Display"), () -> COORDINATES_ACTIVE.get());
    public static CustomWidget MOVEMENTVISUALWIDGET = CustomWidget.readIn("movementVisualWidget.json", Component.literal("Movement Visual Display"), () -> MOVEMENT_ACTIVE.get());
    public static CustomWidget HOPPITYWIDGET = CustomWidget.readIn("hoppityWidget.json", Component.literal("Hoppity Display"), () -> HOPPITY_ACTIVE.get());
}
