package net.gamma.qualityoflife.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.gamma.qualityoflife.Config.DISABLE_FOG;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;

@Mixin(FogRenderer.class)
public class RenderFogMixin {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void renderFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f fogColor, float renderDistance, boolean isFoggy, float partialTick, CallbackInfoReturnable<FogParameters> cir)
    {
        if(!onSkyblock){return;}
        if(!DISABLE_FOG.get()){return;}
        Level level = Minecraft.getInstance().level;
        Entity entity = camera.getEntity();
        if(entity instanceof LocalPlayer)
        {
            if(level != null && entity.isInWater())
            {
                cir.setReturnValue(FogParameters.NO_FOG);
                cir.cancel();
            }
            else if(level != null && (level.dimension() == Level.NETHER || level.dimension() == Level.END))
            {
                cir.setReturnValue(FogParameters.NO_FOG);
                cir.cancel();
            }
        }
    }
}
