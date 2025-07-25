package net.gamma.qualityoflife.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;

@Mixin(AbstractContainerScreen.class)
public class InventoryBackgroundMixin{
    private static final Map<String, Integer> rarityToColor = Map.ofEntries(
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


    
    @Inject(method = "renderSlotContents", at = @At("HEAD"))
    private void renderBackground(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, String countString, CallbackInfo ci)
    {
        if(!onSkyblock){return;}
        if(!itemstack.isEmpty())
        {
            List<Component> lines =  itemstack.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
            if(lines.isEmpty()){return;}
            for(int i = lines.size()-1; i >= 0; i--)
            {
                String text = lines.get(i).getString();
                for(String rarity : rarityToColor.keySet().stream().sorted((a,b) -> Integer.compare(b.length(), a.length())).toList())
                {
                    if(text.contains(rarity))
                    {
                        int color = rarityToColor.getOrDefault(rarity, 0x00FFFFFF);
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
