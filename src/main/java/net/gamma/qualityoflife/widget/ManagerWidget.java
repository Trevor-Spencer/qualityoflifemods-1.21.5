package net.gamma.qualityoflife.widget;

import net.minecraft.network.chat.Component;

import static net.gamma.qualityoflife.Config.COORDINATES_ACTIVE;
import static net.gamma.qualityoflife.Config.MOVEMENT_ACTIVE;

public class ManagerWidget {
    public static CustomWidget COORDINATESWIDGET = new CustomWidget(2,2,100,30, Component.literal("Coordinate Display"), () -> COORDINATES_ACTIVE.get());
    static{
        COORDINATESWIDGET.active = true;
        COORDINATESWIDGET.visible = true;
    }
    public static CustomWidget MOVEMENTVISUALWIDGET = new CustomWidget(50, 100, 50, 30, Component.literal("Movement Visual Display"), () -> MOVEMENT_ACTIVE.get());
    static{
        MOVEMENTVISUALWIDGET.active = true;
        MOVEMENTVISUALWIDGET.visible = true;
    }
}
