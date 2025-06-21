package net.gamma.qualityoflife;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

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

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
