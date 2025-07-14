package net.gamma.qualityoflife;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue FULLBRIGHT_ACTIVE = BUILDER
            .comment("Enable fullbright")
            .define("enablefullbright", true);
    public static final ModConfigSpec.BooleanValue COORDINATES_ACTIVE = BUILDER
            .comment("Enable Coordinates")
            .define("enablecoordinates", true);
    public static final ModConfigSpec.BooleanValue ZOOM_ACTIVE = BUILDER
            .comment("Enable Zoom")
            .define("enablezoom", true);
    public static final ModConfigSpec.BooleanValue MOVEMENT_ACTIVE = BUILDER
            .comment("Enable Movement Visuals")
            .define("enablemovement", true);
    public static final ModConfigSpec.BooleanValue SLAYER_ACTIVE = BUILDER
            .comment("Enable Slayer Tracker")
            .define("enableslayer", true);
    public static final ModConfigSpec.BooleanValue HOPPITY_ACTIVE = BUILDER
            .comment("Enable Hoppity Tracker")
            .define("enablehoppity", true);
    public static final ModConfigSpec.BooleanValue HUNTING_ACTIVE = BUILDER
                .comment("Enable Hunting Tracker")
                .define("enablehunting", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}