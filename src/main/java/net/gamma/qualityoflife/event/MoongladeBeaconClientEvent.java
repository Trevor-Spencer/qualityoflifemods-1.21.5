package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.gamma.qualityoflife.Config.HUNTING_ACTIVE;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;
import static net.gamma.qualityoflife.util.DisplayUtils.drawInfo;
import static net.gamma.qualityoflife.widget.ManagerWidget.BEACONWIDGET;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MoongladeBeaconClientEvent {
    //Variables for Tune Frequency
    private static List<Item> items = new ArrayList<>();
    private static String color = "Color: ";
    private static String speed = "Speed: ";
    private static int tickDelay = 0;
    //Variables for Signal Upgrade
    private static List<Item> enchantedItems = new ArrayList<>();
    private static List<Item> regularItems = new ArrayList<>();
    private static String enchantedColor = "Enchanted Color: ";
    private static String regularColor = "Regular Color: ";
    private static String enchantedSpeed = "Enchanted Speed: ";
    private static String regularSpeed = "Regular Speed: ";
    private static int enchantedDelay = 0;
    private static int regularDelay = 0;

    private static final int TOLERANCE = 4;
    private static final int HORIZONTALPADDING = 2;
    private static final int VERTICALPADDING = 2;

    private static final Map<Integer, Integer> delayToSpeed = Map.of(
            54, 1,
            44, 2,
            34, 3,
            24, 4,
            14, 5

    );

    @SubscribeEvent
    private static void tick(ClientTickEvent.Post event) {
        if(!HUNTING_ACTIVE.get()){return;}
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {return;}
        if(!onSkyblock){return;}
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            String title = screen.getTitle().getString();
            if (!title.contains("Tune Frequency") && !title.contains("Upgrade Signal Strength")) {
                tickDelay = 0;
                regularDelay = 0;
                enchantedDelay = 0;
                return;
            }
            AbstractContainerMenu inventory = screen.getMenu();
            if (title.contains("Tune Frequency"))
            {
                tuneFrequency(inventory);
            }
            else
            {
                upgradeStrength(inventory);
            }
        }
    }

    @SubscribeEvent
    private static void screenClose(ScreenEvent.Closing event)
    {
        if(event.getScreen().getTitle().getString().contains("Tune Frequency"))
        {
            tickDelay = 0;
            color = "Color: ";
            speed = "Speed: ";
            items.clear();
        }
        else if(event.getScreen().getTitle().getString().contains("Upgrade Signal Strength"))
        {
            enchantedDelay = 0;
            regularDelay = 0;
            enchantedSpeed = "Enchanted Speed: ";
            regularSpeed = "Regular Speed: ";
            enchantedColor = "Enchanted Color: ";
            regularColor = "Regular Color: ";
            enchantedItems.clear();
            regularItems.clear();
        }
    }

    @SubscribeEvent
    private static void render(ScreenEvent.Render.Post event)
    {
        if(!HUNTING_ACTIVE.get() || Minecraft.getInstance().level == null || !onSkyblock){return;}
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            String title = screen.getTitle().getString();
            if (!title.contains("Tune Frequency") && !title.contains("Upgrade Signal Strength")) {return;}
            if (screen.getTitle().getString().contains("Tune Frequency"))
            {
                GuiGraphics graphics = event.getGuiGraphics();

                int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                List<String> strings = List.of(color, speed);

                drawInfo(graphics,
                        screenWidth, screenHeight, BEACONWIDGET.normalizedX, BEACONWIDGET.normalizedY,
                        BEACONWIDGET.normalizedWidth, BEACONWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                        Minecraft.getInstance().font, strings, 0xFFFFFF, false, true);
            }
            if(screen.getTitle().getString().contains("Upgrade Signal Strength"))
            {
                GuiGraphics graphics = event.getGuiGraphics();

                int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                List<String> strings = List.of(enchantedColor, enchantedSpeed, regularColor, regularSpeed);

                drawInfo(graphics,
                        screenWidth, screenHeight, BEACONWIDGET.normalizedX, BEACONWIDGET.normalizedY,
                        BEACONWIDGET.normalizedWidth, BEACONWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                        Minecraft.getInstance().font, strings, 0xFFFFFF, false, true);
            }
        }
    }

    private static void tuneFrequency(AbstractContainerMenu inventory)
    {
        if(items.isEmpty())
        {
            for(int i = 10; i < 17; i++)
            {
                Slot slot = inventory.slots.get(i);
                ItemStack item = slot.getItem();

                items.add(item.getItem());
                if(!item.is(Items.GRAY_STAINED_GLASS_PANE))
                {
                        color = String.format("Color: %s", item.getItemName().getString().replace(" Stained Glass Pane", ""));
                        tickDelay = 0;
                }
            }
        }
        else
        {
            List<Item> tempList = new ArrayList<>();
            for(int i = 10; i < 17; i++)
            {
                Slot slot = inventory.slots.get(i);
                ItemStack item = slot.getItem();

                tempList.add(item.getItem());
            }
            if(!tempList.equals(items))
            {
                for(Item item : items)
                {
                    if(item != Items.GRAY_STAINED_GLASS_PANE)
                    {
                        color = String.format("Color: %s", item.getName().getString().replace(" Stained Glass Pane", ""));
                    }
                }
                speed = String.format("Speed: %d", getSpeed(tickDelay));
                items = tempList;
                tickDelay = 0;
            }
            else
            {
                tickDelay++;
            }
        }
    }

    private static void upgradeStrength(AbstractContainerMenu inventory)
    {
        if(enchantedItems.isEmpty() && regularItems.isEmpty())
        {
            for(int i = 10; i < 17; i++)
            {
                Slot slot = inventory.slots.get(i);
                ItemStack item = slot.getItem();

                if(item.is(Items.GRAY_STAINED_GLASS_PANE))
                {
                    enchantedItems.add(item.getItem());
                    regularItems.add(item.getItem());
                }
                else if(item.isEnchanted() || item.hasFoil())
                {
                    enchantedColor = String.format("Enchanted Color: %s", item.getItemName().getString().replace(" Stained Glass Pane", ""));
                    enchantedItems.add(item.getItem());
                    regularItems.add(Items.GRAY_STAINED_GLASS_PANE);
                }
                else
                {
                    regularColor = String.format("Regular Color: %s", item.getItemName().getString().replace(" Stained Glass Pane", ""));
                    regularItems.add(item.getItem());
                    enchantedItems.add(Items.GRAY_STAINED_GLASS_PANE);
                }
            }
        }
        else
        {
            List<Item> tempEnchanted = new ArrayList<>();
            List<Item> tempRegular = new ArrayList<>();
            for(int i = 10; i < 17; i++)
            {
                Slot slot = inventory.slots.get(i);
                ItemStack item = slot.getItem();

                if(item.is(Items.GRAY_STAINED_GLASS_PANE))
                {
                    tempEnchanted.add(item.getItem());
                    tempRegular.add(item.getItem());
                }
                else if(item.isEnchanted() || item.hasFoil())
                {
                    tempEnchanted.add(item.getItem());
                    tempRegular.add(Items.GRAY_STAINED_GLASS_PANE);
                }
                else
                {
                    tempRegular.add(item.getItem());
                    tempEnchanted.add(Items.GRAY_STAINED_GLASS_PANE);
                }
            }
            if(hasColor(tempRegular))
            {
                if(!tempRegular.equals(regularItems))
                {
                    for(Item item : tempRegular)
                    {
                        if(item != Items.GRAY_STAINED_GLASS_PANE)
                        {
                            regularColor = String.format("Color: %s", item.getName().getString().replace(" Stained Glass Pane", ""));
                        }
                    }
                    regularSpeed = String.format("Regular Speed: %d", getSpeed(regularDelay));
                    regularItems = tempRegular;
                    regularDelay = 0;
                }
                else
                {
                    regularDelay++;
                }
            }
            else
            {
                regularDelay++;
            }
            if(hasColor(tempEnchanted))
            {
                if(!tempEnchanted.equals(enchantedItems))
                {
                    for(Item item : tempEnchanted)
                    {
                        if(item != Items.GRAY_STAINED_GLASS_PANE)
                        {
                            enchantedColor = String.format("Color: %s", item.getName().getString().replace(" Stained Glass Pane", ""));
                        }
                    }
                    enchantedSpeed = String.format("Enchanted Speed: %d", getSpeed(enchantedDelay));
                    enchantedItems = tempEnchanted;
                    enchantedDelay = 0;
                }
                else
                {
                    enchantedDelay++;
                }
            }
            else
            {
                enchantedDelay++;
            }
        }
    }

    private static int getSpeed(int delay)
    {
        for(Map.Entry<Integer, Integer> entry : delayToSpeed.entrySet())
        {
            if(Math.abs(delay - entry.getKey()) <= TOLERANCE)
            {
                return entry.getValue();
            }
        }
        return -1;
    }

    private static boolean hasColor(List<Item> list)
    {
        for(Item item : list)
        {
            if(item != Items.GRAY_STAINED_GLASS_PANE)
            {
                return true;
            }
        }
        return false;
    }



}
