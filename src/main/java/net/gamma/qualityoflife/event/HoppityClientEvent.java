package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
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

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HoppityClientEvent {
    private static String msg = "All-Time Chocolate: ";
    private static String allTimeChocolate = "0";

    @SubscribeEvent
    private static void hoppityScreen(ScreenEvent.Render.Post event)
    {
        if(HOPPITY_ACTIVE.get())
        {
            if(Minecraft.getInstance().player != null) {
                if (event.getScreen() != null) {
                    Screen screen = event.getScreen();
                    if (screen.getTitle().getString().contains("Chocolate Factory")) {
                        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
                            AbstractContainerMenu inventory = containerScreen.getMenu();
                            for (int i = 0; i < inventory.slots.size() - 36; i++) {
                                Slot slot = inventory.slots.get(i);
                                ItemStack item = slot.getItem();
                                if (!item.is(Items.BLACK_STAINED_GLASS_PANE) && !item.is(Items.GRAY_STAINED_GLASS_PANE)) {
                                    List<Component> tooltips = item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
                                    if (!tooltips.isEmpty()) {
                                        for (Component line : tooltips) {
                                            String text = line.getString();
                                            // Parse text here, e.g.:
                                            if (text.contains("All-time Chocolate:")) {
                                                // Extract the value or do something
                                                allTimeChocolate = text.split(": ")[1];
                                                msg = "All-Time Chocolate: " + allTimeChocolate;
                                                event.getGuiGraphics().drawString(Minecraft.getInstance().font, msg, 2, 200, 0xFFFFFF);
                                                break; // stop after first relevant line
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
