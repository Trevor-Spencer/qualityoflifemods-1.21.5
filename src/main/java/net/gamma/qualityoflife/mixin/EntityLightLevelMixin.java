package net.gamma.qualityoflife.mixin;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.gamma.qualityoflife.Config.FULLBRIGHT_ACTIVE;


@Mixin(EntityRenderer.class)
public class EntityLightLevelMixin<T extends Entity> {

    @Inject(method = "getPackedLightCoords",
            at = @At("HEAD"),
            cancellable = true)
    private void changeBlockLightLevel(T entity, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        if(FULLBRIGHT_ACTIVE.get())
        {
            cir.setReturnValue(0xF000F0);
            cir.cancel();
        }

    }
}
