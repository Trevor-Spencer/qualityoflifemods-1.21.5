package net.gamma.qualityoflife.mixin;

import net.gamma.qualityoflife.util.ParticleUtils;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.gamma.qualityoflife.Config.HUNTING_ACTIVE;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    @Inject(method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("RETURN"), cancellable = true)
    private void onCreateParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
        if(!onSkyblock){return;}
        if(!HUNTING_ACTIVE.get()){return;}
        Particle particle = cir.getReturnValue();
        if (particle != null && particleData == ParticleTypes.CRIT) {
            int lifetime = particle.getLifetime();

            BlockPos pos = BlockPos.containing(particle.getPos().x, particle.getPos().y, particle.getPos().z);
            ParticleUtils.trackedParticles.add(new ParticleUtils.ParticleTracker(pos, lifetime));
        }
    }
}