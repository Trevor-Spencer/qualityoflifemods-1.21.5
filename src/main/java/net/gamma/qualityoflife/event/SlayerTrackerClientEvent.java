package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import static net.gamma.qualityoflife.Config.SLAYER_ACTIVE;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;
import static net.gamma.qualityoflife.util.DisplayUtils.drawInfo;
import static net.gamma.qualityoflife.util.SlayerUtils.logMobs;
import static net.gamma.qualityoflife.widget.ManagerWidget.SLAYERWIDGET;

import java.util.*;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SlayerTrackerClientEvent {
    //GENERIC TRACKERS
    private static int bossID = -1;
    private static int nameID = -1;
    private static int spawnedByID = -1;
    private static int timeRemainingID = -1;
    private static boolean bossFound = false;
    //ADVANCED TRACKERS
    private static int skeletonBossID = -1;
    private static int skeletonNameID = -1;
    private static int skeletonTimeRemainingID = -1;
    private static boolean skeletonFound = false;
    private static int piglinBossID = -1;
    private static int piglinNameID = -1;
    private static int piglinTimeRemainingID = -1;
    private static boolean piglinFound = false;
    private static boolean miniFound = false;
    //TRACKERS FOR PROPER MOD USE TIME
    private static boolean trackingBoss = false;
    private static boolean activeQuest = false;
    private static boolean bossSlain = false;
    //TRACKERS FOR WHICH BOSS IS SPAWNED
    private static boolean trackingZombieBoss = false;
    private static boolean trackingSpiderBoss = false;
    private static boolean trackingWolfBoss = false;
    private static boolean trackingEndermanBoss = false;
    private static boolean trackingVampireBoss = false;
    private static boolean trackingBlazeBoss = false;
    //TRACKERS FOR RESETTING
    private static boolean needToResetTrackers = false;
    private static boolean advancedReset = false;
    //Used for slowing processing
    private static final int TRACKCOOLDOWNMAX = 10;
    private static int trackCooldown = 0;

    //Rendering Variables
    private static final int HORIZONTALPADDING = 2;
    private static final int VERTICALPADDING = 2;
    private static final int TEXTCOLOR = 0xFFFFFF;
    private static final int BACKGROUND_COLOR = 0x805C5C5C;


    private static final Map<String, Integer> SLAYERTYPEMAP = Map.of(
            "Revenant Horror", 0,
            "Tarantula Broodfather", 1,
            "Sven Packmaster", 2,
            "Voidgloom Seraph", 3,
            "Riftstalker Bloodfiend", 4,
            "Inferno Demonlord", 5);
    private static final Map<Integer, String> SLAYERTYPEMAPLOOKUP = Map.of(
            0, "Revenant Horror",
            1, "Tarantula Broodfather",
            2, "Sven Packmaster",
            3, "Voidgloom Seraph",
            4, "Riftstalker Bloodfiend",
            5, "Inferno Demonlord");
    private static int slayerType = -1;

    private static void checkSlayer()
    {
       Collection<PlayerTeam> teams = Minecraft.getInstance().level.getScoreboard().getPlayerTeams();
       for(PlayerTeam team : teams)
       {
           String scoreboardLine = String.format("%s%s",team.getPlayerPrefix().getString(), team.getPlayerSuffix().getString());
           for(Map.Entry<String, Integer> entry : SLAYERTYPEMAP.entrySet())
           {
               if(scoreboardLine.contains(entry.getKey()))
               {
                   slayerType = entry.getValue();
                   return;
               }
           }
       }
       slayerType = -1;
    }

    @SubscribeEvent
    private static void bossTracker(ClientTickEvent.Post event)
    {
        if(!SLAYER_ACTIVE.get() || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null){return;}
        if(!onSkyblock){return;}
        if(Minecraft.getInstance().level.isClientSide)
        {
            if(trackCooldown < TRACKCOOLDOWNMAX){trackCooldown++; return;}
            trackCooldown = 0;
            checkSlayer();
            if(slayerType == -1){trackingBoss = false; activeQuest = false; bossSlain = false;return;}
            Collection<PlayerTeam> teams = Minecraft.getInstance().level.getScoreboard().getPlayerTeams();
            for(PlayerTeam team : teams)
            {
                String scoreboardLine = String.format("%s%s",team.getPlayerPrefix().getString(), team.getPlayerSuffix().getString());
                if(scoreboardLine.contains("Slay the boss!"))
                {
                    switch (slayerType)
                    {
                        case 0 : {trackingZombieBoss = true;break;}
                        case 1 : {trackingSpiderBoss = true;break;}
                        case 2 : {trackingWolfBoss = true;break;}
                        case 3 : {trackingEndermanBoss = true;break;}
                        case 4 : {trackingVampireBoss = true;break;}
                        case 5 : {trackingBlazeBoss = true;break;}
                    }
                    trackingBoss = true;
                    break;
                }
                else
                {
                    trackingZombieBoss = false;
                    trackingSpiderBoss = false;
                    trackingWolfBoss = false;
                    trackingEndermanBoss = false;
                    trackingVampireBoss = false;
                    trackingBlazeBoss = false;
                    trackingBoss = false;
                }
            }
            for(PlayerTeam team : teams)
            {
                String scoreboardLine = String.format("%s%s",team.getPlayerPrefix().getString(), team.getPlayerSuffix().getString());
                if(scoreboardLine.contains("Slayer Quest"))
                {
                    activeQuest = true;
                    break;
                }
                else
                {
                    activeQuest = false;
                }
            }
            for(PlayerTeam team : teams)
            {
                String scoreboardLine = String.format("%s%s",team.getPlayerPrefix().getString(), team.getPlayerSuffix().getString());
                if(scoreboardLine.contains("Boss slain!"))
                {
                    bossSlain = true;
                    break;
                }
                else
                {
                    bossSlain = false;
                }
            }
            if(trackingZombieBoss)
            {
                needToResetTrackers = true;
                trackZombieBoss();
            }
            else if(trackingSpiderBoss)
            {
                needToResetTrackers = true;
                trackSpiderBoss();
            }
            else if(trackingWolfBoss)
            {
                needToResetTrackers = true;
                trackWolfBoss();
            }
            else if(trackingEndermanBoss)
            {
                needToResetTrackers = true;
                trackEndermanBoss();
            }
            else if(trackingVampireBoss)
            {
                needToResetTrackers = true;
                trackVampireBoss();
            }
            else if(trackingBlazeBoss)
            {
                needToResetTrackers = true;
                advancedReset = true;
                trackBlazeBoss();
            }
            //If Slayer not active
            if(!trackingBoss)
            {
                if(needToResetTrackers){resetAllTrackers();}
            }
        }
    }

    private static void resetAllTrackers()
    {

        bossID = -1;
        nameID = -1;
        spawnedByID = -1;
        timeRemainingID = -1;
        bossFound = false;
        if(advancedReset)
        {
            skeletonBossID = -1;
            skeletonNameID = -1;
            skeletonTimeRemainingID = -1;
            skeletonFound = false;

            piglinBossID = -1;
            piglinNameID = -1;
            piglinTimeRemainingID = -1;
            piglinFound = false;

            miniFound = false;
        }

        needToResetTrackers = false;
        advancedReset = false;
    }

    private static void trackZombieBoss()
    {
        if(Minecraft.getInstance().player instanceof LocalPlayer player)
        {
            ClientLevel level = Minecraft.getInstance().level;
            if(level.getEntity(nameID) != null && level.getEntity(bossID) != null && level.getEntity(spawnedByID) != null && level.getEntity(timeRemainingID) != null)
            {
                return;
            }

            //RESET ALL TRACKERS
            bossID = -1;
            nameID = -1;
            spawnedByID = -1;
            timeRemainingID = -1;
            bossFound = false;

            AABB area = new AABB(player.getOnPos()).inflate(20);
            List<Entity> nearbyZombie = level.getEntities(
                    player,
                    area,
                    entity -> entity.getType() == EntityType.ZOMBIE);

            for(Entity entityZombie : nearbyZombie)
            {
                int tempNameID = -1;
                int tempSpawnByID = -1;
                int tempTimeRemainingID = -1;
                int tempBossID = entityZombie.getId();

                AABB areaArmorstand = new AABB(entityZombie.blockPosition()).inflate(0d,2d,0d);
                List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                for(Entity entityArmorstand : nearbyArmorStand)
                {
                    Component customName = entityArmorstand.getCustomName();
                    int armorstandID = entityArmorstand.getId();
                    if(customName != null)
                    {
                        if(customName.getString().contains("Revenant Horror"))
                        {
                            tempNameID = armorstandID;
                        }
                        else if(customName.getString().contains("Spawned by: " + player.getName().getString()))
                        {
                            tempSpawnByID = armorstandID;
                        }
                        else if(customName.getString().matches(".*\\d+:\\d+.*"))
                        {
                            tempTimeRemainingID = armorstandID;
                        }
                    }
                }

                if(tempNameID != -1 && tempSpawnByID != -1 && tempTimeRemainingID != -1)
                {
                    bossID = tempBossID;
                    nameID = tempNameID;
                    spawnedByID = tempSpawnByID;
                    timeRemainingID = tempTimeRemainingID;
                    bossFound = true;
                    //LOG BOSS INITIALIZE WITH INFO
                    logMobs(bossID, nameID, spawnedByID, timeRemainingID, false);
                    break;
                }
                nearbyArmorStand.clear();
            }
            nearbyZombie.clear();
        }
    }
    private static void trackSpiderBoss()
    {
        if(Minecraft.getInstance().player instanceof LocalPlayer player)
        {
            ClientLevel level = Minecraft.getInstance().level;
            if(level.getEntity(nameID) != null && level.getEntity(bossID) != null && level.getEntity(spawnedByID) != null && level.getEntity(timeRemainingID) != null)
            {
                return;
            }
            //RESET ALL TRACKERS
            bossID = -1;
            nameID = -1;
            spawnedByID = -1;
            timeRemainingID = -1;
            bossFound = false;

            AABB area = new AABB(player.getOnPos()).inflate(20);
            List<Entity> nearbySpider = level.getEntities(
                    player,
                    area,
                    entity -> entity.getType() == EntityType.SPIDER);

            for(Entity entitySpider : nearbySpider)
            {
                int tempNameID = -1;
                int tempSpawnByID = -1;
                int tempTimeRemainingID = -1;
                int tempBossID = entitySpider.getId();

                AABB areaArmorstand = new AABB(entitySpider.blockPosition()).inflate(0d,2d,0d);
                List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                for(Entity entityArmorstand : nearbyArmorStand)
                {
                    Component customName = entityArmorstand.getCustomName();
                    int armorstandID = entityArmorstand.getId();
                    if(customName != null)
                    {
                        if(customName.getString().contains("Tarantula Broodfather"))
                        {
                            tempNameID = armorstandID;
                        }
                        else if(customName.getString().contains("Spawned by: " + player.getName().getString()))
                        {
                            tempSpawnByID = armorstandID;
                        }
                        else if(customName.getString().matches(".*\\d+:\\d+.*"))
                        {
                            tempTimeRemainingID = armorstandID;
                        }
                    }
                }

                if(tempNameID != -1 && tempSpawnByID != -1 && tempTimeRemainingID != -1)
                {
                    bossID = tempBossID;
                    nameID = tempNameID;
                    spawnedByID = tempSpawnByID;
                    timeRemainingID = tempTimeRemainingID;
                    bossFound = true;
                    //LOG BOSS INITIALIZE WITH INFO
                    logMobs(bossID, nameID, spawnedByID, timeRemainingID, false);
                    break;
                }
                nearbyArmorStand.clear();
            }
            nearbySpider.clear();
        }
    }
    private static void trackWolfBoss()
    {
        if(Minecraft.getInstance().player instanceof LocalPlayer player)
        {
            ClientLevel level = Minecraft.getInstance().level;
            if(level.getEntity(nameID) != null && level.getEntity(bossID) != null && level.getEntity(spawnedByID) != null && level.getEntity(timeRemainingID) != null)
            {
                return;
            }
            //RESET ALL TRACKERS
            bossID = -1;
            nameID = -1;
            spawnedByID = -1;
            timeRemainingID = -1;
            bossFound = false;

            AABB area = new AABB(player.getOnPos()).inflate(20);
            List<Entity> nearbyWolf = level.getEntities(
                    player,
                    area,
                    entity -> entity.getType() == EntityType.WOLF);

            for(Entity entityWolf : nearbyWolf)
            {
                int tempNameID = -1;
                int tempSpawnByID = -1;
                int tempTimeRemainingID = -1;
                int tempBossID = entityWolf.getId();

                AABB areaArmorstand = new AABB(entityWolf.blockPosition()).inflate(0d,2d,0d);
                List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                for(Entity entityArmorstand : nearbyArmorStand)
                {
                    Component customName = entityArmorstand.getCustomName();
                    int armorstandID = entityArmorstand.getId();
                    if(customName != null)
                    {
                        if(customName.getString().contains("Sven Packmaster"))
                        {
                            tempNameID = armorstandID;
                        }
                        else if(customName.getString().contains("Spawned by: " + player.getName().getString()))
                        {
                            tempSpawnByID = armorstandID;
                        }
                        else if(customName.getString().matches(".*\\d+:\\d+.*"))
                        {
                            tempTimeRemainingID = armorstandID;
                        }
                    }
                }

                if(tempNameID != -1 && tempSpawnByID != -1 && tempTimeRemainingID != -1)
                {
                    bossID = tempBossID;
                    nameID = tempNameID;
                    spawnedByID = tempSpawnByID;
                    timeRemainingID = tempTimeRemainingID;
                    bossFound = true;
                    //LOG BOSS INITIALIZE WITH INFO
                    logMobs(bossID, nameID, spawnedByID, timeRemainingID, false);
                    break;
                }
                nearbyArmorStand.clear();
            }
            nearbyWolf.clear();
        }
    }
    private static void trackEndermanBoss()
    {
        if(Minecraft.getInstance().player instanceof LocalPlayer player)
        {
            ClientLevel level = Minecraft.getInstance().level;
            if(level.getEntity(nameID) != null && level.getEntity(bossID) != null && level.getEntity(spawnedByID) != null && level.getEntity(timeRemainingID) != null)
            {
                return;
            }
            //RESET ALL TRACKERS
            bossID = -1;
            nameID = -1;
            spawnedByID = -1;
            timeRemainingID = -1;
            bossFound = false;

            AABB area = new AABB(player.getOnPos()).inflate(20);
            List<Entity> nearbyEnderman = level.getEntities(
                    player,
                    area,
                    entity -> entity.getType() == EntityType.ENDERMAN);

            for(Entity entityEnderman : nearbyEnderman)
            {
                int tempNameID = -1;
                int tempSpawnByID = -1;
                int tempTimeRemainingID = -1;
                int tempBossID = entityEnderman.getId();

                AABB areaArmorstand = new AABB(entityEnderman.blockPosition()).inflate(0d,2d,0d);
                List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                for(Entity entityArmorstand : nearbyArmorStand)
                {
                    Component customName = entityArmorstand.getCustomName();
                    int armorstandID = entityArmorstand.getId();
                    if(customName != null)
                    {
                        if(customName.getString().contains("Voidgloom Seraph"))
                        {
                            tempNameID = armorstandID;
                        }
                        else if(customName.getString().contains("Spawned by: " + player.getName().getString()))
                        {
                            tempSpawnByID = armorstandID;
                        }
                        else if(customName.getString().matches(".*\\d+:\\d+.*"))
                        {
                            tempTimeRemainingID = armorstandID;
                        }
                    }
                }

                if(tempNameID != -1 && tempSpawnByID != -1 && tempTimeRemainingID != -1)
                {
                    bossID = tempBossID;
                    nameID = tempNameID;
                    spawnedByID = tempSpawnByID;
                    timeRemainingID = tempTimeRemainingID;
                    bossFound = true;
                    //LOG BOSS INITIALIZE WITH INFO
                    logMobs(bossID, nameID, spawnedByID, timeRemainingID, false);
                    break;
                }
                nearbyArmorStand.clear();
            }
            nearbyEnderman.clear();
        }
    }
    private static void trackVampireBoss() {}
    private static void trackBlazeBoss()
    {
        if(Minecraft.getInstance().player instanceof LocalPlayer player)
        {
            ClientLevel level = Minecraft.getInstance().level;
            if(level.getEntity(nameID) != null && level.getEntity(bossID) != null && level.getEntity(spawnedByID) != null && level.getEntity(timeRemainingID) != null)
            {
                return;
            }
            if((level.getEntity(skeletonBossID) != null && level.getEntity(skeletonNameID) != null && level.getEntity(skeletonTimeRemainingID) != null) || (level.getEntity(piglinBossID) != null && level.getEntity(piglinNameID) != null && level.getEntity(piglinTimeRemainingID) != null))
            {
                return;
            }

            //RESET ALL TRACKERS
            bossID = -1;
            nameID = -1;
            spawnedByID = -1;
            timeRemainingID = -1;
            bossFound = false;
            miniFound = false;

            skeletonBossID = -1;
            skeletonNameID = -1;
            skeletonTimeRemainingID = -1;
            skeletonFound = false;
            piglinBossID = -1;
            piglinNameID = -1;
            piglinTimeRemainingID = -1;
            piglinFound = false;

            AABB area = new AABB(player.getOnPos()).inflate(20);
            List<Entity> nearbyBlazes = level.getEntities(
                    player,
                    area,
                    entity -> entity.getType() == EntityType.BLAZE);

            for(Entity entityBlaze : nearbyBlazes)
            {
                int tempNameID = -1;
                int tempSpawnByID = -1;
                int tempTimeRemainingID = -1;
                int tempBossID = entityBlaze.getId();

                AABB areaArmorstand = new AABB(entityBlaze.blockPosition()).inflate(0d,2d,0d);
                List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                for(Entity entityArmorstand : nearbyArmorStand)
                {
                    Component customName = entityArmorstand.getCustomName();
                    int armorstandID = entityArmorstand.getId();
                    if(customName != null)
                    {
                        if(customName.getString().contains("Inferno Demonlord"))
                        {
                            tempNameID = armorstandID;
                        }
                        else if(customName.getString().contains("Spawned by: " + player.getName().getString()))
                        {
                            tempSpawnByID = armorstandID;
                        }
                        else if(customName.getString().matches(".*\\d+:\\d+.*"))
                        {
                            tempTimeRemainingID = armorstandID;
                        }
                    }
                }

                if(tempNameID != -1 && tempSpawnByID != -1 && tempTimeRemainingID != -1)
                {
                    bossID = tempBossID;
                    nameID = tempNameID;
                    spawnedByID = tempSpawnByID;
                    timeRemainingID = tempTimeRemainingID;
                    bossFound = true;
                    //LOG BOSS INITIALIZE WITH INFO
                    logMobs(bossID, nameID, spawnedByID, timeRemainingID, false);
                    break;
                }
                nearbyArmorStand.clear();
            }
            nearbyBlazes.clear();
            //NOT IN BLAZE PHASE (miniboss phase)
            if(!bossFound)
            {
                nameID = -1;
                spawnedByID = -1;
                timeRemainingID = -1;

                //RESET ALL TRACKERS
                skeletonBossID = -1;
                skeletonNameID = -1;
                skeletonTimeRemainingID = -1;
                skeletonFound = false;
                AABB areaSkeleton = new AABB(player.getOnPos()).inflate(20);
                List<Entity> nearbySkeletons = level.getEntities(
                        player,
                        areaSkeleton,
                        entity -> entity.getType() == EntityType.WITHER_SKELETON);

                for(Entity entitySkeleton : nearbySkeletons)
                {
                    int tempNameID = -1;
                    int tempTimeRemainingID = -1;
                    int tempBossID = entitySkeleton.getId();

                    AABB areaArmorstand = new AABB(entitySkeleton.blockPosition()).inflate(0d,3d,0d);
                    List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                    for(Entity entityArmorstand : nearbyArmorStand)
                    {
                        Component customName = entityArmorstand.getCustomName();
                        int armorstandID = entityArmorstand.getId();
                        if(customName != null)
                        {
                            if(customName.getString().contains("ⓆⓊⒶⓏⒾⒾ"))
                            {
                                tempNameID = armorstandID;
                            }
                            else if(customName.getString().matches(".*\\d+:\\d+.*"))
                            {
                                tempTimeRemainingID = armorstandID;
                            }
                        }
                    }

                    if(tempNameID != -1 && tempTimeRemainingID != -1)
                    {
                        skeletonBossID = tempBossID;
                        skeletonNameID = tempNameID;
                        skeletonTimeRemainingID = tempTimeRemainingID;
                        skeletonFound = true;
                        miniFound = true;
                        //LOG BOSS INITIALIZE WITH INFO
                        logMobs(skeletonBossID, skeletonNameID, -1, skeletonTimeRemainingID, true);
                        break;
                    }
                    nearbyArmorStand.clear();
                }
                nearbySkeletons.clear();

                //RESET ALL TRACKERS
                piglinBossID = -1;
                piglinNameID = -1;
                piglinTimeRemainingID = -1;
                piglinFound = false;

                AABB areaPiglin = new AABB(player.getOnPos()).inflate(20);
                List<Entity> nearbyZombifiedPiglin = level.getEntities(
                        player,
                        areaPiglin,
                        entity -> entity.getType() == EntityType.ZOMBIFIED_PIGLIN);

                for(Entity entityPiglin : nearbyZombifiedPiglin)
                {
                    int tempNameID = -1;
                    int tempTimeRemainingID = -1;
                    int tempBossID = entityPiglin.getId();

                    AABB areaArmorstand = new AABB(entityPiglin.blockPosition()).inflate(0d,3d,0d);
                    List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                    for(Entity entityArmorstand : nearbyArmorStand)
                    {
                        Component customName = entityArmorstand.getCustomName();
                        int armorstandID = entityArmorstand.getId();
                        if(customName != null)
                        {
                            if(customName.getString().contains("ⓉⓎⓅⒽⓄⒺⓊⓈ"))
                            {
                                tempNameID = armorstandID;
                            }
                            else if(customName.getString().matches(".*\\d+:\\d+.*"))
                            {
                                tempTimeRemainingID = armorstandID;
                            }
                        }
                    }

                    if(tempNameID != -1 && tempTimeRemainingID != -1)
                    {
                        piglinBossID = tempBossID;
                        piglinNameID = tempNameID;
                        piglinTimeRemainingID = tempTimeRemainingID;
                        piglinFound = true;
                        miniFound = true;
                        //LOG BOSS INITIALIZE WITH INFO
                        logMobs(piglinBossID, piglinNameID, -1, piglinTimeRemainingID, true);
                        break;
                    }
                    nearbyArmorStand.clear();
                }
                nearbyZombifiedPiglin.clear();
            }
        }
    }

    @SubscribeEvent
    private static void renderGui(RenderGuiEvent.Post event)
    {
        if(!SLAYER_ACTIVE.get() || Minecraft.getInstance().level == null){return;}
        if(!onSkyblock){return;}
        GuiGraphics graphics = event.getGuiGraphics();
        String title = String.format("Slayer Quest For %s", SLAYERTYPEMAPLOOKUP.get(slayerType));
        String ending = "Boss Slain";

        if(activeQuest && !bossSlain && !trackingBoss)
        {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            List<String> strings = List.of(title);

            drawInfo(graphics,
                    screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                    SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                    Minecraft.getInstance().font, List.of(), BACKGROUND_COLOR, false, true);

            drawInfo(graphics,
                    screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                    SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                    Minecraft.getInstance().font, strings, TEXTCOLOR, false, true);
        }
        if(activeQuest && bossSlain && !trackingBoss)
        {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            List<String> strings = List.of(title, ending);

            drawInfo(graphics,
                    screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                    SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                    Minecraft.getInstance().font, List.of(), BACKGROUND_COLOR, false, true);

            drawInfo(graphics,
                    screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                    SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                    Minecraft.getInstance().font, strings, TEXTCOLOR, false, true);
        }
        if(activeQuest &&!bossSlain && trackingBoss)
        {

            if(bossFound)
            {
                String name;
                String time;
                String spawned;
                String type;
                if(Minecraft.getInstance().level.getEntity(nameID) != null)
                {
                    if(Minecraft.getInstance().level.getEntity(nameID).getCustomName() != null)
                    {
                        name = String.format("Name: %s", Minecraft.getInstance().level.getEntity(nameID).getCustomName().getString());
                    }
                    else
                    {
                        name = "Name:";
                    }
                }
                else
                {
                    name = "Name:";
                }
                if(Minecraft.getInstance().level.getEntity(timeRemainingID) != null)
                {
                    if(Minecraft.getInstance().level.getEntity(timeRemainingID).getCustomName() != null)
                    {
                        time = String.format("Time: %s", Minecraft.getInstance().level.getEntity(timeRemainingID).getCustomName().getString());
                    }
                    else
                    {
                        time = "Time:";
                    }
                }
                else
                {
                    time = "Time:";
                }
                if(Minecraft.getInstance().level.getEntity(spawnedByID) != null)
                {
                    if(Minecraft.getInstance().level.getEntity(spawnedByID).getCustomName() != null)
                    {
                        spawned = String.format("Spawned By: %s", Minecraft.getInstance().level.getEntity(spawnedByID).getCustomName().getString().split(": ")[1]);
                    }
                    else
                    {
                        spawned = "Spawned By:";
                    }
                }
                else
                {
                    spawned = "Spawned By:";
                }
                type = "Boss Tracking";

                int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                List<String> strings = List.of(title, name, time, spawned, type);

                drawInfo(graphics,
                        screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                        SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                        Minecraft.getInstance().font, List.of(), BACKGROUND_COLOR, false, true);

                drawInfo(graphics,
                        screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                        SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                        Minecraft.getInstance().font, strings, TEXTCOLOR, false, true);
            }
            else if(miniFound)
            {
                String nameSkeleton = "Name:";
                String namePiglin = "Name:";
                String time = "Time:";
                String type;
                if(skeletonFound)
                {
                    Entity skeletonName = Minecraft.getInstance().level.getEntity(skeletonNameID);
                    Entity skeletonTime = Minecraft.getInstance().level.getEntity(skeletonTimeRemainingID);
                    if(skeletonName != null)
                    {
                        Component name = skeletonName.getCustomName();
                        if(name != null)
                        {
                            nameSkeleton = String.format("Name: %s", name.getString());
                        }
                        else
                        {
                            nameSkeleton = "Name:";
                        }
                    }
                    else
                    {
                        nameSkeleton = "Name:";
                    }
                    if(skeletonTime != null)
                    {
                        Component name = skeletonTime.getCustomName();
                        if(name != null)
                        {
                            time = String.format("Time: %s", name.getString());
                        }
                        else
                        {
                            time = "Time:";
                        }
                    }
                    else
                    {
                        time = "Time:";
                    }
                }

                if(piglinFound)
                {
                    Entity piglinName = Minecraft.getInstance().level.getEntity(piglinNameID);
                    Entity piglinTime = Minecraft.getInstance().level.getEntity(piglinTimeRemainingID);
                    if(piglinName != null)
                    {
                        Component name = piglinName.getCustomName();
                        if(name != null)
                        {
                            namePiglin = String.format("Name: %s", name.getString());
                        }
                        else
                        {
                            namePiglin = "Name:";
                        }
                    }
                    else
                    {
                        namePiglin = "Name:";
                    }
                    if(piglinTime != null)
                    {
                        Component name = piglinTime.getCustomName();
                        if(name != null)
                        {
                            time = String.format("Time: %s", name.getString());
                        }
                        else
                        {
                            time = "Time:";
                        }
                    }
                    else
                    {
                        time = "Time:";
                    }
                }
                type = "Mini Tracking";

                int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                List<String> strings = List.of(title, nameSkeleton, namePiglin, time, type);

                drawInfo(graphics,
                        screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                        SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                        Minecraft.getInstance().font, List.of(), BACKGROUND_COLOR, false, true);

                drawInfo(graphics,
                        screenWidth, screenHeight, SLAYERWIDGET.normalizedX, SLAYERWIDGET.normalizedY,
                        SLAYERWIDGET.normalizedWidth, SLAYERWIDGET.normalizedHeight, HORIZONTALPADDING, VERTICALPADDING,
                        Minecraft.getInstance().font, strings, TEXTCOLOR, false, true);
            }
        }
    }
}
