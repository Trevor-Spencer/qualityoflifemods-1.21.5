package net.gamma.qualityoflife.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;
import static net.gamma.qualityoflife.keybinding.ModKeyBinding.LOCK_KEY;
import static net.gamma.qualityoflife.util.InventoryUtils.*;

@Mixin(AbstractContainerScreen.class)
public class InventoryBackgroundMixin{
    @Shadow @Final protected AbstractContainerMenu menu;
    private static final Map<String, Integer> RARITYTOCOLOR = Map.ofEntries(
            Map.entry("COMMON", 0x80FFFFFF),
            Map.entry("UNCOMMON", 0x8055FF55),
            Map.entry("RARE", 0x805555FF),
            Map.entry("EPIC",  0x80AA00AA),
            Map.entry("LEGENDARY", 0x80FFAA00),
            Map.entry("MYTHIC", 0x80FF55FF),
            Map.entry("DIVINE", 0x8055FFFF),
            Map.entry("SPECIAL", 0x80FF5555),
            Map.entry("VERY SPECIAL", 0x80FF5555),
            Map.entry("ULTIMATE", 0x80AA0000),
            Map.entry("ADMIN", 0x80AA0000)
    );
    private static boolean keyRecentlyPressed = false;



    private void processSpecificInventory(GuiGraphics graphics, Slot slot)
    {
        int offset = getOffset(menu.getClass());
        if(menu instanceof InventoryMenu)
        {
            if(LOCKED_INVENTORY_SLOTS.contains(slot.index - offset) || LOCKED_ARMOR_SLOTS.contains(slot.index - 5))
            {
                graphics.hLine(slot.x, slot.x+15, slot.y, 0xFF000000);
                graphics.hLine(slot.x, slot.x+15, slot.y+15, 0xFF000000);
                graphics.vLine(slot.x, slot.y, slot.y+15, 0xFF000000);
                graphics.vLine(slot.x+15, slot.y, slot.y+15, 0xFF000000);
            }
        }
        else if(offset != -1)
        {
            if(LOCKED_INVENTORY_SLOTS.contains(slot.index - offset))
            {
                graphics.hLine(slot.x, slot.x+15, slot.y, 0xFF000000);
                graphics.hLine(slot.x, slot.x+15, slot.y+15, 0xFF000000);
                graphics.vLine(slot.x, slot.y, slot.y+15, 0xFF000000);
                graphics.vLine(slot.x+15, slot.y, slot.y+15, 0xFF000000);
            }
        }
        else if(menu instanceof ChestMenu)
        {
            int rowCount = ((ChestMenu) menu).getRowCount();
            int chestSlots = rowCount * 9;
            if(LOCKED_INVENTORY_SLOTS.contains(slot.index - chestSlots))
            {
                graphics.hLine(slot.x, slot.x+15, slot.y, 0xFF000000);
                graphics.hLine(slot.x, slot.x+15, slot.y+15, 0xFF000000);
                graphics.vLine(slot.x, slot.y, slot.y+15, 0xFF000000);
                graphics.vLine(slot.x+15, slot.y, slot.y+15, 0xFF000000);
            }
        }
    }

    private void toggleInventorySlots(int slot, int offset)
    {
        if(LOCKED_INVENTORY_SLOTS.contains(slot - offset))
        {
            LOCKED_INVENTORY_SLOTS.remove(slot - offset);
        }
        else
        {
            LOCKED_INVENTORY_SLOTS.add(slot - offset);
        }
    }
    private void toggleArmorSlots(int slot)
    {
        if(LOCKED_ARMOR_SLOTS.contains(slot - 5))
        {
            LOCKED_ARMOR_SLOTS.remove(slot - 5);
        }
        else
        {
            LOCKED_ARMOR_SLOTS.add(slot - 5);
        }
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci)
    {
//        if(!onSkyblock){return;}
        int offset = getOffset(menu.getClass());
        if(menu instanceof InventoryMenu)
        {
            if(LOCKED_INVENTORY_SLOTS.contains(slotId - offset) || LOCKED_ARMOR_SLOTS.contains(slotId - 5))
            {
                ci.cancel();
            }
        }
        else if(offset != -1)
        {
            if(LOCKED_INVENTORY_SLOTS.contains(slotId - offset))
            {
                ci.cancel();
            }
        }
        else if(menu instanceof ChestMenu)
        {
            int rowCount = ((ChestMenu) menu).getRowCount();
            int chestSlots = rowCount * 9;
            if(LOCKED_INVENTORY_SLOTS.contains(slotId - chestSlots))
            {
                ci.cancel();
            }
        }
    }


    @Inject(method = "render", at = @At("HEAD"))
    private void render(GuiGraphics p_283479_, int p_283661_, int p_281248_, float p_281886_, CallbackInfo ci)
    {
//        if(!onSkyblock){return;}
        Slot value = ((AbstractContainerScreen<?>) (Object) this).getSlotUnderMouse();
        if(value == null){return;}
        long window = Minecraft.getInstance().getWindow().getWindow();
        boolean keyDown = InputConstants.isKeyDown(window, LOCK_KEY.getKey().getValue());
        if(keyDown && !keyRecentlyPressed)
        {
            keyRecentlyPressed = true;
            int offset = getOffset(menu.getClass());
            if(menu instanceof InventoryMenu)
            {
                if(value.index >= 5 && value.index <= 8)
                {
                    toggleArmorSlots(value.index);
                }
                else if(value.index >= 9 && value.index <= 44)
                {
                    toggleInventorySlots(value.index, offset);
                }
            }
            else if(offset != -1)
            {
                toggleInventorySlots(value.index, offset);
            }
            else if(menu instanceof ChestMenu)
            {
                int rowCount = ((ChestMenu) menu).getRowCount();
                int chestSlots = rowCount * 9;
                toggleInventorySlots(value.index, chestSlots);
            }
        }
        if(!keyDown && keyRecentlyPressed)
        {
            keyRecentlyPressed = false;
        }
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void renderLock(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci)
    {
        processSpecificInventory(guiGraphics, slot);
    }

    @Inject(method = "renderSlotContents", at = @At("HEAD"))
    private void renderBackground(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, String countString, CallbackInfo ci)
    {
//        if(!onSkyblock){return;}
        if(!itemstack.isEmpty())
        {
            List<Component> lines =  itemstack.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
            if(lines.isEmpty()){return;}
            for(int i = lines.size()-1; i >= 0; i--)
            {
                String text = lines.get(i).getString();
                for(String rarity : RARITYTOCOLOR.keySet().stream().sorted((a,b) -> Integer.compare(b.length(), a.length())).toList())
                {
                    if(text.contains(rarity))
                    {
                        int color = RARITYTOCOLOR.getOrDefault(rarity, 0x00FFFFFF);
                        if(color != 0x00FFFFFF)
                        {
                            guiGraphics.fill(slot.x,slot.y,slot.x+16,slot.y+16, color);
                            return;
                        }
                    }
                }
            }
        }
    }
}
