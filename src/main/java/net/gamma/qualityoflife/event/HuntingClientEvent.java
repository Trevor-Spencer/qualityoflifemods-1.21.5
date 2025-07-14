package net.gamma.qualityoflife.event;

import com.mojang.blaze3d.vertex.*;
import net.gamma.qualityoflife.QualityofLifeMods;
import net.gamma.qualityoflife.util.ParticleUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.*;

import static net.gamma.qualityoflife.Config.HUNTING_ACTIVE;
import static net.gamma.qualityoflife.util.ParticleUtils.trackedParticles;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HuntingClientEvent {
    private static final List<Entity> scannedEntities = new ArrayList<>();
    private static final Set<BlockPos> scannedParticles = new HashSet<>();

    private static final ResourceLocation RED_TEXTURE = ResourceLocation.fromNamespaceAndPath(QualityofLifeMods.MOD_ID, "textures/blanktexture.png");
    private static final RenderType SOLID_TRANSLUCENT_RENDERTYPE = RenderType.entityTranslucent(RED_TEXTURE);

    private static final int RED = 0xFF;
    private static final int GREEN = 0xFF;
    private static final int BLUE = 0xFF;
    private static final int ALPHA = 0x80;

    private static final int SCANDELAY = 20;
    private static int timer = 0;
    @SubscribeEvent
    private static void tick(ClientTickEvent.Post event) {
        if (!HUNTING_ACTIVE.get()) {
            return;
        }
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        if (!Minecraft.getInstance().level.isClientSide) {
            return;
        }
        if (timer < SCANDELAY) {
            timer++;
            return;
        }
        timer = 0;
        scannedEntities.clear();
        processMob(EntityType.SHULKER, ChatFormatting.LIGHT_PURPLE);
        processMob(EntityType.TURTLE, ChatFormatting.GREEN);
        trackedParticles.removeIf(p -> ((p.ticksRemaining -= SCANDELAY) <= 0));
    }

    private static void processMob(EntityType<?> entityType, ChatFormatting color)
    {
        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        Scoreboard scoreboard = Minecraft.getInstance().level.getScoreboard();
        if (scoreboard == null) {return;}

        String teamName = "qol_hunting_" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "_highlight";
        Team team = scoreboard.getPlayerTeam(teamName);
        if (team == null)
        {
            team = scoreboard.addPlayerTeam(teamName);
            if (team instanceof PlayerTeam playerTeam)
            {
                playerTeam.setColor(color);
            }
        }

        AABB radius = new AABB(Minecraft.getInstance().player.getOnPos()).inflate(50D);
        List<Entity> nearbyEntities = level.getEntities(
                player,
                radius,
                entity -> entity.getType() == entityType);

        for (Entity entity : nearbyEntities)
        {
            if(scannedEntities.contains(entity)){continue;}
            if (team instanceof PlayerTeam playerTeam) {
                scoreboard.addPlayerToTeam(entity.getStringUUID(), playerTeam);
            }
            entity.setGlowingTag(true);
            scannedEntities.add(entity);
        }
    }

    @SubscribeEvent
    private static void renderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();
        scannedParticles.clear();

        VertexConsumer buffer = mc.renderBuffers().bufferSource()
                .getBuffer(SOLID_TRANSLUCENT_RENDERTYPE);

        for (ParticleUtils.ParticleTracker particleTracker : trackedParticles) {
            if(scannedParticles.isEmpty() || !scannedParticles.contains(particleTracker.pos))
            {

                BlockPos pos = particleTracker.pos;
                scannedParticles.add(pos);
                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();

                double minX = x - camPos.x;
                double minY = y - camPos.y;
                double minZ = z - camPos.z;
                double maxX = minX + 1;
                double maxY = minY + 1;
                double maxZ = minZ + 1;

                //North Face
                drawSolidRedFace(buffer,
                        maxX, minY, minZ,
                        minX, minY, minZ,
                        minX, maxY, minZ,
                        maxX, maxY, minZ,
                        RED, GREEN, BLUE, ALPHA);
                //South Face
                drawSolidRedFace(buffer,
                        maxX, minY, maxZ,
                        minX, minY, maxZ,
                        minX, maxY, maxZ,
                        maxX, maxY, maxZ,
                        RED, GREEN, BLUE, ALPHA);
                //East Face
                drawSolidRedFace(buffer,
                        maxX,minY,minZ,
                        maxX,minY,maxZ,
                        maxX,maxY,maxZ,
                        maxX,maxY,minZ,
                        RED, GREEN, BLUE, ALPHA);
                //West Face
                drawSolidRedFace(buffer,
                        minX,minY,minZ,
                        minX,minY,maxZ,
                        minX,maxY,maxZ,
                        minX,maxY,minZ,
                        RED, GREEN, BLUE, ALPHA);
                //Bottom Face
                drawSolidRedFace(buffer,
                        maxX,minY,minZ,
                        minX,minY,minZ,
                        minX,minY,maxZ,
                        maxX,minY,maxZ,
                        RED, GREEN, BLUE, ALPHA);
                //Top Face
                drawSolidRedFace(buffer,
                        maxX,maxY,minZ,
                        minX,maxY,minZ,
                        minX,maxY,maxZ,
                        maxX,maxY,maxZ,
                        RED, GREEN, BLUE, ALPHA);
            }

        }

        // Important: flush the buffer
        mc.renderBuffers().bufferSource().endBatch(SOLID_TRANSLUCENT_RENDERTYPE);
    }


    private static void drawSolidRedFace(VertexConsumer buffer,
                                         double x1, double y1, double z1,
                                         double x2, double y2, double z2,
                                         double x3, double y3, double z3,
                                         double x4, double y4, double z4,
                                         int red, int green, int blue, int alpha)
    {
        buffer.addVertex((float)x1, (float)y1, (float)z1).setColor(red, green, blue, alpha)
                .setUv(0.5f, 0.5f).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0).setNormal(0, 0, 1);
        buffer.addVertex((float)x2, (float)y2, (float)z2).setColor(red, green, blue, alpha)
                .setUv(0.5f, 0.5f).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0).setNormal(0, 0, 1);
        buffer.addVertex((float)x3, (float)y3, (float)z3).setColor(red, green, blue, alpha)
                .setUv(0.5f, 0.5f).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0).setNormal(0, 0, 1);
        buffer.addVertex((float)x4, (float)y4, (float)z4).setColor(red, green, blue, alpha)
                .setUv(0.5f, 0.5f).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0).setNormal(0, 0, 1);
    }
}

