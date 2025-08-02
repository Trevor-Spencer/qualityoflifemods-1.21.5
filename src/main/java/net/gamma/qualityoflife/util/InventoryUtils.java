package net.gamma.qualityoflife.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minecraft.world.inventory.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;
import static net.gamma.qualityoflife.QualityofLifeMods.MODCONFIGFOLDER;

public class InventoryUtils {
    public static final Set<Integer> LOCKED_INVENTORY_SLOTS = new HashSet<>();// only 0-35
    public static final Set<Integer> LOCKED_ARMOR_SLOTS = new HashSet<>();// only 0-3
    public static final Map<Class<?>, Integer> MENU_OFFSET = Map.ofEntries(
            Map.entry(InventoryMenu.class, 9),
            Map.entry(FurnaceMenu.class, 3),
            Map.entry(BrewingStandMenu.class, 5),
            Map.entry(AnvilMenu.class, 3),
            Map.entry(BeaconMenu.class, 1),
            Map.entry(BlastFurnaceMenu.class, 3),
            Map.entry(CartographyTableMenu.class, 3),
            Map.entry(CrafterMenu.class, 9),
            Map.entry(CraftingMenu.class, 10),
            Map.entry(DispenserMenu.class, 9),
            Map.entry(EnchantmentMenu.class, 2),
            Map.entry(GrindstoneMenu.class, 3),
            Map.entry(HopperMenu.class, 5),
            Map.entry(HorseInventoryMenu.class, 2),
            Map.entry(LoomMenu.class, 4),
            Map.entry(MerchantMenu.class, 3),
            Map.entry(ShulkerBoxMenu.class, 27),
            Map.entry(SmithingMenu.class, 4),
            Map.entry(SmokerMenu.class, 3),
            Map.entry(StonecutterMenu.class, 2)
    );


    public static int getOffset(Class<?> className)
    {
        return MENU_OFFSET.getOrDefault(className, -1);
    }

    public static void readIn(String filename)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Path filePath = MODCONFIGFOLDER.resolve(filename);
        String debugStatement;
        if(!Files.exists(filePath))
        {
            debugStatement = String.format("[QUALITYOFLIFE] Filepath not found: %s", filePath);
            LOGGER.warn(debugStatement);
            return;
        }
        try
        {
            JsonNode jsonNode = objectMapper.readTree(filePath.toFile());
            if(jsonNode.has("inventorySlots") && jsonNode.get("inventorySlots").isArray())
            {
                for (JsonNode node : jsonNode.get("inventorySlots"))
                {
                    LOCKED_INVENTORY_SLOTS.add(node.asInt());
                }
            }
            if(jsonNode.has("armorSlots") && jsonNode.get("armorSlots").isArray())
            {
                for (JsonNode node : jsonNode.get("armorSlots"))
                {
                    LOCKED_ARMOR_SLOTS.add(node.asInt());
                }
            }

            debugStatement = String.format("[QUALITYOFLIFE] Finished Reading file: %s", filePath);
            LOGGER.info(debugStatement);
        } catch(IOException exception) {
            debugStatement = String.format("[QUALITYOFLIFE] Unable to parse file: %s", filePath);
            LOGGER.warn(debugStatement);
        }

    }
    public static void writeOut(String filename)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        ArrayNode inventoryArray = objectMapper.createArrayNode();
        ArrayNode armorArray = objectMapper.createArrayNode();
        for(int slot : LOCKED_ARMOR_SLOTS)
        {
            armorArray.add(slot);
        }for(int slot : LOCKED_INVENTORY_SLOTS)
        {
            inventoryArray.add(slot);
        }
        jsonNode.set("inventorySlots", inventoryArray);
        jsonNode.set("armorSlots", armorArray);

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
            LOGGER.warn(debugStatement);
        }
    }
}
