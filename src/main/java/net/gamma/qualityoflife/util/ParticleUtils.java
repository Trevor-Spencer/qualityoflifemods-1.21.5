package net.gamma.qualityoflife.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtils {
    public static final List<ParticleTracker> trackedParticles = new ArrayList<>();

    public static class ParticleTracker {
        public final BlockPos pos;
        public int ticksRemaining;

        public ParticleTracker(BlockPos pos, int ticks) {
            this.pos = pos;
            this.ticksRemaining = ticks;
        }
    }

}
