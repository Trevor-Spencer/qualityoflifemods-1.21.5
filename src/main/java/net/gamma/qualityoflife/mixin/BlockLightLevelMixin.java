package net.gamma.qualityoflife.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.gamma.qualityoflife.Config.FULLBRIGHT_ACTIVE;


@Mixin(LevelRenderer.class)
public class BlockLightLevelMixin {
    @Inject(method = "getLightColor(Lnet/minecraft/client/renderer/LevelRenderer$BrightnessGetter;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("HEAD"),
            cancellable = true)
    private static void changeBlockLightLevel(LevelRenderer.BrightnessGetter brightnessGetter,
                                            BlockAndTintGetter level,
                                            BlockState state,
                                            BlockPos pos,
                                            CallbackInfoReturnable<Integer> cir) {
        if(FULLBRIGHT_ACTIVE.get())
        {
            cir.setReturnValue((15 << 4) | (15 << 20));
            cir.cancel();
        }

    }
}
