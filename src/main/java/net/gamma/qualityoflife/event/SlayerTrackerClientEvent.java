package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import static net.gamma.qualityoflife.Config.SLAYER_ACTIVE;
import static net.gamma.qualityoflife.util.SlayerUtils.checkOnSkyblock;
import static net.gamma.qualityoflife.util.SlayerUtils.logMobs;

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
    private static int TRACKCOOLDOWNMAX = 10;
    private static int trackCooldown = 0;
    //Read scoreboard object when world switch
    public static boolean updateWorld = false;

    //Rendering Variables
    public static int x = 20;
    public static int y = 50;
    private static int widthPadding = 2;
    private static int heightPadding = 2;
    public static int displayWidth = 100;
    public static int displayHeight = 50;

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
    private static void joinWorld(EntityJoinLevelEvent event)
    {
        if(!event.getLevel().isClientSide){return;}
        if(event.getEntity() instanceof LocalPlayer)
        {
            updateWorld = true;
        }
    }
    @SubscribeEvent
    private static void bossTracker(ClientTickEvent.Post event)
    {
        if(!SLAYER_ACTIVE.get() || Minecraft.getInstance().player == null){return;}
        if(Minecraft.getInstance().level.isClientSide)
        {
            if(updateWorld){checkOnSkyblock();}
            if(trackCooldown < TRACKCOOLDOWNMAX){trackCooldown++; return;}
            trackCooldown = 0;
            if(!checkOnSkyblock()) {return;}
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
                        case 0 : {trackingZombieBoss = true;}
                        case 1 : {trackingSpiderBoss = true;}
                        case 2 : {trackingWolfBoss = true;}
                        case 3 : {trackingEndermanBoss = true;}
                        case 4 : {trackingVampireBoss = true;}
                        case 5 : {trackingBlazeBoss = true;}
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
        if(!checkOnSkyblock()){return;}
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;
        int colorText = 0xFFFFFF;
        int colorBackground = 0x805C5C5C;
        int height = Minecraft.getInstance().font.lineHeight;
        String title = String.format("Slayer Quest For %s", SLAYERTYPEMAPLOOKUP.get(slayerType));
        String ending = "Boss Slain";
        if(activeQuest && !bossSlain && !trackingBoss)
        {
            graphics.fill(x, y, x + displayWidth, y + displayHeight, colorBackground);
            //Calculate scale
            float scaleWidth = 1.0f;
            float scaleHeight = 1.0f;
            List<Integer> textWidths = List.of(font.width(title)+(widthPadding*2));
            int textHeight = 1 * font.lineHeight + (heightPadding*2);
            int maxWidth = textWidths.stream().max(Integer::compareTo).orElse(0);
            if(textHeight > displayHeight)
            {
                scaleHeight = (float) displayHeight / textHeight;
            }
            if(maxWidth > displayWidth)
            {
                scaleWidth = (float) displayWidth / maxWidth;
            }
            float scale = Math.min(scaleWidth, scaleHeight);
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(font, title, (x + widthPadding)/scale, (y + heightPadding + (height*0))/scale, colorText, false);
            graphics.pose().popPose();
        }
        if(activeQuest && bossSlain && !trackingBoss)
        {
            graphics.fill(x, y, x + displayWidth, y + displayHeight, colorBackground);
            //Calculate scale
            float scaleWidth = 1.0f;
            float scaleHeight = 1.0f;
            List<Integer> textWidths = List.of(font.width(title)+(widthPadding*2), font.width(ending)+(widthPadding*2));
            int textHeight = 2 * font.lineHeight + (heightPadding*2);
            int maxWidth = textWidths.stream().max(Integer::compareTo).orElse(0);
            if(textHeight > displayHeight)
            {
                scaleHeight = (float) displayHeight / textHeight;
            }
            if(maxWidth > displayWidth)
            {
                scaleWidth = (float) displayWidth / maxWidth;
            }
            float scale = Math.min(scaleWidth, scaleHeight);
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(font, title, (x + widthPadding)/scale, (y + heightPadding + (height*0))/scale, colorText, false);
            graphics.drawString(font, ending, (x + widthPadding)/scale, (y + heightPadding + (height*1))/scale, colorText, false);
            graphics.pose().popPose();
        }
        if(activeQuest &&!bossSlain && trackingBoss)
        {
            graphics.fill(x, y, x + displayWidth, y + displayHeight, colorBackground);
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

                //Calculate scale
                float scaleWidth = 1.0f;
                float scaleHeight = 1.0f;
                List<Integer> textWidths = List.of(font.width(title)+(widthPadding*2),font.width(name)+(widthPadding*2),font.width(time)+(widthPadding*2),font.width(spawned)+(widthPadding*2),font.width(type)+(widthPadding*2));
                int textHeight = 5 * font.lineHeight + (heightPadding*2);
                int maxWidth = textWidths.stream().max(Integer::compareTo).orElse(0);
                if(textHeight > displayHeight)
                {
                    scaleHeight = (float) displayHeight / textHeight;
                }
                if(maxWidth > displayWidth)
                {
                    scaleWidth = (float) displayWidth / maxWidth;
                }
                float scale = Math.min(scaleWidth, scaleHeight);
                graphics.pose().pushPose();
                graphics.pose().scale(scale, scale, 1.0f);
                graphics.drawString(font, title, (x + widthPadding)/scale, (y + heightPadding + (height*0))/scale, colorText, false);
                graphics.drawString(font, name, (x + widthPadding)/scale, (y + heightPadding + (height*1))/scale, colorText, false);
                graphics.drawString(font, time, (x + widthPadding)/scale, (y + heightPadding + (height*2))/scale, colorText, false);
                graphics.drawString(font, spawned, (x + widthPadding)/scale, (y + heightPadding + (height*3))/scale, colorText, false);
                graphics.drawString(font, type, (x + widthPadding)/scale, (y + heightPadding + (height*4))/scale, colorText, false);
                graphics.pose().popPose();
            }
            else if(miniFound)
            {
                String nameSkeleton = "Name:";
                String namePiglin = "Name:";
                String time = "Time:";
                String type;
                if(skeletonFound)
                {
                    if(Minecraft.getInstance().level.getEntity(skeletonNameID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(skeletonNameID).getCustomName() != null)
                        {
                            nameSkeleton = String.format("Name: %s", Minecraft.getInstance().level.getEntity(skeletonNameID).getCustomName().getString());
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
                    if(Minecraft.getInstance().level.getEntity(skeletonTimeRemainingID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(skeletonTimeRemainingID).getCustomName() != null)
                        {
                            time = String.format("Time: %s", Minecraft.getInstance().level.getEntity(skeletonTimeRemainingID).getCustomName().getString());
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
                    if(Minecraft.getInstance().level.getEntity(piglinNameID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(piglinNameID).getCustomName() != null)
                        {
                            namePiglin = String.format("Name: %s", Minecraft.getInstance().level.getEntity(piglinNameID).getCustomName().getString());
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
                    if(Minecraft.getInstance().level.getEntity(piglinTimeRemainingID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(piglinTimeRemainingID).getCustomName() != null)
                        {
                            time = String.format("Time: %s", Minecraft.getInstance().level.getEntity(piglinTimeRemainingID).getCustomName().getString());
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

                //Calculate scale
                float scaleWidth = 1.0f;
                float scaleHeight = 1.0f;
                List<Integer> textWidths = List.of(font.width(title)+(widthPadding*2),font.width(nameSkeleton)+(widthPadding*2),font.width(namePiglin)+(widthPadding*2),font.width(time)+(widthPadding*2),font.width(type)+(widthPadding*2));
                int textHeight = 5 * font.lineHeight + (heightPadding*2);
                int maxWidth = textWidths.stream().max(Integer::compareTo).orElse(0);
                if(textHeight > displayHeight)
                {
                    scaleHeight = (float) displayHeight / textHeight;
                }
                if(maxWidth > displayWidth)
                {
                    scaleWidth = (float) displayWidth / maxWidth;
                }
                float scale = Math.min(scaleWidth, scaleHeight);
                graphics.pose().pushPose();
                graphics.pose().scale(scale, scale, 1.0f);
                graphics.drawString(font, title, (x + widthPadding)/scale, (y + heightPadding + (height*0))/scale, colorText, false);
                graphics.drawString(font, nameSkeleton, (x + widthPadding)/scale, (y + heightPadding + (height*1))/scale, colorText, false);
                graphics.drawString(font, namePiglin, (x + widthPadding)/scale, (y + heightPadding + (height*2))/scale, colorText, false);
                graphics.drawString(font, time, (x + widthPadding)/scale, (y + heightPadding + (height*3))/scale, colorText, false);
                graphics.drawString(font, type, (x + widthPadding)/scale, (y + heightPadding + (height*4))/scale, colorText, false);
                graphics.pose().popPose();
            }
        }
    }
}
