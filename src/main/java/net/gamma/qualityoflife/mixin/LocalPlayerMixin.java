package net.gamma.qualityoflife.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.gamma.qualityoflife.util.InventoryUtils.LOCKED_INVENTORY_SLOTS;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void onDrop(boolean fullStack, CallbackInfoReturnable<Boolean> cir)
    {
        LocalPlayer player = (LocalPlayer)(Object)this;
        Inventory inventory = player.getInventory();
        if(LOCKED_INVENTORY_SLOTS.contains(inventory.getSelectedSlot()+27))
        {
            cir.setReturnValue(false);
        }
    }
}
