package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;

import static net.gamma.qualityoflife.Config.HOPPITY_ACTIVE;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;
import static net.gamma.qualityoflife.util.DisplayUtils.drawInfo;
import static net.gamma.qualityoflife.util.MathUtils.findNumeric;
import static net.gamma.qualityoflife.widget.ManagerWidget.HOPPITYWIDGET;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HoppityClientEvent {
    private static String allTimeChocolate = "All-Time Chocolate: ";
    private static String factoryLevelString = "Factory Level: ";
    private static String chocolateCurrentPrestigeString = "Chocolate this Prestige: ";
    private static String rabbitBarnLevelString = "Rabbit Barn Level: ";
    private static String barnStorageString = "Storage: ";
    private static String timeTowerString = "Time Tower Level: ";
    private static String towerStatus = "Status: ";
    private static String towerCharges = "Charges: ";

    private static final int HORIZONTALPADDING = 2;
    private static final int VERTICALPADDING = 2;

    @SubscribeEvent
    private static void hoppityScreen(ScreenEvent.Render.Post event)
    {
        if(!HOPPITY_ACTIVE.get()){return;}
        if(Minecraft.getInstance().player == null){return;}
        if(!onSkyblock){return;}
        Screen screen = event.getScreen();
        if (!screen.getTitle().getString().contains("Chocolate Factory")) {return;}
        if (screen instanceof AbstractContainerScreen<?> containerScreen)
        {
            AbstractContainerMenu inventory = containerScreen.getMenu();
            for (int i = 0; i < inventory.slots.size() - 36; i++)
            {
                Slot slot = inventory.slots.get(i);
                ItemStack item = slot.getItem();
                if (!item.is(Items.BLACK_STAINED_GLASS_PANE) && !item.is(Items.GRAY_STAINED_GLASS_PANE))
                {
                    //Time Tower Item
                    if(item.is(Items.CLOCK))
                    {
                        if(item.getDisplayName().getString().matches("\\[Time Tower [IVXLCDM]+]"))
                        {
                            String name = item.getDisplayName().getString();
                            timeTowerString = String.format("Time Tower Level: %d", findNumeric(name.split("Time Tower ")[1].split("]")[0]));
                            List<Component> tooltips = item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
                            if (!tooltips.isEmpty())
                            {
                                for (Component line : tooltips)
                                {
                                    String text = line.getString();
                                    if (text.contains("Status: "))
                                    {
                                        if (text.split("Status: ")[1].contains("INACTIVE"))
                                        {
                                            towerStatus = String.format("Status: %s", "INACTIVE");
                                        } else
                                        {
                                            towerStatus = String.format("Status: %s", "ACTIVE");
                                        }
                                    }
                                    else if (text.matches("Charges: \\d*/\\d*"))
                                    {
                                        towerCharges = String.format("Charges: %s", text.split(": ")[1].split("/")[0]);
                                    }
                                }
                            }
                        }
                    }
                    //Current Factory Level
                    else if(item.is(Items.DROPPER))
                    {
                        if(item.getDisplayName().getString().matches("\\[Chocolate Factory [IVXLCDM]+]"))
                        {
                            String name = item.getDisplayName().getString();
                            factoryLevelString = String.format("Factory Level: %d", findNumeric(name.split("Chocolate Factory ")[1].split("]")[0]));
                            List<Component> tooltips = item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
                            if (!tooltips.isEmpty())
                            {
                                for (Component line : tooltips)
                                {
                                    String text = line.getString();
                                    if (text.matches("Chocolate this Prestige: (\\d*,?)*"))
                                    {
                                        chocolateCurrentPrestigeString = String.format("Chocolate this Prestige: %s", text.split(": ")[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Barn Storage level
                    else if(item.is(Items.OAK_FENCE))
                    {
                        if(item.getDisplayName().getString().matches("\\[Rabbit Barn [IVXLCDM]+]"))
                        {
                            String name = item.getDisplayName().getString();
                            rabbitBarnLevelString = String.format("Rabbit Barn Level: %d",findNumeric(name.split("Rabbit Barn ")[1].split("]")[0]));
                            List<Component> tooltips = item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
                            if (!tooltips.isEmpty())
                            {
                                for (Component line : tooltips)
                                {
                                    String text = line.getString();
                                    if (text.matches("Your Barn: \\d*/\\d* Rabbits"))
                                    {
                                        barnStorageString = String.format("Barn Storage: %s", text.split("Your Barn: ")[1].split(" ")[0]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    List<Component> tooltips = item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
                    if (!tooltips.isEmpty())
                    {
                        for (Component line : tooltips)
                        {
                            String text = line.getString();
                            if (text.matches("All-time Chocolate: (\\d*,?)*"))
                            {
                                allTimeChocolate = String.format("All-Time Chocolate: %s", text.split(": ")[1]);
                                break;
                            }
                        }
                    }
                }
            }

            //Display Relevant contents
            GuiGraphics graphics = event.getGuiGraphics();
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            List<String> strings = List.of(allTimeChocolate, factoryLevelString, chocolateCurrentPrestigeString,
                     rabbitBarnLevelString, barnStorageString, timeTowerString, towerStatus, towerCharges);

            drawInfo(graphics,
                    screenWidth, screenHeight, HOPPITYWIDGET.normalizedX, HOPPITYWIDGET.normalizedY,
                    HOPPITYWIDGET.normalizedWidth, HOPPITYWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                    Minecraft.getInstance().font, strings, 0xFFFFFF, false, true);
        }
    }
}
