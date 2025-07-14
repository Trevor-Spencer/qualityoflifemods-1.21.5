package net.gamma.qualityoflife.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.gamma.qualityoflife.Config.HUNTING_ACTIVE;

@Mixin(Entity.class)
public abstract class HuntingMixin {
        @Shadow
        private boolean hasGlowingTag;
        @Shadow
        protected abstract void setSharedFlag(int flag, boolean value);

        @Inject(method = "setGlowingTag", at = @At("HEAD"), cancellable = true)
        private void onSetGlowingTag(boolean glowing, CallbackInfo ci) {
            if(HUNTING_ACTIVE.get())
            {
                this.hasGlowingTag = glowing;
                this.setSharedFlag(6, glowing);

                ci.cancel();
            }
        }
}
